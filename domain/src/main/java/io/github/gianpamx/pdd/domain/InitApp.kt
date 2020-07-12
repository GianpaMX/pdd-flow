package io.github.gianpamx.pdd.domain

import io.github.gianpamx.pdd.domain.api.PersistenceApi
import io.github.gianpamx.pdd.domain.api.TimeApi
import io.github.gianpamx.pdd.domain.entity.State

class InitApp(
    private val persistenceApi: PersistenceApi,
    private val timeApi: TimeApi
) {
    suspend operator fun invoke(): State? = when {
        persistenceApi.getLastStateLog() == null -> State.IDLE.also {
            persistenceApi.newStateLog(timeApi.now(), it)
        }
        else -> null
    }
}
