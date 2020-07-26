package io.github.gianpamx.pdd.notification

import androidx.lifecycle.asLiveData
import io.github.gianpamx.pdd.domain.ObserveState
import io.github.gianpamx.pdd.domain.ObserveState.State
import io.github.gianpamx.pdd.toClock
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.channels.BroadcastChannel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map

class NotificationViewModel(
    observeState: ObserveState,
    defaultDispatcher: CoroutineDispatcher
) {
    private val errorChannel = BroadcastChannel<Throwable>(Channel.CONFLATED)

    val errors = errorChannel.asFlow()

    val notificationState = observeState()
        .map { it.toNotificationState() }
        .filterNotNull()
        .flowOn(defaultDispatcher)
        .asLiveData(defaultDispatcher)

    private fun State.toNotificationState() = when (this) {
        is State.Pomodoro -> NotificationState.Pomodoro(time.toClock())
        is State.Break -> NotificationState.Break(time.toClock())
        else -> null
    }
}
