package com.gilbertohdz.media3.video.model

data class Video(
    val id: Int,
    val title: String,
    val format: String,
    val source: String,
    val thumb: String,
    val durationSeconds: Int,
)