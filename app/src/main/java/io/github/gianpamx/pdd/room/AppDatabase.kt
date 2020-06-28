package io.github.gianpamx.pdd.room

import androidx.room.Database
import androidx.room.RoomDatabase
import io.github.gianpamx.pdd.room.entity.StateLog

@Database(entities = [StateLog::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun transitionDao(): TransitionDao
}
