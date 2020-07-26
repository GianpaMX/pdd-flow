package io.github.gianpamx.pdd.domain

import io.github.gianpamx.pdd.domain.api.TransitionApi
import io.github.gianpamx.pdd.domain.api.TimeApi
import io.github.gianpamx.pdd.domain.entity.Action
import io.github.gianpamx.pdd.domain.entity.State.BREAK
import io.github.gianpamx.pdd.domain.entity.State.DONE
import io.github.gianpamx.pdd.domain.entity.State.IDLE
import io.github.gianpamx.pdd.domain.entity.State.POMODORO
import io.github.gianpamx.pdd.domain.entity.Transition
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.onEach

private const val POMODORO_LENGTH = 25 * 60
private const val BREAK_LENGTH = 5 * 60

class ObserveState(
    private val nextState: NextState,
    private val transitionApi: TransitionApi,
    private val timeApi: TimeApi
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
            when (it) {
                is State.Pomodoro, is State.Break -> it.ifTimeUp { nextState(Action.COMPLETE) }
            }
        }

    private suspend fun State.ifTimeUp(block: suspend () -> Unit) {
        when (this) {
            is State.Pomodoro -> if (this.time == 0) block.invoke()
            is State.Break -> if (this.time == 0) block.invoke()
        }
    }

    private fun Transition.toState(now: Int) = when (state) {
        IDLE -> State.Idle
        POMODORO -> State.Pomodoro(timestamp + POMODORO_LENGTH - now)
        DONE -> State.Done
        BREAK -> State.Break(timestamp + BREAK_LENGTH - now)
    }
}
