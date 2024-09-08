package com.gilbertohdz.media3.video.ui.player

import com.gilbertohdz.media3.video.model.Video
import com.gilbertohdz.media3.video.ui.components.ControlsListener

data class PlayerScreenState(
    val isPlaying: Boolean = false,
    val isBuffering: Boolean = false,
    val currentPosition: Long = 0L,
    val bufferedPosition: Long = 0L,
    val currentVideo: Video? = null,
    val currentSpeed: Int = 1,
    val playlist: List<Video> = emptyList(),
    val controlsListener: ControlsListener = object : ControlsListener {},
    val error: Exception? = null,
)