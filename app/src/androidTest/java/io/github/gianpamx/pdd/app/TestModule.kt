package io.github.gianpamx.pdd.app

import dagger.Module
import dagger.Provides
import io.github.gianpamx.pdd.domain.ObserveState
import io.github.gianpamx.pdd.domain.ObserveStateMock
import javax.inject.Singleton

@Module
class TestModule {
    @Provides
    @Singleton
    fun provideObserveState(observeStateMock: ObserveStateMock): ObserveState = observeStateMock

    @Provides
    @Singleton
    fun provideObserveStateMock() = ObserveStateMock()
}
