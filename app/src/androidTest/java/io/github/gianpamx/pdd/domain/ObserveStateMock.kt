package io.github.gianpamx.pdd.domain

import io.github.gianpamx.pdd.domain.ObserveState.State
import kotlinx.coroutines.flow.Flow

class ObserveStateMock : ObserveState {
    var onInvoke: Flow<State>? = null

    override fun invoke(): Flow<State> = onInvoke ?: throw NotMockedException
}

object NotMockedException : Exception()
