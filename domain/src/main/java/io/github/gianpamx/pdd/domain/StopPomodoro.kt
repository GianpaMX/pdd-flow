package io.github.gianpamx.pdd.domain

import io.github.gianpamx.pdd.domain.api.PersistenceApi
import io.github.gianpamx.pdd.domain.api.TimeApi
import io.github.gianpamx.pdd.domain.entity.Action
import io.github.gianpamx.pdd.domain.entity.State

class StopPomodoro(
    private val nextState: NextState,
    private val persistenceApi: PersistenceApi,
    private val timeApi: TimeApi
) {
    suspend operator fun invoke() {
        persistenceApi.getLastStateLog()?.let {
            if (it.state != State.POMODORO) throw NotPomodoroStateException
        } ?: throw NullStateException("Last State is null")

        val pomodoroState = nextState(State.POMODORO, Action.STOP)

        persistenceApi.newStateLog(timeApi.now(), pomodoroState)
    }
}

object NotPomodoroStateException : IllegalStateException()
