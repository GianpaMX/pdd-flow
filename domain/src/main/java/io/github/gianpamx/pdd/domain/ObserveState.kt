package io.github.gianpamx.pdd.domain

import io.github.gianpamx.pdd.domain.ObserveState.State
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

interface ObserveState {
    sealed class State {
        object Idle : State() {
            const val time: Int = 25 * 60
        }

        data class Pomodoro(val time: Int) : State()
        object Done : State()
        data class Break(val time: Int) : State()
    }

    operator fun invoke(): Flow<State>
}

class ObserveStateUseCase : ObserveState {
    override operator fun invoke(): Flow<State> = flow {
        emit(State.Idle)
    }
}
