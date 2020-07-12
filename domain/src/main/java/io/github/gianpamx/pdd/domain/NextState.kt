package io.github.gianpamx.pdd.domain

import io.github.gianpamx.pdd.domain.api.PersistenceApi
import io.github.gianpamx.pdd.domain.api.TimeApi
import io.github.gianpamx.pdd.domain.entity.Action
import io.github.gianpamx.pdd.domain.entity.State

class NextState(
    private val persistenceApi: PersistenceApi,
    private val timeApi: TimeApi
) {
    suspend operator fun invoke(action: Action): State {
        val currentState = persistenceApi
            .getLastStateLog()
            ?.state
            ?: throw IllegalNullStateException

        val nextState = when {
            currentState == State.IDLE && action == Action.START -> State.POMODORO
            currentState == State.POMODORO && action == Action.STOP -> State.IDLE
            currentState == State.POMODORO && action == Action.COMPLETE -> State.DONE
            currentState == State.DONE && action == Action.TAKE -> State.BREAK
            currentState == State.BREAK && action == Action.START -> State.POMODORO
            else -> throw IllegalActionException(currentState, action)
        }

        persistenceApi.newStateLog(timeApi.now(), nextState)

        return nextState
    }
}

object IllegalNullStateException : IllegalStateException()
class IllegalActionException(state: State, action: Action) :
    IllegalArgumentException("action ${action.name} on ${state.name} state")
