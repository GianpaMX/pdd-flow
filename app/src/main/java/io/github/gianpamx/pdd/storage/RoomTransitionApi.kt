package io.github.gianpamx.pdd.storage

import io.github.gianpamx.pdd.domain.api.TransitionApi
import io.github.gianpamx.pdd.domain.entity.State
import io.github.gianpamx.pdd.domain.entity.Transition
import io.github.gianpamx.pdd.storage.entity.StateLog
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.map

class RoomTransitionApi(private val stateLogDao: StateLogDao) : TransitionApi {
    override fun observeTransitionLog(): Flow<Transition> = stateLogDao
        .observeLast()
        .map { it?.toTransition() }
        .filterNotNull()

    override suspend fun getLastTransition() = stateLogDao.last()?.toTransition()

    override suspend fun newTransition(timestamp: Int, state: State) {
        stateLogDao.insert(StateLog(0L, timestamp, state.name))
    }

    private fun StateLog.toTransition() = Transition(
        state = State.valueOf(state),
        timestamp = timestamp
    )
}
