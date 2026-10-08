package com.sanjey.codestride.data.remote.backend

import com.google.android.gms.tasks.Tasks
import com.google.firebase.auth.FirebaseAuth
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Adds "Authorization: Bearer <Firebase ID token>" to every request to the CodeStride Worker.
 *
 * If the Worker says the token expired (401 token_expired), it gets a fresh token
 * from Firebase and retries the request ONCE.
 *
 * Runs on OkHttp's background threads, so waiting for Firebase here is safe
 * (never on the main thread).
 */
@Singleton
class FirebaseAuthInterceptor @Inject constructor(
    private val auth: FirebaseAuth
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val original = chain.request()

        // Not signed in: send without a token. The Worker answers 401 missing_token.
        val token = fetchToken(forceRefresh = false)
            ?: return chain.proceed(original)

        val response = chain.proceed(original.withToken(token))

        // Only retry when the Worker says the token EXPIRED (not for fake/missing tokens).
        if (response.code == 401 && isTokenExpired(response)) {
            response.close() // must close the old response before making another call
            val fresh = fetchToken(forceRefresh = true) ?: return chain.proceed(original)
            return chain.proceed(original.withToken(fresh))
        }
        return response
    }

    /**
     * Gets the signed-in user's ID token.
     * forceRefresh = false -> Firebase's cached token (refreshed automatically when close to expiry)
     * forceRefresh = true  -> always ask Firebase for a brand-new one
     */
    private fun fetchToken(forceRefresh: Boolean): String? {
        val user = auth.currentUser ?: return null
        return try {
            Tasks.await(user.getIdToken(forceRefresh), 10, TimeUnit.SECONDS).token
        } catch (e: Exception) {
            null // no network, Firebase timeout, etc. -> request goes without a token -> 401
        }
    }

    /** Reads the error code WITHOUT consuming the body (peekBody makes a copy). */
    private fun isTokenExpired(response: Response): Boolean =
        response.peekBody(1024).string().contains("\"token_expired\"")

    private fun Request.withToken(token: String): Request =
        newBuilder().header("Authorization", "Bearer $token").build()
}