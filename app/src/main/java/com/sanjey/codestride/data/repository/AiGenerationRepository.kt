package com.sanjey.codestride.data.repository

import android.util.Log
import com.google.gson.Gson
import com.sanjey.codestride.data.model.RoadmapItem
import com.sanjey.codestride.data.model.ai.AiRequest
import com.sanjey.codestride.data.model.ai.Message
import com.sanjey.codestride.data.remote.AiApiService
import javax.inject.Inject

class AiGenerationRepository @Inject constructor(
    private val api: AiApiService
) {
    suspend fun generateRoadmap(topic: String): List<RoadmapItem> {
        val prompt = """
            Generate a 10-step roadmap to learn $topic as a beginner.
            Each step must include: title, description, and a resource link.
            Respond only in this JSON array format:
            [
              {
                "title": "Intro to X",
                "description": "Short summary...",
                "link": "https://..."
              }
            ]
        """.trimIndent()

        return try {
            val response = api.getAiRoadmap(
                AiRequest(
                    messages = listOf(Message(content = prompt))
                )
            )

            val innerJson = response.choices.firstOrNull()?.message?.content ?: return emptyList()
            Gson().fromJson(innerJson, Array<RoadmapItem>::class.java).toList()

        } catch (e: Exception) {
            Log.e("AI_ERROR", "Failed to generate roadmap: ${e.message}")
            emptyList()
        }
    }
}
