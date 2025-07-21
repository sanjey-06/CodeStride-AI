package com.sanjey.codestride.common

import com.sanjey.codestride.R

fun getIconResId(iconName: String?): Int {
    return when (iconName) {
        "ic_java" -> R.drawable.ic_java
        "ic_python" -> R.drawable.ic_python
        "ic_kotlin" -> R.drawable.ic_kotlin
        "ic_cpp" -> R.drawable.ic_cpp
        else -> R.drawable.ic_none // Fallback icon
    }
}
