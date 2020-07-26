package io.github.gianpamx.pdd.app

import dagger.Module
import dagger.Provides
import io.github.gianpamx.pdd.domain.InitApp
import io.github.gianpamx.pdd.domain.NextState
import io.github.gianpamx.pdd.domain.ObserveState
import io.github.gianpamx.pdd.domain.api.TransitionApi
import io.github.gianpamx.pdd.domain.api.TimeApi

@Module
class DomainModule {
    @Provides
    @AppScope
    fun provideObserveState(
        nextState: NextState,
        transitionApi: TransitionApi,
        timeApi: TimeApi
    ) =
        ObserveState(nextState, transitionApi, timeApi)

    @Provides
    @AppScope
    fun provideInitApp(transitionApi: TransitionApi, timeApi: TimeApi) =
        InitApp(transitionApi, timeApi)

    @Provides
    @AppScope
    fun provideNextState(transitionApi: TransitionApi, timeApi: TimeApi) =
        NextState(transitionApi, timeApi)
}
