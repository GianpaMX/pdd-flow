package io.github.gianpamx.pdd.app

import android.app.Application
import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import io.github.gianpamx.pdd.domain.api.MockTimeApi
import io.github.gianpamx.pdd.domain.api.PersistenceApi
import io.github.gianpamx.pdd.domain.api.TimeApi
import io.github.gianpamx.pdd.room.AppDatabase
import io.github.gianpamx.pdd.room.RoomPersistenceApi
import io.github.gianpamx.pdd.room.StateLogDao

@Module
class TestModule {
    @Provides
    @AppScope
    fun provideApplication(testApp: TestApp): Application = testApp

    @Provides
    @AppScope
    fun provideTimeApi(mockTimeApi: MockTimeApi): TimeApi = mockTimeApi

    @Provides
    @AppScope
    fun provideMockTimeApi() = MockTimeApi()

    @Provides
    @AppScope
    fun providePersistenceApi(stateLogDao: StateLogDao): PersistenceApi =
        RoomPersistenceApi(stateLogDao)

    @Provides
    @AppScope
    fun provideStateLogDao(appDatabase: AppDatabase) = appDatabase.stateLogDao()

    @Provides
    @AppScope
    fun provideAppDatabase(applicationContext: Context) =
        Room.inMemoryDatabaseBuilder(applicationContext, AppDatabase::class.java)
            .fallbackToDestructiveMigration()
            .build()
}
