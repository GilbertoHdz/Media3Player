package com.gilbertohdz.media3.video.ui.components

import android.content.Context
import android.view.SurfaceView
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.Crossfade
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.media3.common.Player
import com.gilbertohdz.media3.video.R
import com.gilbertohdz.media3.video.ui.player.PlayerScreenState
import com.gilbertohdz.media3.video.ui.player.PlayerScreenViewModel.Companion.PLAYBACK_PREFERENCES
import com.gilbertohdz.media3.video.ui.theme.Media3PlayerTheme
import com.gilbertohdz.media3.video.utils.DummyPlayer
import kotlinx.coroutines.delay

@Composable
fun VideoPlayer(player: Player, screenState: PlayerScreenState) {
    VideoPlayer(
        player = player,
        isPlaying = screenState.isPlaying,
        isLoading = screenState.isBuffering,
        currentSpeed = screenState.currentSpeed,
        currentPosition = screenState.currentPosition,
        bufferedPosition = screenState.bufferedPosition,
        durationSeconds = screenState.currentVideo?.durationSeconds,
        controlsListener = screenState.controlsListener,
    )
}

@Composable
private fun VideoPlayer(
    player: Player?,
    isPlaying: Boolean,
    isLoading: Boolean,
    currentSpeed: Int,
    currentPosition: Long,
    bufferedPosition: Long,
    durationSeconds: Int?,
    controlsListener: ControlsListener,
    error: Exception? = null,
) {
    var showControls by remember(error) {
        mutableStateOf(error == null)
    }
    val clickModifier = Modifier.clickable(
        interactionSource = remember { MutableInteractionSource() },
        indication = null,
    ) {
        showControls = !showControls
    }
    BoxWithConstraints(contentAlignment = Alignment.Center) {
        val modifier = Modifier
            .fillMaxWidth()
            .height(maxWidth * 9 / 16f)

        AndroidView(
            modifier = modifier
                .then(clickModifier),
            factory = ::SurfaceView
        ) { surfaceView ->
            player?.setVideoSurfaceView(surfaceView)
        }

        Crossfade(targetState = error, label = "ControlsOrErrorCrossfade") { error ->
            if (error != null) {
                val errorMessage = error.message ?: "Unknown error."
                ErrorButtons(
                    modifier = modifier,
                    error = errorMessage,
                    onRetry = controlsListener::onPlayPause,
                    onSkipNext = controlsListener::onSkipNext,
                )
            } else {
                AnimatedVisibility(
                    visible = showControls,
                    enter = fadeIn(),
                    exit = fadeOut(),
                    label = "ControlsVisibility",
                ) {
                    PlayerControls(
                        modifier = modifier,
                        isPlaying = isPlaying,
                        isLoading = isLoading,
                        currentPosition = currentPosition,
                        bufferedPosition = bufferedPosition,
                        durationSeconds = durationSeconds,
                        currentSpeed = currentSpeed,
                        controlsListener = controlsListener,
                    )
                }
            }
        }

        LaunchedEffect(showControls, isPlaying) {
            if (isPlaying) {
                delay(3000)
                showControls = false
            }
        }
    }
}

@Composable
private fun ErrorButtons(
    modifier: Modifier = Modifier,
    error: String,
    onRetry: () -> Unit,
    onSkipNext: () -> Unit
) {
    Column(
        modifier = modifier.background(Color.Black),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Text(error, modifier = Modifier.wrapContentSize())
        Spacer(modifier = Modifier.height(16.dp))
        Row(horizontalArrangement = Arrangement.SpaceEvenly, modifier = Modifier.fillMaxWidth()) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(
                    painter = painterResource(id = R.drawable.icon_retry),
                    contentDescription = stringResource(id = R.string.retry_action_text),
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .clickable(onClick = onRetry),
                    tint = Color.White,
                )
                Text(text = stringResource(id = R.string.retry_action_text), modifier = Modifier.wrapContentSize())
            }
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(
                    painter = painterResource(id = R.drawable.icon_skip_next),
                    contentDescription = stringResource(id = R.string.skip_next_action_text),
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .clickable(onClick = onSkipNext),
                    tint = Color.White,
                )
                Text(
                    text = stringResource(id = R.string.skip_next_action_text),
                    modifier = Modifier.wrapContentSize()
                )
            }
        }
    }
}

@Preview
@Composable
fun VideoPlayerPreview() {
    Media3PlayerTheme {
        Surface {
            val player = DummyPlayer()
            val playbackPreferences =
                LocalContext.current.getSharedPreferences(PLAYBACK_PREFERENCES, Context.MODE_PRIVATE)
            VideoPlayer(
                player = player,
                isPlaying = false,
                isLoading = false,
                currentSpeed = 1,
                currentPosition = 5_000L,
                bufferedPosition = 50_000L,
                durationSeconds = 100,
                controlsListener = PlayerControlsListener(player, playbackPreferences)
            )
        }
    }
}