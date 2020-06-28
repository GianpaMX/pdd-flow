package io.github.gianpamx.pdd.room

import io.github.gianpamx.pdd.domain.api.PersistenceApi
import io.github.gianpamx.pdd.domain.entity.State
import io.github.gianpamx.pdd.domain.entity.Transition
import io.github.gianpamx.pdd.room.entity.StateLog
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.map

class RoomPersistenceApi(private val transitionDao: TransitionDao) : PersistenceApi {
    override fun observeStateLog(): Flow<Transition> = transitionDao
        .observeStateLog()
        .map { it?.toTransition() }
        .filterNotNull()

    override suspend fun getLastStateLog() = transitionDao.lastStateLog()?.toTransition()

    override suspend fun newStateLog(timestamp: Int, state: State) {
        transitionDao.insertStateLog(StateLog(0, timestamp, state.name))
    }

    private fun StateLog.toTransition() = Transition(
        state = State.valueOf(state),
        timestamp = timestamp
    )
}
