package com.datnht.android_extensions.playexo

import android.content.Context
import android.net.Uri
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.source.ConcatenatingMediaSource
import com.google.android.exoplayer2.source.MediaSource
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.source.hls.HlsMediaSource
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector
import com.google.android.exoplayer2.ui.PlayerView
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory

fun PlayerView.initPlayer(context: Context): SimpleExoPlayer {
    val player = SimpleExoPlayer.Builder(context).build()
    this.player = player
    return player
}

fun PlayerView.initPlayerWithAdapStream(context: Context): SimpleExoPlayer {
    val trackSelector = DefaultTrackSelector(context)
    trackSelector.setParameters(
        trackSelector.buildUponParameters().setMaxVideoSizeSd())
    val player = SimpleExoPlayer.Builder(context).setTrackSelector(trackSelector).build()
    this.player = player
    return player
}

fun buildMediaSource(context: Context, uri: Uri): MediaSource = ProgressiveMediaSource.Factory(
    DefaultDataSourceFactory(
        context,
        "agent-dattien"
    )
).createMediaSource(uri)

fun buildConcatMediaSource(context: Context, uris: List<Uri>): MediaSource {
    val mediaSourceFactory = ProgressiveMediaSource.Factory(
        DefaultDataSourceFactory(
            context,
            "agent-dattien"
        )
    )
    val sourceList = mutableListOf<ProgressiveMediaSource>()
    uris.forEach {
        sourceList.add(mediaSourceFactory.createMediaSource(it))
    }
    return ConcatenatingMediaSource(*sourceList.toTypedArray())
}

fun buildHlsMediaSource(context: Context, uri: Uri): MediaSource = HlsMediaSource.Factory(
    DefaultDataSourceFactory(
        context,
        "agent-dattien"
    )
).createMediaSource(uri)
