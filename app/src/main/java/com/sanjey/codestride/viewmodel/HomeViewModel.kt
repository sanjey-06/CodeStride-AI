package com.sanjey.codestride.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.sanjey.codestride.R
import com.sanjey.codestride.common.getIconResId
import com.sanjey.codestride.data.model.RoadmapUI
import com.sanjey.codestride.data.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.time.LocalDate
import javax.inject.Inject
import kotlin.math.absoluteValue

data class UserStats(
    val streak: Int = 0,
    val progressPercent: Float = 0f,
    val nextBadgeMsg: String = ""
)

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val userRepository: UserRepository
) : ViewModel() {

    private val firestore = FirebaseFirestore.getInstance()
    private val userId = FirebaseAuth.getInstance().currentUser?.uid

    // ✅ First Name
    val firstName: LiveData<String> = MutableLiveData<String>().apply {
        userId?.let { uid ->
            firestore.collection("users").document(uid)
                .addSnapshotListener { snapshot, _ ->
                    value = snapshot?.getString("firstName") ?: ""
                }
        }
    }

    // ✅ Quote of the Day
    val quoteOfTheDay: LiveData<String> = MutableLiveData<String>().apply {
        val quotes = listOf(
            "One step closer to mastery every day",
            "Keep pushing, even if it's 1% improvement",
            "Consistency beats motivation",
            "You’re not late, you’re early for tomorrow",
            "Learn a little, but learn every day"
        )
        val uidHash = userId?.hashCode()?.absoluteValue ?: 0
        val dayIndex = (LocalDate.now().dayOfYear + uidHash) % quotes.size
        value = quotes[dayIndex]
    }

    // ✅ User Stats from Repository
    private val _userStats = MutableLiveData<UserStats>()
    val userStats: LiveData<UserStats> = _userStats

    // ✅ Current Roadmap
    private val _currentRoadmap = MutableLiveData<RoadmapUI>()
    val currentRoadmap: LiveData<RoadmapUI> = _currentRoadmap

    // ✅ Badges
    private val _badges = MutableLiveData<List<Triple<String, Int, Boolean>>>()
    val badges: LiveData<List<Triple<String, Int, Boolean>>> = _badges

    // ✅ Explore Roadmaps
    private val _exploreRoadmaps = MutableLiveData<List<Pair<Int, String>>>()
    val exploreRoadmaps: LiveData<List<Pair<Int, String>>> = _exploreRoadmaps

    init {

        fetchCurrentRoadmap()
        fetchBadges()
        fetchExploreRoadmaps()
    }

    fun updateStreakOnLearning() {
        viewModelScope.launch {
            val uid = FirebaseAuth.getInstance().currentUser?.uid ?: return@launch
            Log.d("STREAK_DEBUG", "updateStreakOnLearning() triggered for user: $uid")

            val stats = userRepository.updateStreakOnLearning(uid)
            Log.d("STREAK_DEBUG", "Updated streak result → ${stats.streak} days, progress: ${stats.progressPercent}")

            _userStats.postValue(stats)
        }
    }

    fun refreshUserStats() {
        viewModelScope.launch {
            val uid = FirebaseAuth.getInstance().currentUser?.uid ?: return@launch
            val userDoc = FirebaseFirestore.getInstance()
                .collection("users").document(uid).get().await()

            val streak = userDoc.getLong("streak")?.toInt() ?: 0
            val progress = (streak / 10f).coerceAtMost(1f)
            val nextBadgeMsg = if (streak >= 10) {
                "🔥 Amazing! You're on fire with $streak days streak!"
            } else {
                "You're ${10 - streak} day(s) away from hitting 10 days!"
            }

            Log.d("STREAK_DEBUG", "Refreshed streak → $streak days, progress: $progress")
            _userStats.postValue(UserStats(streak, progress, nextBadgeMsg))
        }
    }



    private fun fetchCurrentRoadmap() {
        viewModelScope.launch {
            userId?.let { uid ->
                val userDoc = firestore.collection("users").document(uid).get().await()
                val currentRoadmapId = userDoc.getString("current_roadmap") ?: "java"

                val roadmapDoc = firestore.collection("roadmaps").document(currentRoadmapId).get().await()
                val title = roadmapDoc.getString("title") ?: "Loading..."
                val iconName = roadmapDoc.getString("icon") ?: ""
                val iconRes = getIconResId(iconName)

                val progressDoc = firestore.collection("users").document(uid)
                    .collection("progress").document(currentRoadmapId).get().await()
                val completed = (progressDoc.get("completed_modules") as? List<*>)?.size ?: 0
                val totalModules = firestore.collection("roadmaps").document(currentRoadmapId)
                    .collection("modules").get().await().size()
                val progressPercent = if (totalModules > 0) (completed * 100 / totalModules) else 0

                _currentRoadmap.postValue(RoadmapUI(title, iconRes, progressPercent))
            }
        }
    }

    private fun fetchBadges() {
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
                val iconRes = getIconResId(iconName)
                iconRes to title
            }
            _exploreRoadmaps.postValue(data)
        }
    }
}
