package com.datnht.android_extensions.extensions

import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

suspend fun BottomNavigationView.awaitTabChange() {

    suspendCancellableCoroutine<Unit> { cont ->
        cont.invokeOnCancellation {
            // do nothing
        }

        setOnNavigationItemSelectedListener {
            cont.resume(Unit)
            true
        }

    }
}