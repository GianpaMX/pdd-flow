package io.github.gianpamx.pdd.app

import dagger.Module
import dagger.Provides
import io.github.gianpamx.pdd.domain.ObserveState
import io.github.gianpamx.pdd.domain.ObserveStateUseCase

@Module
class DomainModule {
    @Provides
    fun provideObserveState(): ObserveState = ObserveStateUseCase()
}
