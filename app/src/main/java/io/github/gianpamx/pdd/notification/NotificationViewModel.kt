package io.github.gianpamx.pdd.notification

import androidx.lifecycle.asLiveData
import io.github.gianpamx.pdd.domain.ObserveState
import io.github.gianpamx.pdd.toClock
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map

class NotificationViewModel(observeState: ObserveState, defaultDispatcher: CoroutineDispatcher) {
    val state = observeState()
        .map { it.toNotificationState() }
        .filterNotNull()
        .flowOn(defaultDispatcher)
        .asLiveData(defaultDispatcher)

    private fun ObserveState.State.toNotificationState() = when (this) {
        is ObserveState.State.Pomodoro -> NotificationState.Pomodoro(time.toClock())
        is ObserveState.State.Break -> NotificationState.Break(time.toClock())
        else -> null
    }
}
