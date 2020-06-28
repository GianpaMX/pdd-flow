package io.github.gianpamx.pdd.domain

import io.github.gianpamx.pdd.domain.api.PersistenceApi
import io.github.gianpamx.pdd.domain.api.TimeApi
import io.github.gianpamx.pdd.domain.entity.State.BREAK
import io.github.gianpamx.pdd.domain.entity.State.DONE
import io.github.gianpamx.pdd.domain.entity.State.IDLE
import io.github.gianpamx.pdd.domain.entity.State.POMODORO
import io.github.gianpamx.pdd.domain.entity.Transition
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class ObserveState(
    private val persistenceApi: PersistenceApi,
    private val timeApi: TimeApi
) {
    sealed class State {
        object Idle : State() {
            const val time: Int = 25 * 60
        }

        data class Pomodoro(val time: Int) : State()
        object Done : State()
        data class Break(val time: Int) : State()
    }

    operator fun invoke(): Flow<State> = persistenceApi.observeStateLog().map { it.toState() }

    private fun Transition.toState() = when (state) {
        IDLE -> State.Idle
        POMODORO -> State.Pomodoro(timeApi.now() - timestamp)
        DONE -> State.Done
        BREAK -> State.Break(timeApi.now() - timestamp)
    }
}
