package com.gilbertohdz.media3.audio.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.ViewModelProvider
import androidx.media3.common.AudioAttributes
import androidx.media3.common.C
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.session.MediaSession
import com.gilbertohdz.media3.audio.ui.player.PlayerScreen
import com.gilbertohdz.media3.audio.ui.player.PlayerScreenViewModel
import com.gilbertohdz.media3.audio.ui.theme.Media3PlayerTheme

class MainActivity : ComponentActivity() {

    private val viewmodel by viewModels<PlayerScreenViewModel> {
        ViewModelProvider.AndroidViewModelFactory.getInstance(application)
    }

    lateinit var mediaSession: MediaSession
        private set

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Media3PlayerTheme {
                Surface {
                    val playerScreenState by viewmodel.playerScreenState.collectAsState()
                    PlayerScreen(playerScreenState)
                }
            }
        }

        val audioAttrs = AudioAttributes.Builder()
            .setUsage(C.USAGE_MEDIA)
            .setContentType(C.AUDIO_CONTENT_TYPE_MUSIC) // Router the audio with headphones or speaker
            .build()

        val player = ExoPlayer.Builder(this)
            .setAudioAttributes(audioAttrs, true) // Auto pause when other app start playing
            .setHandleAudioBecomingNoisy(true) // when unplug headphone will pause the player
            .build()

        mediaSession = MediaSession.Builder(this, player)
            .build()

        viewmodel.setupPlayer(player)
    }

    override fun onStart() {
        super.onStart()
        viewmodel.onStart()
    }

    override fun onStop() {
        super.onStop()
    }

    override fun onDestroy() {
        mediaSession.player.release()
        mediaSession.release()
        super.onDestroy()
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    Media3PlayerTheme {
        Greeting("Android")
    }
}