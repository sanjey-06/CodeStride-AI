package com.sanjey.codestride.common

import com.sanjey.codestride.R

fun getAvatarResourceId(avatar: String): Int {
    return when (avatar) {
        "avatar_1" -> R.drawable.avatar_1
        "avatar_2" -> R.drawable.avatar_2
        "ic_none"  -> R.drawable.ic_none
        else -> R.drawable.ic_none
    }
}
