package io.github.gianpamx.pdd.room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import io.github.gianpamx.pdd.room.entity.StateLog
import kotlinx.coroutines.flow.Flow

@Dao
interface TransitionDao {
    @Query("SELECT * FROM StateLog ORDER BY id DESC LIMIT 1")
    fun observeStateLog(): Flow<StateLog?>

    @Query("SELECT * FROM StateLog ORDER BY id DESC LIMIT 1")
    suspend fun lastStateLog(): StateLog?

    @Insert
    fun insertStateLog(stateLog: StateLog)
}
