package io.github.gianpamx.pdd.notification

import androidx.lifecycle.asLiveData
import io.github.gianpamx.pdd.domain.NextState
import io.github.gianpamx.pdd.domain.ObserveState
import io.github.gianpamx.pdd.domain.ObserveState.State
import io.github.gianpamx.pdd.domain.entity.Action
import io.github.gianpamx.pdd.toClock
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.channels.BroadcastChannel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext

class NotificationViewModel(
    observeState: ObserveState,
    private val nextState: NextState,
    private val defaultDispatcher: CoroutineDispatcher
) {
    private val viewModelScope = object : CoroutineScope {
        override val coroutineContext: CoroutineContext =
            SupervisorJob() + Dispatchers.Main.immediate
    }

    private val errorChannel = BroadcastChannel<Throwable>(Channel.CONFLATED)

    val errors = errorChannel.asFlow()

    val notificationState = observeState()
        .onEach {
            when (it) {
                is State.Pomodoro, is State.Break -> it.hasTimeUp { complete() }
            }
        }
        .map { it.toNotificationState() }
        .filterNotNull()
        .flowOn(defaultDispatcher)
        .asLiveData(defaultDispatcher)

    private fun complete() = launchNextState(Action.COMPLETE)

    private fun launchNextState(action: Action) {
        viewModelScope.launch(defaultDispatcher) {
            try {
                nextState(action)
            } catch (t: Throwable) {
                errorChannel.send(t)
            }
        }
    }

    private fun State.toNotificationState() = when (this) {
        is State.Pomodoro -> NotificationState.Pomodoro(time.toClock())
        is State.Break -> NotificationState.Break(time.toClock())
        else -> null
    }
}
