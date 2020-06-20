package io.github.gianpamx.pdd.app

import com.nhaarman.mockitokotlin2.mock
import dagger.Module
import dagger.Provides
import io.github.gianpamx.pdd.domain.ObserveState
import javax.inject.Singleton

@Module
class TestModule {
    @Provides
    @Singleton
    fun provideObserveState(): ObserveState = mock()
}
