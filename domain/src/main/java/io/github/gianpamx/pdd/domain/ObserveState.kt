package io.github.gianpamx.pdd.domain

import io.github.gianpamx.pdd.domain.api.StorageApi
import io.github.gianpamx.pdd.domain.api.TimeApi
import io.github.gianpamx.pdd.domain.api.TransitionApi
import io.github.gianpamx.pdd.domain.api.ZenModeApi
import io.github.gianpamx.pdd.domain.entity.Action
import io.github.gianpamx.pdd.domain.entity.State.BREAK
import io.github.gianpamx.pdd.domain.entity.State.DONE
import io.github.gianpamx.pdd.domain.entity.State.IDLE
import io.github.gianpamx.pdd.domain.entity.State.POMODORO
import io.github.gianpamx.pdd.domain.entity.Transition
import io.github.gianpamx.pdd.domain.entity.ZenMode
import kotlinx.coroutines.channels.BroadcastChannel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.onEach

private const val POMODORO_LENGTH = 25 * 60
private const val BREAK_LENGTH = 5 * 60

class ObserveState(
    private val nextState: NextState,
    private val transitionApi: TransitionApi,
    private val timeApi: TimeApi,
    private val zenModeApi: ZenModeApi,
    private val storageApi: StorageApi,
    private val errorChannel: BroadcastChannel<Throwable>
) {
    sealed class State {
        object Idle : State() {
            const val time: Int = POMODORO_LENGTH
        }

        data class Pomodoro(val time: Int) : State()
        object Done : State()
        data class Break(val time: Int) : State()
    }

    operator fun invoke(): Flow<State> = timeApi
        .ticker()
        .combine(transitionApi.observeTransitionLog()) { now, transition ->
            transition.toState(now)
        }
        .distinctUntilChanged()
        .onEach {
            try {
                if (it is State.Pomodoro) turnOnZenMode() else turnOffZenMode()
            } catch (e: Exception) {
                errorChannel.send(e)
            }
        }
        .onEach {
            if (it is State.Pomodoro || it is State.Break) it.doOnTimeUp {
                nextState(Action.COMPLETE)
            }
        }

    private suspend fun State.doOnTimeUp(block: suspend () -> Unit) {
        when (this) {
            is State.Pomodoro -> if (this.time == 0) block.invoke()
            is State.Break -> if (this.time == 0) block.invoke()
        }
    }

    private fun turnOnZenMode() {
        if (zenModeApi.mode != ZenMode.AlarmsOnly) {
            storageApi.originalMode = zenModeApi.mode
            zenModeApi.mode = ZenMode.AlarmsOnly
        }
    }

    private fun turnOffZenMode() {
        storageApi.originalMode?.let {
            zenModeApi.mode = it
        }
    }

    private fun Transition.toState(now: Int) = when (state) {
        IDLE -> State.Idle
        POMODORO -> State.Pomodoro(timestamp + POMODORO_LENGTH - now)
        DONE -> State.Done
        BREAK -> State.Break(timestamp + BREAK_LENGTH - now)
    }
}
