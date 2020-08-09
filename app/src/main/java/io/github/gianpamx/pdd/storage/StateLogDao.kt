package io.github.gianpamx.pdd.storage

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import io.github.gianpamx.pdd.storage.entity.StateLog
import kotlinx.coroutines.flow.Flow

@Dao
interface StateLogDao {
    @Query("SELECT * FROM StateLog ORDER BY id DESC LIMIT 1")
    fun observeLast(): Flow<StateLog?>

    @Query("SELECT * FROM StateLog ORDER BY id DESC LIMIT 1")
    suspend fun last(): StateLog?

    @Insert
    suspend fun insert(stateLog: StateLog)

    @Insert
    fun insertBlocking(stateLog: StateLog)
}
