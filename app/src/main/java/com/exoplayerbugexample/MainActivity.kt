package com.exoplayerbugexample

import android.graphics.Color
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.OptIn
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.media3.common.util.UnstableApi
import androidx.media3.ui.AspectRatioFrameLayout
import androidx.media3.ui.PlayerView
import com.exoplayerbugexample.theme.ExoPlayerComposeTheme
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ExoPlayerComposeTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background,
                ) {
                    VideoPlayerSample()
                }
            }
        }
    }
}

@Composable
fun VideoPlayerSample() {
    val state = rememberExoPlayerState()

    var isFirstVideo by remember { mutableStateOf(true) }

    LaunchedEffect(state) {
        state.startVideo(R.raw.example_1)
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
    ) {
        ExoPlayer(
            state = state,
            modifier = Modifier
                .align(Alignment.Center)
                .fillMaxWidth()
                .aspectRatio(16f / 9),
        )

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            ControlButtons(
                isPlayed = state.playWhenReady,
                onPlay = {
                    state.resume()
                },
                onPause = {
                    state.pause()
                },
                onNext = {
                    if (isFirstVideo) {
                        state.startVideo(R.raw.example_2)
                    } else {
                        state.startVideo(R.raw.example_1)
                    }

                    isFirstVideo = isFirstVideo.not()
                },
            )
        }
    }
}

@Composable
private fun ControlButtons(
    isPlayed: Boolean,
    onPlay: () -> Unit,
    onPause: () -> Unit,
    onNext: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Button(
            onClick = {
                if (isPlayed) {
                    onPause()
                } else {
                    onPlay()
                }
            },
        ) {
            val text = if (isPlayed) {
                "Pause"
            } else {
                "Play"
            }
            Text(text = text)
        }

        Button(onClick = onNext) {
            Text(text = "Next")
        }
    }
}

@OptIn(UnstableApi::class)
@Composable
private fun ExoPlayer(
    state: ExoPlayerState,
    modifier: Modifier = Modifier,
) {
    val scope = rememberCoroutineScope()
    AndroidView(
        modifier = modifier,
        factory = {
            val view = PlayerView(it).apply {
                keepScreenOn = true
                resizeMode = AspectRatioFrameLayout.RESIZE_MODE_FIT
                useController = false
                setShutterBackgroundColor(Color.TRANSPARENT)
            }

            return@AndroidView view
        },
        update = {
            scope.launch {
                it.player = state.awaitPlayer()
            }
        },
    )
}
