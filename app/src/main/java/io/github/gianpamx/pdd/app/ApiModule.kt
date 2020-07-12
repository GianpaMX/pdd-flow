package io.github.gianpamx.pdd.app

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import io.github.gianpamx.pdd.domain.api.PersistenceApi
import io.github.gianpamx.pdd.domain.api.TimeApi
import io.github.gianpamx.pdd.room.AppDatabase
import io.github.gianpamx.pdd.room.RoomPersistenceApi
import io.github.gianpamx.pdd.room.StateLogDao
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.consumeAsFlow
import kotlinx.coroutines.flow.map
import org.threeten.bp.Instant

@Module
class ApiModule {
    @Provides
    @AppScope
    fun provideTimeApi() = object : TimeApi {
        override fun now() = Instant.now().epochSecond.toInt()
        override fun ticker(): Flow<Int> = kotlinx.coroutines.channels
            .ticker(1_000)
            .consumeAsFlow()
            .map { now() }
    }

    @Provides
    @AppScope
    fun providePersistenceApi(stateLogDao: StateLogDao): PersistenceApi =
        RoomPersistenceApi(stateLogDao)

    @Provides
    @AppScope
    fun provideStateLogDao(appDatabase: AppDatabase): StateLogDao = appDatabase.stateLogDao()

    @Provides
    @AppScope
    fun provideAppDatabase(applicationContext: Context) =
        Room.databaseBuilder(applicationContext, AppDatabase::class.java, "pdd-flow")
            .fallbackToDestructiveMigration()
            .build()

}
