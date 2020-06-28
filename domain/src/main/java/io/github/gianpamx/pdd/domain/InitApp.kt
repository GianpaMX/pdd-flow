package io.github.gianpamx.pdd.domain

import io.github.gianpamx.pdd.domain.api.PersistenceApi
import io.github.gianpamx.pdd.domain.api.TimeApi

class InitApp(
    private val nextState: NextState,
    private val persistenceApi: PersistenceApi,
    private val timeApi: TimeApi
) {
    suspend operator fun invoke() {
        if (persistenceApi.getLastStateLog() != null) return

        val initState = nextState(null, null)

        persistenceApi.newStateLog(timeApi.now(), initState)
    }
}
