package io.github.gianpamx.pdd.domain

import io.github.gianpamx.pdd.domain.api.PersistenceApi
import io.github.gianpamx.pdd.domain.api.TimeApi
import io.github.gianpamx.pdd.domain.entity.Action
import io.github.gianpamx.pdd.domain.entity.State

class StartPomodoro(
    private val nextState: NextState,
    private val persistenceApi: PersistenceApi,
    private val timeApi: TimeApi
) {
    suspend operator fun invoke() {
        persistenceApi.getLastStateLog()?.let {
            if (it.state != State.IDLE) throw NotIdleStateException
        } ?: throw NullStateException("Last State is null")

        val pomodoroState = nextState(State.IDLE, Action.START)

        persistenceApi.newStateLog(timeApi.now(), pomodoroState)
    }
}

object NotIdleStateException : IllegalStateException()

class NullStateException(message: String) : IllegalStateException(message)
