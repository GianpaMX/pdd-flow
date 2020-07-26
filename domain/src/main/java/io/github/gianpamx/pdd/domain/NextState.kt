package io.github.gianpamx.pdd.domain

import io.github.gianpamx.pdd.domain.api.TransitionApi
import io.github.gianpamx.pdd.domain.api.TimeApi
import io.github.gianpamx.pdd.domain.entity.Action
import io.github.gianpamx.pdd.domain.entity.State

class NextState(
    private val transitionApi: TransitionApi,
    private val timeApi: TimeApi
) {
    suspend operator fun invoke(action: Action): State {
        val currentState = transitionApi
            .getLastTransition()
            ?.state
            ?: throw IllegalNullStateException

        val nextState = when {
            currentState == State.IDLE && action == Action.START -> State.POMODORO
            currentState == State.POMODORO && action == Action.STOP -> State.IDLE
            currentState == State.POMODORO && action == Action.COMPLETE -> State.DONE
            currentState == State.DONE && action == Action.TAKE -> State.BREAK
            currentState == State.BREAK && action == Action.START -> State.POMODORO
            currentState == State.BREAK && action == Action.COMPLETE -> State.IDLE
            else -> throw IllegalActionException(currentState, action)
        }

        transitionApi.newTransition(timeApi.now(), nextState)

        return nextState
    }
}

object IllegalNullStateException : IllegalStateException()
class IllegalActionException(state: State, action: Action) :
    IllegalArgumentException("action ${action.name} on ${state.name} state")
