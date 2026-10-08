package com.sanjey.codestride.data.repository

import android.util.Log
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import com.google.gson.Gson
import com.sanjey.codestride.BuildConfig
import com.sanjey.codestride.common.Constants
import com.sanjey.codestride.data.model.Question
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
import com.sanjey.codestride.data.model.backend.RoadmapRequest
import com.sanjey.codestride.data.remote.backend.CodeStrideApi
import com.sanjey.codestride.data.remote.backend.backendCall

class AiGenerationRepository @Inject constructor(
    private val api: AiApiService,
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

    suspend fun generateModuleContent(topic: String, moduleTitle: String): String {
        val prompt = """
You are an expert curriculum designer and HTML writer.

Write a rich HTML tutorial for the topic: "$moduleTitle" as part of the "$topic" learning roadmap.

The tutorial must include:

- A <h2> title
- 3 to 5 <p> paragraphs explaining the concept clearly
- One <ul><li></li></ul> list (tools, tips, features)
- One <pre><code> code block with comments
- Use <b> and <i> tags for emphasis
- Include a real-world analogy if helpful

Format the output as a single HTML string (no JSON, no markdown).
""".trimIndent()

        return try {
            val response = api.getAiRoadmap(
                AiRequest(
                    model = "gpt-3.5-turbo",
                    messages = listOf(Message(content = prompt)),
                    max_tokens = 1500,
                    temperature = 0.7
                )
            )
            response.choices.firstOrNull()?.message?.content?.trim().orEmpty()
        } catch (e: Exception) {
            ""
        }
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


    suspend fun generateQuiz(topic: String, moduleTitle: String): List<Question> {
        if (BuildConfig.DEBUG) {
            Log.d(
                "QUIZ_DEBUG",
                "🚀 Starting AI quiz generation → topic=$topic, moduleTitle=$moduleTitle"
            )
        }

        val prompt = """
You are an expert quiz maker.

Create a multiple-choice quiz for the topic "$moduleTitle" in the "$topic" learning roadmap.

Requirements:
- 5 questions
- Each question has exactly 4 options
- Only ONE correct answer per question
- Provide a detailed explanation for the correct answer
- Return in pure JSON array format, where each object contains:
    - question_text (string)
    - options (array of 4 strings)
    - correct_answer (string)
""".trimIndent()


        return try {
            if (BuildConfig.DEBUG) {
                Log.d("QUIZ_DEBUG", "📡 Sending prompt to AI (${prompt.length} chars)")
            }

            val response = api.getAiRoadmap(
                AiRequest(
                    model = "gpt-3.5-turbo",
                    messages = listOf(Message(content = prompt)),
                    max_tokens = 1000,
                    temperature = 0.7
                )
            )


            val innerJson = response.choices.firstOrNull()?.message?.content?.trim().orEmpty()
            if (BuildConfig.DEBUG) {
                Log.d("QUIZ_DEBUG", "🔥 Raw AI output: $innerJson")
            }


            var cleanedJson = innerJson
            if (cleanedJson.startsWith("```json")) {
                cleanedJson = cleanedJson.removePrefix("```json").trim()
            }
            if (cleanedJson.endsWith("```")) {
                cleanedJson = cleanedJson.removeSuffix("```").trim()
            }

            return Gson().fromJson(cleanedJson, Array<Question>::class.java).toList()

        } catch (e: Exception) {
            emptyList()
        }
    }

}
