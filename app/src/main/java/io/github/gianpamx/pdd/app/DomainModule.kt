package io.github.gianpamx.pdd.app

import dagger.Module
import dagger.Provides
import io.github.gianpamx.pdd.domain.ObserveState

@Module
class DomainModule {
    @Provides
    fun provideObserveState() = ObserveState()
}
