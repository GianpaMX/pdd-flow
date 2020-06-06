package io.github.gianpamx.pdd

import android.view.View
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

@ExperimentalCoroutinesApi
fun View.clicks(): Flow<Unit> = callbackFlow {
    this@clicks.setOnClickListener {
        offer(Unit)
    }
    awaitClose { this@clicks.setOnClickListener(null) }
}
