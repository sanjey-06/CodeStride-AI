package com.sanjey.codestride.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.sanjey.codestride.R
import com.sanjey.codestride.data.repository.FirebaseRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.time.LocalDate
import javax.inject.Inject
import com.sanjey.codestride.common.getIconResId
import kotlin.math.absoluteValue

data class UserStats(
    val streak: Int = 0,
    val progressPercent: Float = 0f,
    val nextBadgeMsg: String = ""
)

data class RoadmapUI(
    val title: String,
    val iconResId: Int,
    val progressPercent: Int
)

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val repo: FirebaseRepository
) : ViewModel() {

    private val firestore = FirebaseFirestore.getInstance()
    private val userId = FirebaseAuth.getInstance().currentUser?.uid

    val firstName: LiveData<String> = repo.getFirstName()

    val quoteOfTheDay: LiveData<String> = MutableLiveData<String>().apply {
        val quotes = listOf(
            "One step closer to mastery every day",
            "Keep pushing, even if it's 1% improvement",
            "Consistency beats motivation",
            "You’re not late, you’re early for tomorrow",
            "Learn a little, but learn every day"
        )
        val uid = FirebaseAuth.getInstance().currentUser?.uid ?: "default"
        val hash = uid.hashCode().absoluteValue
        val dayIndex = (LocalDate.now().dayOfYear + hash) % quotes.size
        value = quotes[dayIndex]
    }

    private val _userStats = MutableLiveData<UserStats>()
    val userStats: LiveData<UserStats> = _userStats

    private val _currentRoadmap = MutableLiveData<RoadmapUI>()
    val currentRoadmap: LiveData<RoadmapUI> = _currentRoadmap

    private val _badges = MutableLiveData<List<Triple<String, Int, Boolean>>>()
    val badges: LiveData<List<Triple<String, Int, Boolean>>> = _badges

    private val _exploreRoadmaps = MutableLiveData<List<Pair<Int, String>>>()
    val exploreRoadmaps: LiveData<List<Pair<Int, String>>> = _exploreRoadmaps

    init {
        fetchUserStats()
        fetchCurrentRoadmap()
        fetchBadges()
        fetchExploreRoadmaps()
    }

    private fun fetchUserStats() {
        viewModelScope.launch {
            userId?.let { uid ->
                val userDoc = firestore.collection("users").document(uid).get().await()
                val streak = userDoc.getLong("streak")?.toInt() ?: 0

                val progress = (streak.toFloat() / 10f).coerceAtMost(1f)
                val nextBadgeMsg = if (streak >= 10) {
                    "🔥 Amazing! You're on fire with $streak days streak!"
                } else {
                    "You're ${10 - streak} day(s) away from hitting 10 days!"
                }

                _userStats.postValue(UserStats(streak, progress, nextBadgeMsg))
            }
        }
    }


    private fun fetchCurrentRoadmap() {
        viewModelScope.launch {
            userId?.let { uid ->
                val userDoc = firestore.collection("users").document(uid).get().await()
                val currentRoadmapId = userDoc.getString("current_roadmap") ?: "java"

                // Fetch roadmap title
                val roadmapDoc = firestore.collection("roadmaps").document(currentRoadmapId).get().await()
                val title = roadmapDoc.getString("title") ?: "Loading..."

                // Fetch progress
                val progressDoc = firestore.collection("users").document(uid)
                    .collection("progress").document(currentRoadmapId).get().await()
                val completed = (progressDoc.get("completed_modules") as? List<*>)?.size ?: 0

                val totalModules = firestore.collection("roadmaps").document(currentRoadmapId)
                    .collection("modules").get().await().size()
                val progressPercent = if (totalModules > 0) (completed * 100 / totalModules) else 0

                // Assign icon based on roadmapId
                val iconName = roadmapDoc.getString("icon") ?: ""
                val icon = getIconResId(iconName)
                _currentRoadmap.postValue(RoadmapUI(title, icon, progressPercent))


                _currentRoadmap.postValue(RoadmapUI(title, icon, progressPercent))
            }
        }
    }

    private fun fetchBadges() {
        // Future: Fetch from users/{userId}/badges
        _badges.postValue(
            listOf(
                Triple("Kotlin Novice", R.drawable.kotlin_novice_badge, true),
                Triple("Security Specialist", R.drawable.security_specialist_badge, false),
                Triple("Jetpack Explorer", R.drawable.jetpack_explorer_badge, false)
            )
        )
    }

    private fun fetchExploreRoadmaps() {
        viewModelScope.launch {
            val roadmapList = firestore.collection("roadmaps").get().await()
            val data = roadmapList.documents.map {
                val title = it.getString("title") ?: "Unknown"
                val iconName = it.getString("icon") ?: ""
                val iconResId = getIconResId(iconName)
                iconResId to title
            }
            _exploreRoadmaps.postValue(data)
        }
    }
}
