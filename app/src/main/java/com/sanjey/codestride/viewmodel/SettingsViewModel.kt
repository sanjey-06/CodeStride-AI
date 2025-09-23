package com.sanjey.codestride.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor() : ViewModel() {

    private val _openUrl = MutableSharedFlow<String>()
    val openUrl: SharedFlow<String> = _openUrl

    fun onLegalClick(item: String) {
        val url = when (item) {
            "Privacy Policy" -> "https://codestride.vercel.app/privacy-policy"
            "Terms & Conditions" -> "https://codestride.vercel.app/terms-and-conditions"
            else -> return
        }
        viewModelScope.launch {
            _openUrl.emit(url)
        }
    }
}
