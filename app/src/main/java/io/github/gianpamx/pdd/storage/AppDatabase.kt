package io.github.gianpamx.pdd.storage

import androidx.room.Database
import androidx.room.RoomDatabase
import io.github.gianpamx.pdd.storage.entity.StateLog

@Database(entities = [StateLog::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun stateLogDao(): StateLogDao
}
