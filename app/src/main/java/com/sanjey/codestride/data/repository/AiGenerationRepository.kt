package com.sanjey.codestride.data.repository

import android.util.Log
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import com.google.gson.Gson
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

class AiGenerationRepository @Inject constructor(
    private val api: AiApiService
) {
    private suspend fun generateRoadmap(topic: String): List<RoadmapItem> {
        val prompt = """
You are an expert curriculum designer and HTML writer.

Create a 10-step beginner-friendly roadmap for learning "$topic".

Each step must include these 5 fields:

1. "title": A concise module name (Title Case, max 6 words)
2. "description": One-sentence summary of what the learner will achieve
3. "link": A relevant YouTube video URL. Use "https://www.youtube.com/watch?v=xxxxx" if unknown.
4. "html_content": A rich, detailed HTML tutorial-style section with:

    - A <h2> title for the topic
    - 3 to 5 <p> paragraphs explaining the concept in depth
    - One <ul><li></li></ul> list with examples, tools, or tips
    - One <pre><code> block with a relevant code example (with comments)
    - Use <b> and <i> tags for emphasis
    - If helpful, include a real-world analogy or use-case
5. "quizId": A unique string identifier for the module's quiz (e.g., "quiz1", "quiz2")


Important:

- Make the content feel like a short blog tutorial
- Do not skip paragraphs or code
- Format response as a pure JSON array with 10 items only (no markdown wrapping)
""".trimIndent()


        Log.d("AI_DEBUG", "🟡 Starting AI roadmap generation for topic: $topic")
        Log.d("AI_DEBUG", "📝 Prompt size = ${prompt.length} characters")

        val startTime = System.currentTimeMillis()

        return try {
            Log.d("AI_PROMPT_USED", "Prompt:\n$prompt")
            Log.d("AI_PARAMS", "Tokens: 2000, Temp: 0.7, Model: gpt-3.5-turbo")

            val response = api.getAiRoadmap(
                AiRequest(
                    model = "gpt-3.5-turbo",
                    messages = listOf(Message(content = prompt)),
                    max_tokens = 2000,
                    temperature = 0.7
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

            // 🔹 Clean and parse JSON
            val cleanedJson = sanitizeAiJson(innerJson)
            Log.d("AI_RAW_JSON", "✅ Cleaned JSON:\n$cleanedJson")

            val parsed = Gson().fromJson(cleanedJson, Array<RoadmapItem>::class.java).toList()

            if (parsed.size != 10) {
                Log.w("AI_DEBUG", "⚠️ Expected 10 modules, got ${parsed.size}.")
            }

            Log.d("AI_DEBUG", "✅ Parsed ${parsed.size} modules successfully")
            parsed

        } catch (e: Exception) {
            val duration = System.currentTimeMillis() - startTime
            Log.e("AI_ERROR", "❌ Failed to generate roadmap: ${e.message} (after ${duration}ms)")
            emptyList()
        }
    }


    private fun sanitizeAiJson(raw: String): String {
        var result = raw.trim()

        // 1. Remove markdown code fences
        if (result.startsWith("```json")) {
            result = result.removePrefix("```json").trim()
        }
        if (result.endsWith("```")) {
            result = result.removeSuffix("```").trim()
        }

        // 2. Extract only the JSON array part
        val start = result.indexOf("[")
        val end = result.lastIndexOf("]")
        if (start != -1 && end != -1 && end > start) {
            result = result.substring(start, end + 1)
        }

        // 3. Escape unescaped quotes inside html_content
        result = result.replace(Regex("(?<=html_content\":\\s?\").*?(?=\")")) {
            it.value.replace("\"", "\\\"")
        }

        return result
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
            Log.e("AI_SINGLE_MODULE", "❌ Error generating content for $moduleTitle: ${e.message}")
            ""
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

            Log.d("YOUTUBE_FETCH", "🚀 fetchYouTubeUrlSuspend CALLED with query=$query, key=${Constants.YOUTUBE_API_KEY.take(6)}***")

            val call = YouTubeApiClient.retrofit.searchVideos(
                query = "$query tutorial",
                apiKey = Constants.YOUTUBE_API_KEY
            )

            Log.d("YOUTUBE_FETCH", "📡 Executing YouTube API call → ${call.request().url}")

            call.enqueue(object : retrofit2.Callback<YouTubeResponse> {
                override fun onResponse(call: Call<YouTubeResponse>, response: retrofit2.Response<YouTubeResponse>) {
                    val videoId = response.body()?.items?.firstOrNull()?.id?.videoId
                    Log.d("YOUTUBE_FETCH", "✅ Response code: ${response.code()}")
                    Log.d("YOUTUBE_FETCH", "✅ Response body: ${response.body()}")
                    Log.d("YOUTUBE_FETCH", "🎥 Video ID: ${videoId ?: "NULL"}")

                    cont.resume(
                        videoId?.let { "https://www.youtube.com/watch?v=$it" },
                        null
                    )
                }

                override fun onFailure(call: Call<YouTubeResponse>, t: Throwable) {
                    Log.e("YOUTUBE_FETCH", "❌ API call failed: ${t.message}", t)
                    cont.resume(null, null)
                }
            })
        }


    suspend fun generateQuiz(topic: String, moduleTitle: String): List<Question> {
        Log.d("QUIZ_DEBUG", "🚀 Starting AI quiz generation → topic=$topic, moduleTitle=$moduleTitle")

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
            Log.d("QUIZ_DEBUG", "📡 Sending prompt to AI (${prompt.length} chars)")

            val response = api.getAiRoadmap(
                AiRequest(
                    model = "gpt-3.5-turbo",
                    messages = listOf(Message(content = prompt)),
                    max_tokens = 1000,
                    temperature = 0.7
                )
            )


            val innerJson = response.choices.firstOrNull()?.message?.content?.trim().orEmpty()
            Log.d("QUIZ_DEBUG", "🔥 Raw AI output: $innerJson")


            var cleanedJson = innerJson
            if (cleanedJson.startsWith("```json")) {
                cleanedJson = cleanedJson.removePrefix("```json").trim()
            }
            if (cleanedJson.endsWith("```")) {
                cleanedJson = cleanedJson.removeSuffix("```").trim()
            }
            Log.d("QUIZ_DEBUG", "✅ Cleaned JSON: $cleanedJson")

            return Gson().fromJson(cleanedJson, Array<Question>::class.java).toList()

        } catch (e: Exception) {
            Log.e("QUIZ_DEBUG", "❌ Error generating quiz for $moduleTitle: ${e.message}")
            emptyList()
        }
    }

}
