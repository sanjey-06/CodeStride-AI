package com.sanjey.codestride.viewmodel

import android.util.Log
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
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
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

    private val _homeUiState = MutableStateFlow<UiState<HomeScreenData>>(UiState.Loading)
    val homeUiState: StateFlow<UiState<HomeScreenData>> = _homeUiState

    init {
        observeHomeData()
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    private fun observeHomeData() {
        viewModelScope.launch {
            try {
                val userId = FirebaseAuth.getInstance().currentUser?.uid
                    ?: throw Exception("User not logged in")

                roadmapRepository.observeCurrentRoadmap(userId)
                    .flatMapLatest { currentRoadmapId ->
                        combine(
                            roadmapRepository.observeRoadmaps(),
                            if (currentRoadmapId != null)
                                roadmapRepository.observeProgress(userId, currentRoadmapId)
                            else flowOf(null to emptyList()),
                            userRepository.observeUserStats(userId) // 👈 NEW live stream for streak
                        ) { roadmaps, progressData, userStats ->
                            Triple(Triple(currentRoadmapId, roadmaps, progressData), userStats, Unit)
                        }
                    }
                    .collect { (triple, userStats) ->
                        val (currentRoadmapId, roadmaps, progressData) = triple
                        val (currentModuleId, completedModules) = progressData

                        val firstName = firebaseRepository.getFirstName()
                        val quotes = firebaseRepository.getQuotes()
                        val badgeDocs = firebaseRepository.getUserBadges(userId)
                        val quote = if (quotes.isNotEmpty()) {
                            val uidHash = userId.hashCode().absoluteValue
                            val dayIndex = (LocalDate.now().dayOfYear + uidHash) % quotes.size
                            quotes[dayIndex]
                        } else {
                            Quote("Keep pushing!", "CodeStride")
                        }

                        val roadmap = roadmaps.find { it.id == currentRoadmapId }
                            ?: currentRoadmapId?.let { roadmapRepository.getRoadmapById(it) }

                        val currentModuleTitle = if (currentModuleId != null && roadmap != null) {
                            roadmapRepository.getModuleTitle(roadmap.id, currentModuleId)
                        } else {
                            "Start from Module 1"
                        }

                        val totalModules = if (roadmap != null) {
                            roadmapRepository.getModulesCount(roadmap.id)
                        } else 0

                        val progressPercent = if (totalModules > 0) {
                            ((completedModules.size.toFloat() / totalModules.toFloat()) * 100).toInt()
                        } else 0

                        Log.d("HOMIES_DEBUG", """
--- HOME DATA CALCULATION ---
Current Roadmap ID: $currentRoadmapId
Roadmap Title: ${roadmap?.title ?: "N/A"}
Total Modules: $totalModules
Completed Modules: ${completedModules.size} → $completedModules
Calculated Progress: $progressPercent%
------------------------------
""".trimIndent())

                        _homeUiState.value = UiState.Success(
                            HomeScreenData(
                                firstName = firstName,
                                userStats = userStats, // 👈 now comes from live observer
                                currentRoadmap = if (roadmap != null) {
                                    RoadmapUI(
                                        title = roadmap.title,
                                        iconResId = getIconResource(roadmap.icon),
                                        progressPercent = progressPercent,
                                        currentModuleTitle = currentModuleTitle
                                    )
                                } else {
                                    RoadmapUI(
                                        title = "No Roadmap Selected",
                                        iconResId = R.drawable.ic_none,
                                        progressPercent = 0
                                    )
                                },
                                badges = badgeDocs,
                                exploreRoadmaps = roadmaps,
                                quote = quote
                            )
                        )
                    }
            } catch (e: Exception) {
                Log.e("HOME_DEBUG", "Error in observeHomeData: ${e.message}", e)
                _homeUiState.value = UiState.Error(e.message ?: "Failed to load home data")
            }
        }
    }
    fun refreshBadges() {
        viewModelScope.launch {
            try {
                val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return@launch
                val badgeDocs = firebaseRepository.getUserBadges(userId)

                val currentState = _homeUiState.value
                if (currentState is UiState.Success) {
                    _homeUiState.value = currentState.copy(
                        data = currentState.data.copy(badges = badgeDocs)
                    )
                }
            } catch (e: Exception) {
                Log.e("HOME_DEBUG", "Failed to refresh badges: ${e.message}")
            }
        }
    }

}
