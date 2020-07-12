package io.github.gianpamx.pdd.room

import io.github.gianpamx.pdd.domain.api.PersistenceApi
import io.github.gianpamx.pdd.domain.entity.State
import io.github.gianpamx.pdd.domain.entity.Transition
import io.github.gianpamx.pdd.room.entity.StateLog
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.map

class RoomPersistenceApi(private val stateLogDao: StateLogDao) : PersistenceApi {
    override fun observeStateLog(): Flow<Transition> = stateLogDao
        .observeLast()
        .map { it?.toTransition() }
        .filterNotNull()

    override suspend fun getLastStateLog() = stateLogDao.last()?.toTransition()

    override suspend fun newStateLog(timestamp: Int, state: State) {
        stateLogDao.insert(StateLog(0L, timestamp, state.name))
    }

    private fun StateLog.toTransition() = Transition(
        state = State.valueOf(state),
        timestamp = timestamp
    )
}
