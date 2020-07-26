package io.github.gianpamx.pdd.domain.api

import io.github.gianpamx.pdd.domain.entity.State
import io.github.gianpamx.pdd.domain.entity.Transition
import kotlinx.coroutines.flow.Flow

interface TransitionApi {
    fun observeTransitionLog(): Flow<Transition>
    suspend fun getLastTransition(): Transition?
    suspend fun newTransition(timestamp: Int, state: State)
}
