package com.sanjey.codestride.data.repository

import android.util.Log
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import com.google.gson.Gson
import com.sanjey.codestride.common.Constants
import com.sanjey.codestride.data.model.RoadmapItem
import com.sanjey.codestride.data.model.ai.AiRequest
import com.sanjey.codestride.data.model.ai.Message
import com.sanjey.codestride.data.remote.AiApiService
import com.sanjey.codestride.data.remote.YouTubeApiClient
import com.sanjey.codestride.data.remote.YouTubeResponse
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.tasks.await
import retrofit2.Call
import javax.inject.Inject

class AiGenerationRepository @Inject constructor(
    private val api: AiApiService
) {
    private suspend fun generateRoadmap(topic: String): List<RoadmapItem> {
        val prompt = """
        You are an expert curriculum designer.

        Create a 10-step beginner-friendly roadmap for learning "$topic".

        Each step must:
        - Have a short, specific, and unique title (use Title Case, max 6 words)
        - Be logically ordered from beginner to advanced
        - Clearly define a learning milestone

        Each step should include these 4 fields:
        - "title": A concise module name
        - "description": What the learner will achieve
        - "link": A YouTube video URL (use "https://www.youtube.com/watch?v=xxxxx" if unsure)
        - "html_content": 2 to 4 paragraphs of clean HTML using <h2>, <p>, <ul>, <pre><code>...</code></pre>

        Important:
        - All content must be beginner-friendly and self-contained
        - Focus on teaching concepts clearly
        - Format response as pure JSON array with exactly 10 items
    """.trimIndent()

        Log.d("AI_DEBUG", "🟡 Starting AI roadmap generation for topic: $topic")
        Log.d("AI_DEBUG", "📝 Prompt size = ${prompt.length} characters")

        val startTime = System.currentTimeMillis()

        return try {
            val response = api.getAiRoadmap(
                AiRequest(
                    messages = listOf(Message(content = prompt))
                )
            )

            val duration = System.currentTimeMillis() - startTime
            Log.d("AI_DEBUG", "✅ OpenAI response received in ${duration}ms")

            val innerJson = response.choices.firstOrNull()?.message?.content

            if (innerJson == null) {
                Log.e("AI_DEBUG", "❌ AI returned null content")
                return emptyList()
            }

            Log.d("AI_RAW_JSON", "🔥 Raw AI response:\n$innerJson")

            var cleanedJson = innerJson.trim()

            if (cleanedJson.startsWith("```json")) {
                cleanedJson = cleanedJson.removePrefix("```json").trim()
            }
            if (cleanedJson.endsWith("```")) {
                cleanedJson = cleanedJson.removeSuffix("```").trim()
            }

            Log.d("AI_RAW_JSON", "✅ Cleaned JSON:\n$cleanedJson")

            val parsed = Gson().fromJson(cleanedJson, Array<RoadmapItem>::class.java).toList()
            Log.d("AI_DEBUG", "✅ Parsed ${parsed.size} modules successfully")

            return parsed



        } catch (e: Exception) {
            val duration = System.currentTimeMillis() - startTime
            Log.e("AI_ERROR", "❌ Failed to generate roadmap: ${e.message} (after ${duration}ms)")
            emptyList()
        }
    }


    suspend fun generateAndStoreRoadmap(
        topic: String,
        roadmapId: String,
        onComplete: (Boolean) -> Unit
    ) {
        Log.d("AI_MODULE_UPLOAD", "🚀 Generating roadmap for topic: $topic")

        val modules = generateRoadmap(topic)

        if (modules.isEmpty()) {
            Log.e("AI_MODULE_UPLOAD", "❌ No modules generated for topic: $topic")
            onComplete(false)
            return
        }

        val firestore = Firebase.firestore

        modules.forEachIndexed { index, module ->
            val moduleId = "module${index + 1}"
            val title = module.title
            val description = module.description
            val html = module.html_content
            val placeholder = "https://www.youtube.com/watch?v=xxxxx"

            Log.d("AI_MODULE_UPLOAD", "🔹 Processing $moduleId → $title")

            val finalUrl = try {
                if (module.link == placeholder) {
                    val query = "$topic $title"
                    Log.d("YOUTUBE_FETCH", "🔍 Searching YouTube with query: $query")

                    fetchYouTubeUrlSuspend(query)
                        ?: "https://www.youtube.com/results?search_query=${query.replace(" ", "+")}".also {
                            Log.w("YOUTUBE_FETCH", "⚠️ Fallback YouTube search URL used for $query")
                        }
                } else {
                    Log.d("YOUTUBE_FETCH", "✅ Using provided link for $title: ${module.link}")
                    module.link
                }
            } catch (e: Exception) {
                Log.e("YOUTUBE_FETCH", "❌ Error fetching YouTube link for $title: ${e.message}")
                "https://www.youtube.com/results?search_query=${("$topic $title").replace(" ", "+")}"
            }

            val moduleData = hashMapOf(
                "title" to title,
                "description" to description,
                "custom_content" to html,
                "yt_url" to finalUrl,
                "order" to index + 1,
                "quiz_id" to ""
            )

            try {
                firestore.collection("ai_roadmaps")
                    .document(roadmapId)
                    .collection("modules")
                    .document(moduleId)
                    .set(moduleData)
                    .await()

                Log.d("AI_MODULE_UPLOAD", "✅ Uploaded $moduleId → $title")
            } catch (e: Exception) {
                Log.e("AI_MODULE_UPLOAD", "❌ Failed to upload $moduleId: ${e.message}")
            }
        }

        Log.d("AI_MODULE_UPLOAD", "🎉 Finished uploading all modules for $roadmapId")
        onComplete(true)
    }



    @OptIn(ExperimentalCoroutinesApi::class)
    private suspend fun fetchYouTubeUrlSuspend(query: String): String? =
        suspendCancellableCoroutine { cont ->
            val call = YouTubeApiClient.retrofit.searchVideos(
                query = "$query tutorial",
                apiKey = Constants.YOUTUBE_API_KEY
            )

            call.enqueue(object : retrofit2.Callback<YouTubeResponse> {
                override fun onResponse(call: Call<YouTubeResponse>, response: retrofit2.Response<YouTubeResponse>) {
                    val videoId = response.body()?.items?.firstOrNull()?.id?.videoId
                    cont.resume(
                        videoId?.let { "https://www.youtube.com/watch?v=$it" },
                        null
                    )
                }

                override fun onFailure(call: Call<YouTubeResponse>, t: Throwable) {
                    cont.resume(null, null)
                }
            })
        }
}
