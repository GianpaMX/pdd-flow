package io.github.gianpamx.pdd.domain

import io.github.gianpamx.pdd.domain.entity.Action
import io.github.gianpamx.pdd.domain.entity.State

class NextState {
    operator fun invoke(currentState: State?, action: Action?): State = when {
        currentState == State.IDLE && action == Action.START -> State.POMODORO
        currentState == State.POMODORO && action == Action.STOP -> State.IDLE
        else -> State.IDLE
    }
}
