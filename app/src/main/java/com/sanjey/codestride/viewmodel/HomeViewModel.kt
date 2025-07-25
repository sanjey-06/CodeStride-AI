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

    /**
     * ✅ Observes Firestore data for real-time updates but keeps existing logic
     */
    private fun observeHomeData() {
        viewModelScope.launch {
            try {
                val userId = FirebaseAuth.getInstance().currentUser?.uid
                    ?: throw Exception("User not logged in")

                roadmapRepository.observeRoadmaps().collectLatest { roadmaps ->
                    _homeUiState.value = UiState.Loading // Optional: show shimmer

                    // ✅ Run these in parallel
                    val firstNameDeferred = async { firebaseRepository.getFirstName() }
                    val userStatsDeferred = async { userRepository.updateStreakOnLearning(userId) }
                    val quotesDeferred = async { firebaseRepository.getQuotes() }

                    // ✅ Await all results together
                    val firstName = firstNameDeferred.await()
                    val userStats = userStatsDeferred.await()
                    val quotes = quotesDeferred.await()

                    val currentRoadmap = if (roadmaps.isNotEmpty()) {
                        val roadmap = roadmaps.first()
                        RoadmapUI(
                            title = roadmap.title,
                            iconResId = getIconResource(roadmap.icon),
                            progressPercent = 0
                        )
                    } else {
                        RoadmapUI("No Roadmap", 0, 0)
                    }

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
                _homeUiState.value = UiState.Error(e.message ?: "Failed to load home data")
            }
        }
    }
}
