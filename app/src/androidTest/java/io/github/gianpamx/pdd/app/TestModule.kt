package io.github.gianpamx.pdd.app

import dagger.Module
import dagger.Provides
import io.github.gianpamx.pdd.domain.api.MockTimeApi
import io.github.gianpamx.pdd.domain.api.PersistenceApi
import io.github.gianpamx.pdd.domain.api.TimeApi
import io.github.gianpamx.pdd.room.MockTransitionDao
import io.github.gianpamx.pdd.room.RoomPersistenceApi
import io.github.gianpamx.pdd.room.TransitionDao
import javax.inject.Singleton

@Module
class TestModule {
    @Provides
    fun provideTimeApi(mockTimeApi: MockTimeApi): TimeApi = mockTimeApi

    @Provides
    @Singleton
    fun provideMockTimeApi() = MockTimeApi()

    @Provides
    fun providePersistenceApi(transitionDao: TransitionDao): PersistenceApi =
        RoomPersistenceApi(transitionDao)

    @Provides
    fun provideTransitionDao(mockTransitionDao: MockTransitionDao): TransitionDao =
        mockTransitionDao

    @Provides
    @Singleton
    fun provideMockTransitionDao() = MockTransitionDao()
}
