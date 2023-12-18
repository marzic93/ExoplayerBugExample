package com.exoplayerbugexample

import android.content.Context
import androidx.annotation.OptIn
import androidx.annotation.RawRes
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.datasource.DefaultDataSourceFactory
import androidx.media3.datasource.RawResourceDataSource
import androidx.media3.exoplayer.DefaultRenderersFactory
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.source.ProgressiveMediaSource
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first

@OptIn(UnstableApi::class)
@Stable
class ExoPlayerState(
    private val context: Context,
) : Player.Listener {

    var playWhenReady by mutableStateOf(false)

    private val playerFlow = MutableStateFlow<ExoPlayer?>(null)

    init {
        val renderersFactory = DefaultRenderersFactory(context.applicationContext)
            .setExtensionRendererMode(DefaultRenderersFactory.EXTENSION_RENDERER_MODE_ON)
        val player = ExoPlayer.Builder(context, renderersFactory).build()
        player.addListener(this)

        playerFlow.value = player
    }

    override fun onPlayerStateChanged(playWhenReady: Boolean, playbackState: Int) {
        this.playWhenReady = playWhenReady
    }

    fun startVideo(@RawRes videoId: Int) {
        val uri = RawResourceDataSource.buildRawResourceUri(videoId)
        val mediaItem = MediaItem.Builder().setUri(uri).build()
        val dataSourceFactory = DefaultDataSourceFactory(context, "VIDEO_USER_AGENT")

        val mediaSource =
            ProgressiveMediaSource.Factory(dataSourceFactory).createMediaSource(mediaItem)

        val player = playerFlow.value
        player?.setMediaSource(mediaSource)
        player?.prepare()

        resume()
    }

    fun pause() {
        playerFlow.value?.playWhenReady = false
    }

    fun resume() {
        playerFlow.value?.playWhenReady = true
    }

    internal suspend fun awaitPlayer(): ExoPlayer {
        return playerFlow.value ?: playerFlow.filterNotNull().first()
    }
}

@Composable
fun rememberExoPlayerState(): ExoPlayerState {
    val context = LocalContext.current

    return remember(context) {
        ExoPlayerState(context)
    }
}
