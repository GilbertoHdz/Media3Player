package com.gilbertohdz.media3.audio.model

data class Song(
    val id: Int,
    val title: String,
    val artist: String,
    val source: String,
    val thumb: String,
    val durationSeconds: Int,
)