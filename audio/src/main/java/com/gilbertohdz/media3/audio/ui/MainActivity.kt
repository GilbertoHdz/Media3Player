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
import com.gilbertohdz.media3.audio.ui.player.PlayerScreen
import com.gilbertohdz.media3.audio.ui.player.PlayerScreenViewModel
import com.gilbertohdz.media3.audio.ui.theme.Media3PlayerTheme

class MainActivity : ComponentActivity() {

    private val viewmodel by viewModels<PlayerScreenViewModel> {
        ViewModelProvider.AndroidViewModelFactory.getInstance(application)
    }

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
    }

    override fun onStart() {
        super.onStart()
        viewmodel.onStart()
    }

    override fun onStop() {
        super.onStop()
        viewmodel.onStop()
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