package com.sanjey.codestride.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sanjey.codestride.data.remote.backend.formatResetTime
import com.sanjey.codestride.data.repository.UsageRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/** Remaining AI roadmaps today, for the "Generate AI Roadmap" popup. */
@HiltViewModel
class UsageViewModel @Inject constructor(
    private val repo: UsageRepository
) : ViewModel() {

    data class RoadmapQuota(
        val remaining: Int,
        val limit: Int,
        val resetTime: String   // e.g. "1:00 AM", in the phone's time zone
    )

    /** null = not loaded (or failed) -> the popup simply shows nothing extra. */
    private val _roadmapQuota = MutableStateFlow<RoadmapQuota?>(null)
    val roadmapQuota: StateFlow<RoadmapQuota?> = _roadmapQuota

    fun refresh() {
        viewModelScope.launch {
            repo.getUsage().onSuccess { usage ->
                _roadmapQuota.value = usage.features["roadmap"]?.let {
                    RoadmapQuota(it.remaining, it.limit, formatResetTime(usage.resetAt))
                }
            }
            // onFailure: keep null - never block the user because /usage failed
        }
    }
}