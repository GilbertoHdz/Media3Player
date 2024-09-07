package com.gilbertohdz.media3.video.ui.player

import com.gilbertohdz.media3.video.model.Video

data class PlayerScreenState(
    val isPlaying: Boolean = false,
    val isBuffering: Boolean = false,
    val currentPosition: Long = 0L,
    val bufferedPosition: Long = 0L,
    val currentVideo: Video? = null,
    val playlist: List<Video> = emptyList(),
    val error: Exception? = null,
)