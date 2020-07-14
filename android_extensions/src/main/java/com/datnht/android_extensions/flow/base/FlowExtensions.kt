package com.datnht.android_extensions.flow.base

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.collectLatest
import kotlin.coroutines.coroutineContext

/**
 * Only proceed with the given action if the coroutine has not been cancelled.
 * Necessary because Flow.collect receives items even after coroutine was cancelled
 * https://github.com/Kotlin/kotlinx.coroutines/issues/1265
 */
suspend inline fun <T> Flow<T>.safeCollect(crossinline action: suspend (T) -> Unit) {
    collect {
        coroutineContext.ensureActive()
        action(it)
    }
}

@ExperimentalCoroutinesApi
suspend inline fun <T> Flow<T>.safeCollectLatest(crossinline action: suspend (T) -> Unit) {
    collectLatest {
        coroutineContext.ensureActive()
        action(it)
    }
}