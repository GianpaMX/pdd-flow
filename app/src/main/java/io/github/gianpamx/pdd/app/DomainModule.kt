package io.github.gianpamx.pdd.app

import dagger.Module
import dagger.Provides
import io.github.gianpamx.pdd.domain.InitApp
import io.github.gianpamx.pdd.domain.NextState
import io.github.gianpamx.pdd.domain.ObserveState
import io.github.gianpamx.pdd.domain.api.StorageApi
import io.github.gianpamx.pdd.domain.api.TimeApi
import io.github.gianpamx.pdd.domain.api.TransitionApi
import io.github.gianpamx.pdd.domain.api.ZenModeApi
import kotlinx.coroutines.channels.BroadcastChannel

@Module
class DomainModule {
    @Provides
    @AppScope
    fun provideObserveState(
        nextState: NextState,
        transitionApi: TransitionApi,
        timeApi: TimeApi,
        zenModeApi: ZenModeApi,
        storageApi: StorageApi,
        errorChannel: BroadcastChannel<Throwable>
    ) =
        ObserveState(nextState, transitionApi, timeApi, zenModeApi, storageApi, errorChannel)

    @Provides
    @AppScope
    fun provideInitApp(transitionApi: TransitionApi, timeApi: TimeApi) =
        InitApp(transitionApi, timeApi)

    @Provides
    @AppScope
    fun provideNextState(transitionApi: TransitionApi, timeApi: TimeApi) =
        NextState(transitionApi, timeApi)
}
