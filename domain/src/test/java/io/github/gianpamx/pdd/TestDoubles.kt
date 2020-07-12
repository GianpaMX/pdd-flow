package io.github.gianpamx.pdd

import io.github.gianpamx.pdd.domain.entity.State
import io.github.gianpamx.pdd.domain.entity.Transition


fun dummyTransition(
    state: State = State.IDLE,
    timestamp: Int = 0
) = Transition(
    state = state,
    timestamp = timestamp
)
