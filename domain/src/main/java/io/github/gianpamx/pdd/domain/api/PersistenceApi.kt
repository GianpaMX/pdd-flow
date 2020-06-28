package io.github.gianpamx.pdd.domain.api

import io.github.gianpamx.pdd.domain.entity.State
import io.github.gianpamx.pdd.domain.entity.Transition
import kotlinx.coroutines.flow.Flow

interface PersistenceApi {
    fun observeStateLog(): Flow<Transition>
    suspend fun getLastStateLog(): Transition?
    suspend fun newStateLog(timestamp: Int, state: State)
}
