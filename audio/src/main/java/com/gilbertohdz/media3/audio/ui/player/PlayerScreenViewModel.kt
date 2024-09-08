package com.gilbertohdz.media3.audio.ui.player

import android.app.Application
import androidx.lifecycle.AndroidViewModel
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

    fun onStart() {
        updatePlayerScreenState {
            copy(playlist = SongList)
        }
    }

    fun onStop() {

    }

}