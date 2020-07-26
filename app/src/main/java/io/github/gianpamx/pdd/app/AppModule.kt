package io.github.gianpamx.pdd.app

import android.app.Application
import android.content.Context
import dagger.Module
import dagger.Provides
import io.github.gianpamx.pdd.domain.NextState
import io.github.gianpamx.pdd.domain.ObserveState
import io.github.gianpamx.pdd.notification.NotificationViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers

@Module
class AppModule {
    @Provides
    fun provideDefaultDispatcher(): CoroutineDispatcher = Dispatchers.Default

    @Provides
    fun provideApplicationContext(application: Application): Context = application

    @Provides
    fun provideNotificationViewModel(
        observeState: ObserveState,
        defaultDispatcher: CoroutineDispatcher
    ) = NotificationViewModel(observeState, defaultDispatcher)
}
