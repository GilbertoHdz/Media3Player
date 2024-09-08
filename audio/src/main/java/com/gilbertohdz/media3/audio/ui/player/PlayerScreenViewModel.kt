package com.gilbertohdz.media3.audio.ui.player

import android.app.Application
import android.content.ContentResolver
import android.net.Uri
import androidx.core.net.toUri
import androidx.lifecycle.AndroidViewModel
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.media3.common.Player
import com.gilbertohdz.media3.audio.model.SongList
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class PlayerScreenViewModel(
    app: Application
) : AndroidViewModel(app) {

    private val _playerScreenState = MutableStateFlow(PlayerScreenState())
    val playerScreenState: StateFlow<PlayerScreenState> = _playerScreenState

    private fun updatePlayerScreenState(update: PlayerScreenState.() -> PlayerScreenState) {
        _playerScreenState.value = _playerScreenState.value.update()
    }

    fun setupPlayer(player: Player) {
        val mediaItems = SongList.map { song ->
            val mediaUri = when (song.source.startsWith("http")) {
                true -> song.source.toUri()
                else -> {
                    val context = getApplication<Application>()
                    val packageName = context.packageName
                    Uri.Builder()
                        .scheme(ContentResolver.SCHEME_ANDROID_RESOURCE)
                        .authority(packageName)
                        .appendPath(
                            context.resources.getIdentifier(song.source, "raw", packageName).toString()
                        )
                        .build()
                }
            }

            MediaItem.Builder()
                .setMediaId(song.id.toString())
                .setUri(mediaUri)
                .setMediaMetadata(
                    MediaMetadata.Builder()
                        .setTitle(song.title)
                        .setArtist(song.artist)
                        .setArtworkUri(song.thumb.toUri())
                        .build()
                )
                .build()
        }

        player.setMediaItems(mediaItems)
        if (player.playbackState == Player.STATE_IDLE) {
            player.prepare()
        }
        setInitialPlayerScreenState(player)
    }

    private fun setInitialPlayerScreenState(player: Player) {
        updatePlayerScreenState {
            copy(
                onSongClick = { song ->
                    for (index in 0 until  player.mediaItemCount) {
                        if (player.getMediaItemAt(index).mediaId == song.id.toString()) {
                            if (index != player.currentMediaItemIndex) {
                                player.seekTo(index, 0)
                                break
                            }
                        }
                    }
                    if (player.playbackState == Player.STATE_IDLE) {
                        player.prepare()
                    }
                    player.play()
                }
            )
        }
    }

    fun onStart() {
        updatePlayerScreenState {
            copy(playlist = SongList)
        }
    }

    fun onStop() {

    }

}