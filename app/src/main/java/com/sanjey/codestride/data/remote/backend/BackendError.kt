package com.sanjey.codestride.data.remote.backend

import com.google.gson.Gson
import com.sanjey.codestride.data.model.backend.ErrorBody
import com.sanjey.codestride.data.model.backend.ErrorResponse
import kotlinx.coroutines.CancellationException
import retrofit2.HttpException
import java.io.IOException
import java.net.SocketTimeoutException
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

/**
 * Every way a Worker call can fail, with a message that's ready to show the user.
 * Repositories return Result<T>; on failure the exception is one of these.
 */
sealed class BackendError(val userMessage: String) : Exception(userMessage) {

    /** 429 - daily limit for this feature reached. */
    class RateLimited(val feature: String?, val limit: Int?, val resetAt: String?) :
        BackendError("You've reached today's limit for this feature. It resets at ${formatResetTime(resetAt)}.")

    /** 400 content_flagged - moderation blocked the text. */
    class ContentFlagged :
        BackendError("⚠️ Your message was blocked because it may contain inappropriate content.")

    /** 502 / 5xx / timeout - OpenAI or the Worker had a problem. Worth retrying later. */
    class Busy :
        BackendError("AI is busy right now, please try again in a moment.")

    /** 401 - no valid Firebase sign-in. */
    class SignedOut :
        BackendError("Please sign in again to use AI features.")

    /** No connection at all. */
    class NoInternet :
        BackendError("No internet connection. Please check your connection and try again.")

    /** 400 bad_request - the Worker rejected the input (e.g. text too long). */
    class BadRequest(detail: String?) :
        BackendError("Couldn't send that: ${detail ?: "invalid request"}")

    /** Anything unexpected. */
    class Unknown(cause: Throwable?) :
        BackendError("Something went wrong. Please try again.") {
        init { cause?.let { initCause(it) } }
    }
}

/**
 * Runs a Worker call and wraps the outcome:
 *   success -> Result.success(value)
 *   failure -> Result.failure(BackendError)
 *
 * Usage in a repository:
 *   suspend fun chat(...): Result<String> = backendCall { api.chat(...).reply }
 */
suspend fun <T> backendCall(block: suspend () -> T): Result<T> =
    try {
        Result.success(block())
    } catch (e: CancellationException) {
        throw e // the screen was closed - let coroutines cancel normally, don't treat as an error
    } catch (e: Throwable) {
        Result.failure(e.toBackendError())
    }

/** Turns any exception from Retrofit/OkHttp into a BackendError. */
fun Throwable.toBackendError(): BackendError = when (this) {
    is BackendError -> this
    is HttpException -> fromHttp(this)
    is SocketTimeoutException -> BackendError.Busy()   // waited too long for the AI
    is IOException -> BackendError.NoInternet()        // no connection, DNS failure, etc.
    else -> BackendError.Unknown(this)
}

private val gson = Gson()

/** Reads the Worker's {"error": {...}} body and picks the matching error. */
private fun fromHttp(e: HttpException): BackendError {
    val body: ErrorBody? = try {
        e.response()?.errorBody()?.string()?.let { gson.fromJson(it, ErrorResponse::class.java)?.error }
    } catch (_: Exception) {
        null // body wasn't our JSON (e.g. a Cloudflare error page) - fall back to the status code
    }

    // 1) Prefer the Worker's error code
    when (body?.code) {
        "rate_limited" -> return BackendError.RateLimited(body.feature, body.limit, body.resetAt)
        "content_flagged" -> return BackendError.ContentFlagged()
        "bad_request" -> return BackendError.BadRequest(body.message)
        "missing_token", "invalid_token", "token_expired" -> return BackendError.SignedOut()
        "upstream_error", "internal_error" -> return BackendError.Busy()
    }

    // 2) Otherwise fall back to the HTTP status
    return when (e.code()) {
        401 -> BackendError.SignedOut()
        429 -> BackendError.RateLimited(null, null, null)
        400 -> BackendError.BadRequest(body?.message)
        in 500..599 -> BackendError.Busy()
        else -> BackendError.Unknown(e)
    }
}

/** "2026-10-09T00:00:00.000Z" -> "1:00 AM" in the phone's time zone. */
private fun formatResetTime(resetAt: String?): String =
    try {
        val instant = Instant.parse(resetAt)
        DateTimeFormatter.ofPattern("h:mm a").withZone(ZoneId.systemDefault()).format(instant)
    } catch (_: Exception) {
        "midnight UTC"
    }