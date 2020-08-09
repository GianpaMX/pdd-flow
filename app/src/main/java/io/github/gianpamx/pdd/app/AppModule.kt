package io.github.gianpamx.pdd.app

import android.app.Application
import android.app.NotificationManager
import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.Context.NOTIFICATION_SERVICE
import android.content.SharedPreferences
import dagger.Module
import dagger.Provides
import io.github.gianpamx.pdd.domain.ObserveState
import io.github.gianpamx.pdd.notification.NotificationViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.BroadcastChannel
import kotlinx.coroutines.channels.Channel

@Module
class AppModule {
    @Provides
    @AppScope
    fun provideDefaultDispatcher(): CoroutineDispatcher = Dispatchers.Default

    @Provides
    @AppScope
    fun provideApplicationContext(application: Application): Context = application

    @Provides
    @AppScope
    fun provideNotificationViewModel(
        observeState: ObserveState,
        errorChannel: BroadcastChannel<Throwable>,
        defaultDispatcher: CoroutineDispatcher
    ) = NotificationViewModel(observeState, errorChannel, defaultDispatcher)

    @Provides
    @AppScope
    fun provideNotificationManager(context: Context) =
        context.getSystemService(NOTIFICATION_SERVICE) as NotificationManager

    @Provides
    @AppScope
    fun provideErrorChannel() = BroadcastChannel<Throwable>(Channel.CONFLATED)

    @Provides
    @AppScope
    fun provideSharedPreferences(context: Context): SharedPreferences =
        context.getSharedPreferences("internal", MODE_PRIVATE)
}
