package com.gilbertohdz.media3.video.ui.player

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.media3.common.Player
import androidx.media3.exoplayer.mediacodec.MediaCodecAdapter.Configuration
import androidx.media3.session.SessionToken
import com.gilbertohdz.media3.video.R
import com.gilbertohdz.media3.video.model.VideoList
import com.gilbertohdz.media3.video.ui.components.Playlist
import com.gilbertohdz.media3.video.ui.components.VideoPlayer
import com.gilbertohdz.media3.video.ui.theme.Media3PlayerTheme
import com.gilbertohdz.media3.video.utils.DummyPlayer

@Composable
fun PlayerScreen(player: Player, sessionToken: SessionToken, modifier: Modifier = Modifier) {
    val model: PlayerScreenViewModel = viewModel()
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    DisposableEffect(key1 = sessionToken, key2 = context, key3 = lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_STOP) {
                player.pause()
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer) // Main Activity
        model.onStart(context, sessionToken)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
            model.onStop()
        }
    }

    val screenState by model.playerScreenState.collectAsState()
    PlayerScreen(player, screenState, modifier)
}

@Composable
private fun PlayerScreen(player: Player, screenState: PlayerScreenState, modifier: Modifier = Modifier) {
    // Handle Orientation to fullscreen without Title Toolbar
    if (LocalConfiguration.current.orientation == android.content.res.Configuration.ORIENTATION_LANDSCAPE) {
        VideoPlayer(player = player, screenState = screenState)
    } else {
        Surface(modifier = Modifier.fillMaxSize()) {
            Column(modifier = modifier.fillMaxSize()) {
                Text(
                    text = stringResource(id = R.string.app_name),
                    fontSize = 32.sp,
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.primary)
                        .padding(8.dp),
                )
                VideoPlayer(player = player, screenState = screenState)
                Playlist(
                    videos = screenState.playlist,
                    onVideoClick = screenState.controlsListener::onVideoClick,
                    modifier = Modifier.fillMaxHeight(),
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    Media3PlayerTheme {
        PlayerScreen(DummyPlayer(), PlayerScreenState(playlist = VideoList))
    }
}