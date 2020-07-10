package io.github.gianpamx.pdd.room

import io.github.gianpamx.NotMockedException
import io.github.gianpamx.pdd.room.entity.StateLog
import kotlinx.coroutines.flow.Flow

class MockTransitionDao : TransitionDao {
    var onObserveStateLog: (() -> Flow<StateLog?>)? = null

    var onLastStateLog: (() -> StateLog?)? = null

    var insertStateLog: (() -> Unit)? = null

    override fun observeStateLog(): Flow<StateLog?> =
        onObserveStateLog?.invoke() ?: throw NotMockedException

    override suspend fun lastStateLog(): StateLog? =
        onLastStateLog?.invoke() ?: throw NotMockedException

    override fun insertStateLog(stateLog: StateLog) =
        insertStateLog?.invoke() ?: throw NotMockedException
}

