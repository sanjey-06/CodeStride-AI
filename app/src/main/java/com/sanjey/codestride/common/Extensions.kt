package com.sanjey.codestride.common

import android.content.Context
import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.lifecycle.LiveData
import com.sanjey.codestride.R
import java.text.SimpleDateFormat
import java.util.*

// ✅ Show Toast
fun Context.showToast(message: String) {
    Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
}

// ✅ Observe LiveData as State in Compose
@Composable
fun <T> LiveData<T>.collectAsStateValue(initial: T): T {
    val value by this.observeAsState(initial)
    return value
}

// ✅ Capitalize First Letter
fun String.capitalizeFirst(): String {
    return this.replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() }
}

// ✅ Convert Timestamp to Readable Date
fun Long.toDateString(): String {
    val sdf = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
    return sdf.format(Date(this))
}

// ✅ UiState Handling Shortcuts
inline fun <T> UiState<T>.onSuccess(action: (T) -> Unit): UiState<T> {
    if (this is UiState.Success) action(data)
    return this
}

inline fun UiState<*>.onError(action: (String) -> Unit): UiState<*> {
    if (this is UiState.Error) action(message)
    return this
}

fun getImageResIdFromName(imageName: String): Int {
    return when (imageName.lowercase()) {
        "kotlin_novice_badge" -> R.drawable.kotlin_novice_badge
        "security_specialist_badge" -> R.drawable.security_specialist_badge
        "jetpack_explorer_badge" -> R.drawable.jetpack_explorer_badge
        else -> R.drawable.ic_none
    }
}
