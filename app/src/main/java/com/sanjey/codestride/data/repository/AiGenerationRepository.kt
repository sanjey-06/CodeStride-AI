package com.sanjey.codestride.data.repository

import android.util.Log
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import com.sanjey.codestride.BuildConfig
import com.sanjey.codestride.common.Constants
import com.sanjey.codestride.data.model.Question
import com.sanjey.codestride.data.model.RoadmapItem
import com.sanjey.codestride.data.remote.AiApiService
import com.sanjey.codestride.data.remote.YouTubeApiClient
import com.sanjey.codestride.data.remote.YouTubeResponse
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.tasks.await
import retrofit2.Call
import javax.inject.Inject
import com.sanjey.codestride.data.model.backend.RoadmapRequest
import com.sanjey.codestride.data.remote.backend.CodeStrideApi
import com.sanjey.codestride.data.remote.backend.backendCall
import com.sanjey.codestride.data.model.backend.ModuleRequest

class AiGenerationRepository @Inject constructor(
    private val backend: CodeStrideApi
) {
    /**
     * Asks the Worker for a 10-step roadmap (POST /roadmap).
     * The Worker handles moderation, the daily limit, the prompt and JSON checking.
     * Every step's link is the YouTube placeholder, so storeRoadmapModules() searches YouTube.
     */
    suspend fun fetchAiRoadmap(topic: String): Result<List<RoadmapItem>> = backendCall {
        backend.roadmap(RoadmapRequest(topic)).items
    }

    /**
     * Asks the Worker for a module's HTML tutorial (POST /module-content).
     * Same prompt as before - it now lives in the Worker.
     */
    suspend fun generateModuleContent(topic: String, moduleTitle: String): Result<String> = backendCall {
        backend.moduleContent(ModuleRequest(topic, moduleTitle)).html
    }


    suspend fun storeRoadmapModules(
        topic: String,
        roadmapId: String,
        modules: List<RoadmapItem>
    ) {
        val firestore = Firebase.firestore

        modules.forEachIndexed { index, module ->
            val moduleId = "module${index + 1}"
            val title = module.title
            val description = module.description
            val placeholder = "https://www.youtube.com/watch?v=xxxxx"

           if (BuildConfig.DEBUG) {
                Log.d("AI_MODULE_UPLOAD", "🔹 Processing $moduleId → $title")
            }

            val finalUrl = try {
                if (module.link == placeholder) {
                    val query = "$topic $title"
                    if (BuildConfig.DEBUG) {
                        Log.d("YOUTUBE_FETCH", "🔍 Searching YouTube with query: $query")
                    }

                    fetchYouTubeUrlSuspend(query)
                        ?: "https://www.youtube.com/results?search_query=${query.replace(" ", "+")}".also {
                            if (BuildConfig.DEBUG) {
                                Log.w("YOUTUBE_FETCH", "⚠️ Fallback YouTube search URL used for $query")
                            }
                        }
                } else {
                    module.link
                }
            } catch (e: Exception) {
                if (BuildConfig.DEBUG) {
                    Log.e("YOUTUBE_FETCH", "❌ Error fetching YouTube link for $title: ${e.message}")
                }
                "https://www.youtube.com/results?search_query=${("$topic $title").replace(" ", "+")}"
            }

            val moduleData = hashMapOf(
                "title" to title,
                "description" to description,
                "custom_content" to "",
                "yt_url" to finalUrl,
                "order" to index + 1,
                "quiz_id" to module.quizId.ifBlank { "ai_quiz_${index + 1}" }
            )

            try {
                firestore.collection("ai_roadmaps")
                    .document(roadmapId)
                    .collection("modules")
                    .document(moduleId)
                    .set(moduleData)
                    .await()

                if (BuildConfig.DEBUG) {
                    Log.d("AI_MODULE_UPLOAD", "✅ Uploaded $moduleId → $title")
                }
            } catch (e: Exception) {
                if (BuildConfig.DEBUG) {
                    Log.e("AI_MODULE_UPLOAD", "❌ Failed to upload $moduleId: ${e.message}")
                }
            }
        }
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


    /**
     * Asks the Worker for a 5-question quiz (POST /quiz).
     * The Worker checks every question: exactly 4 options, and correct_answer is one of them.
     */
    suspend fun generateQuiz(topic: String, moduleTitle: String): Result<List<Question>> = backendCall {
        backend.quiz(ModuleRequest(topic, moduleTitle)).questions
    }

}
