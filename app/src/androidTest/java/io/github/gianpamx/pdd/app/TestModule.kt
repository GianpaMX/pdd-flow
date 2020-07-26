package io.github.gianpamx.pdd.app

import android.app.Application
import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import io.github.gianpamx.pdd.domain.api.MockTimeApi
import io.github.gianpamx.pdd.domain.api.TransitionApi
import io.github.gianpamx.pdd.domain.api.TimeApi
import io.github.gianpamx.pdd.room.AppDatabase
import io.github.gianpamx.pdd.room.RoomTransitionApi
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
    fun providePersistenceApi(stateLogDao: StateLogDao): TransitionApi =
        RoomTransitionApi(stateLogDao)

    @Provides
    @AppScope
    fun provideStateLogDao(appDatabase: AppDatabase) = appDatabase.stateLogDao()

    /**
     * New instance for every test
     */
    @Provides
    fun provideAppDatabase(applicationContext: Context) =
        Room.inMemoryDatabaseBuilder(applicationContext, AppDatabase::class.java)
            .fallbackToDestructiveMigration()
            .build()
}
