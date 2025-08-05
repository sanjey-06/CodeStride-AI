package com.sanjey.codestride.data.remote

data class YouTubeResponse(
    val items: List<YouTubeVideoItem>
)

data class YouTubeVideoItem(
    val id: VideoId
)

data class VideoId(
    val videoId: String
)
