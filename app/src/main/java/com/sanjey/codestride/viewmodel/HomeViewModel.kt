package com.sanjey.codestride.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.sanjey.codestride.R
import com.sanjey.codestride.common.UiState
import com.sanjey.codestride.common.getIconResource
import com.sanjey.codestride.data.model.HomeScreenData
import com.sanjey.codestride.data.model.Quote
import com.sanjey.codestride.data.model.RoadmapUI
import com.sanjey.codestride.data.repository.FirebaseRepository
import com.sanjey.codestride.data.repository.RoadmapRepository
import com.sanjey.codestride.data.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject
import kotlin.math.absoluteValue

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val firebaseRepository: FirebaseRepository,
    private val roadmapRepository: RoadmapRepository
) : ViewModel() {

    private val _homeUiState = MutableLiveData<UiState<HomeScreenData>>(UiState.Loading)
    val homeUiState: LiveData<UiState<HomeScreenData>> = _homeUiState

    init {
            observeHomeData() // ✅ Use real-time updates
    }


    private fun observeHomeData() {
        viewModelScope.launch {
            try {
                val userId = FirebaseAuth.getInstance().currentUser?.uid
                    ?: throw Exception("User not logged in")

                roadmapRepository.observeRoadmaps().collectLatest { roadmaps ->
                    android.util.Log.d("HOME_DEBUG", "Roadmaps fetched: ${roadmaps.size}")

                    _homeUiState.value = UiState.Loading

                    // ✅ Run parallel async calls for speed
                    val firstNameDeferred = async { firebaseRepository.getFirstName() }
                    val quotesDeferred = async { firebaseRepository.getQuotes() }

                    android.util.Log.d("HOME_DEBUG", "Fetching firstName, streak, quotes for user: $userId")

                    val firstName = firstNameDeferred.await()
                    val quotes = quotesDeferred.await()

                    val userStatsDeferred = async { userRepository.getUserStats(userId) }
                    val userStats = userStatsDeferred.await()


                    val currentRoadmap = if (roadmaps.isNotEmpty()) {
                        val roadmap = roadmaps.first()
                        android.util.Log.d("HOME_DEBUG", "Current roadmap: ${roadmap.title}, ID: ${roadmap.id}")

                        // ✅ Log before fetching progress details
                        android.util.Log.d("HOME_DEBUG", "Calculating progress for roadmapId: ${roadmap.id}")

                        val totalModules = roadmapRepository.getModulesCount(roadmap.id)
                        android.util.Log.d("HOME_DEBUG", "Total modules: $totalModules")

                        val completedModules = userRepository.getCompletedModules(userId, roadmap.id)
                        android.util.Log.d("HOME_DEBUG", "Completed modules: $completedModules")

                        val progressPercent = if (totalModules > 0) {
                            ((completedModules.size.toFloat() / totalModules.toFloat()) * 100).toInt()
                        } else 0
                        android.util.Log.d("HOME_DEBUG", "Progress calculated: $progressPercent%")

                        RoadmapUI(
                            title = roadmap.title,
                            iconResId = getIconResource(roadmap.icon),
                            progressPercent = progressPercent
                        )
                    } else {
                        android.util.Log.d("HOME_DEBUG", "No roadmaps found, showing default UI")
                        RoadmapUI("No Roadmap", 0, 0)
                    }

                    android.util.Log.d("HOME_DEBUG", "Preparing UI with data: firstName=$firstName, streak=${userStats.streak}")

                    val badges = listOf(
                        Triple("Kotlin Novice", R.drawable.kotlin_novice_badge, true),
                        Triple("Security Specialist", R.drawable.security_specialist_badge, false),
                        Triple("Jetpack Explorer", R.drawable.jetpack_explorer_badge, false)
                    )

                    val exploreRoadmaps = roadmaps.map { getIconResource(it.icon) to it.title }

                    val quote = if (quotes.isNotEmpty()) {
                        val uidHash = userId.hashCode().absoluteValue
                        val dayIndex = (LocalDate.now().dayOfYear + uidHash) % quotes.size
                        quotes[dayIndex]
                    } else {
                        Quote("Keep pushing!", "CodeStride")
                    }

                    android.util.Log.d("HOME_DEBUG", "Setting UiState.Success for HomeScreen")

                    _homeUiState.value = UiState.Success(
                        HomeScreenData(
                            firstName = firstName,
                            userStats = userStats,
                            currentRoadmap = currentRoadmap,
                            badges = badges,
                            exploreRoadmaps = exploreRoadmaps,
                            quote = quote
                        )
                    )
                }
            } catch (e: Exception) {
                android.util.Log.e("HOME_DEBUG", "Error in observeHomeData: ${e.message}", e)
                _homeUiState.value = UiState.Error(e.message ?: "Failed to load home data")
            }
        }
    }

}
