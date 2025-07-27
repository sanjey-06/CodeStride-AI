package com.sanjey.codestride.common

import com.sanjey.codestride.R

// ✅ Map icon names to resource IDs
fun getIconResource(iconName: String?): Int {
    return when (iconName?.lowercase()) {
        "ic_java" -> R.drawable.ic_java
        "ic_python" -> R.drawable.ic_python
        "ic_kotlin" -> R.drawable.ic_kotlin
        "ic_cpp" -> R.drawable.ic_cpp
        "ic_javascript" -> R.drawable.ic_javascript // ✅ Add this
        else -> R.drawable.ic_none // Default fallback

    }
}
