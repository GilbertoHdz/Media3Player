package com.gilbertohdz.media3.video.ui.player

import android.app.Application
import android.content.ContentResolver
import android.content.Context
import android.net.Uri
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.session.MediaController
import androidx.media3.session.SessionToken
import com.gilbertohdz.media3.video.model.VideoList
import com.gilbertohdz.media3.video.ui.components.PlayerControlsListener
import com.google.common.util.concurrent.ListenableFuture
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class PlayerScreenViewModel(
    app: Application
) : AndroidViewModel(app) {

    private lateinit var mediaControllerFuture: ListenableFuture<MediaController>

    private val mediaController: MediaController?
        get() = when (mediaControllerFuture.isDone && !mediaControllerFuture.isCancelled) {
            true -> mediaControllerFuture.get()
            else -> null
        }

    private val _playerScreenState = MutableStateFlow(PlayerScreenState())
    val playerScreenState: StateFlow<PlayerScreenState> = _playerScreenState

    private var progressUpdateJob: Job? = null

    fun onStart(context: Context, sessionToken: SessionToken) {
        mediaControllerFuture = MediaController.Builder(context, sessionToken).buildAsync()
        mediaControllerFuture.addListener({ setupPlayer() }, ContextCompat.getMainExecutor(context))
    }

    fun onStop() {
        MediaController.releaseFuture(mediaControllerFuture)
    }

    private fun setupPlayer() {
        val player = mediaController ?: return
        if (player.mediaItemCount == 0) {
            val mediaItems = loadVideos()
            player.setMediaItems(mediaItems)
        }
        setInitialPlayerScreenState(player)
        setupProgressUpdateJob(player)
        if (player.playbackState == Player.STATE_IDLE) {
            player.prepare()
        }
        player.addListener(object : Player.Listener {
            override fun onMediaItemTransition(mediaItem: MediaItem?, reason: Int) {
                updatePlayerScreenState {
                    copy(currentVideo = VideoList.find { it.id.toString() == mediaItem?.mediaId })
                }
            }

            override fun onIsPlayingChanged(isPlaying: Boolean) {
                when {
                    !isPlaying -> progressUpdateJob?.cancel()
                    else -> setupProgressUpdateJob(player)
                }
                updatePlayerScreenState {
                    copy(isPlaying = isPlaying)
                }
            }

            override fun onIsLoadingChanged(isLoading: Boolean) {
                updatePlayerScreenState {
                    copy(isBuffering = isLoading)
                }
            }

            override fun onPlaybackStateChanged(playbackState: Int) {
                updatePlayerScreenState {
                    copy(isBuffering = playbackState == Player.STATE_BUFFERING)
                }
            }

            override fun onPlayerError(error: PlaybackException) {
                updatePlayerScreenState {
                    copy(error = error)
                }
            }

            override fun onPlayerErrorChanged(error: PlaybackException?) {
                updatePlayerScreenState {
                    copy(error = error)
                }
            }

            override fun onPositionDiscontinuity(
                oldPosition: Player.PositionInfo,
                newPosition: Player.PositionInfo,
                reason: Int
            ) {
                if (reason == Player.DISCONTINUITY_REASON_SEEK) {
                    updatePlayerScreenState {
                        copy(currentPosition = newPosition.positionMs)
                    }
                }
            }
        })
    }

    private fun loadVideos(): List<MediaItem> = VideoList.map { video ->
        val context = getApplication<Application>()
        val packageName = context.packageName
        val mediaUri = if (video.source.startsWith("http")) {
            Uri.parse(video.source)
        } else {
            Uri.Builder()
                .scheme(ContentResolver.SCHEME_ANDROID_RESOURCE)
                .authority(packageName)
                .appendPath(context.resources.getIdentifier(video.source, "raw", packageName).toString())
                .build()
        }
        MediaItem.Builder()
            .setMediaId(video.id.toString())
            .setUri(mediaUri)
            .setMediaMetadata(
                MediaMetadata.Builder()
                    .setArtworkUri(video.thumb.toUri())
                    .build()
            )
            .build()
    }

    private fun setInitialPlayerScreenState(player: Player) {
        updatePlayerScreenState {
            copy(
                isPlaying = player.isPlaying,
                playlist = VideoList,
                currentVideo = VideoList.find { it.id.toString() == player.currentMediaItem?.mediaId },
                bufferedPosition = player.bufferedPosition,
                controlsListener = PlayerControlsListener(player)
            )
        }
    }

    private fun setupProgressUpdateJob(player: Player) {
        progressUpdateJob?.cancel()
        progressUpdateJob = viewModelScope.launch {
            while (true) {
                updatePlayerScreenState {
                    copy(
                        currentPosition = player.currentPosition,
                        bufferedPosition = player.bufferedPosition,
                    )
                }
                delay(1000)
            }
        }
    }


    private fun updatePlayerScreenState(block: PlayerScreenState.() -> PlayerScreenState) {
        _playerScreenState.value = block(_playerScreenState.value)
    }
}