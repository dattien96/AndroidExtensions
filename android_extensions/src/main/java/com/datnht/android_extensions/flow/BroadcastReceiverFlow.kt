package com.datnht.android_extensions.flow

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.channels.ConflatedBroadcastChannel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow

@FlowPreview
@ExperimentalCoroutinesApi
fun Context.flowBroadcasts(intentFilter: IntentFilter): Flow<Intent> {
    val resultChannel = ConflatedBroadcastChannel<Intent>()

    val receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            resultChannel.offer(intent)
        }
    }

    resultChannel.invokeOnClose {
        unregisterReceiver(receiver)
    }

    registerReceiver(receiver, intentFilter)
    return resultChannel.asFlow()
}