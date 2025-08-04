package com.sanjey.codestride.common

import com.sanjey.codestride.R

fun getIconResource(iconName: String?, roadmapId: String? = null): Int {
    // ✅ Use AI Robot Icon if roadmap ID starts with "ai_"
    if (roadmapId?.startsWith("ai_") == true) {
        return R.drawable.ic_ai
    }

    // ✅ Fallback to normal icon mapping
    return when (iconName?.lowercase()) {
        "ic_java" -> R.drawable.ic_java
        "ic_python" -> R.drawable.ic_python
        "ic_kotlin" -> R.drawable.ic_kotlin
        "ic_cpp" -> R.drawable.ic_cpp
        "ic_javascript" -> R.drawable.ic_javascript
        else -> R.drawable.ic_none
    }
}
