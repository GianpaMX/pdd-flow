package io.github.gianpamx.pdd.app

import dagger.Module
import dagger.Provides
import io.github.gianpamx.pdd.domain.InitApp
import io.github.gianpamx.pdd.domain.NextState
import io.github.gianpamx.pdd.domain.ObserveState
import io.github.gianpamx.pdd.domain.api.PersistenceApi
import io.github.gianpamx.pdd.domain.api.TimeApi

@Module
class DomainModule {
    @Provides
    @AppScope
    fun provideObserveState(
        nextState: NextState,
        persistenceApi: PersistenceApi,
        timeApi: TimeApi
    ) =
        ObserveState(nextState, persistenceApi, timeApi)

    @Provides
    @AppScope
    fun provideInitApp(persistenceApi: PersistenceApi, timeApi: TimeApi) =
        InitApp(persistenceApi, timeApi)

    @Provides
    @AppScope
    fun provideNextState(persistenceApi: PersistenceApi, timeApi: TimeApi) =
        NextState(persistenceApi, timeApi)
}
