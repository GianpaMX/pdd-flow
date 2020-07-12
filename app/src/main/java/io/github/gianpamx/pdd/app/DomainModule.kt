package io.github.gianpamx.pdd.app

import dagger.Module
import dagger.Provides
import io.github.gianpamx.pdd.domain.InitApp
import io.github.gianpamx.pdd.domain.NextState
import io.github.gianpamx.pdd.domain.ObserveState
import io.github.gianpamx.pdd.domain.StartPomodoro
import io.github.gianpamx.pdd.domain.api.PersistenceApi
import io.github.gianpamx.pdd.domain.api.TimeApi

@Module
class DomainModule {
    @Provides
    @AppScope
    fun provideObserveState(persistenceApi: PersistenceApi, timeApi: TimeApi) =
        ObserveState(persistenceApi, timeApi)

    @Provides
    @AppScope
    fun provideInitApp(
        persistenceApi: PersistenceApi,
        timeApi: TimeApi,
        nextState: NextState
    ) = InitApp(nextState, persistenceApi, timeApi)

    @Provides
    @AppScope
    fun provideNextState() = NextState()

    @Provides
    @AppScope
    fun provideStartPomodoro(
        persistenceApi: PersistenceApi,
        timeApi: TimeApi,
        nextState: NextState
    ) = StartPomodoro(nextState, persistenceApi, timeApi)
}
