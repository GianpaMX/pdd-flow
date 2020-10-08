package io.github.gianpamx.pdd.app

import android.app.NotificationManager
import android.content.Context
import android.content.SharedPreferences
import androidx.room.Room
import dagger.Module
import dagger.Provides
import io.github.gianpamx.pdd.domain.api.StorageApi
import io.github.gianpamx.pdd.domain.api.TimeApi
import io.github.gianpamx.pdd.domain.api.TransitionApi
import io.github.gianpamx.pdd.domain.api.ZenModeApi
import io.github.gianpamx.pdd.storage.AppDatabase
import io.github.gianpamx.pdd.storage.RoomTransitionApi
import io.github.gianpamx.pdd.storage.SharedPreferencesStorageApi
import io.github.gianpamx.pdd.storage.StateLogDao
import io.github.gianpamx.pdd.zenmode.AndroidZenModeApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.receiveAsFlow
import org.threeten.bp.Instant

@Module
class ApiModule {
    @Provides
    fun provideTimeApi() = object : TimeApi {
        override fun now() = Instant.now().epochSecond.toInt()
        override fun ticker(): Flow<Int> = kotlinx.coroutines.channels
            .ticker(1_000, initialDelayMillis = 0L)
            .receiveAsFlow()
            .map { now() }
    }

    @Provides
    @AppScope
    fun providePersistenceApi(stateLogDao: StateLogDao): TransitionApi =
        RoomTransitionApi(stateLogDao)

    @Provides
    @AppScope
    fun provideStateLogDao(appDatabase: AppDatabase): StateLogDao = appDatabase.stateLogDao()

    @Provides
    @AppScope
    fun provideAppDatabase(applicationContext: Context) =
        Room.databaseBuilder(applicationContext, AppDatabase::class.java, "pdd-flow")
            .fallbackToDestructiveMigration()
            .build()

    @Provides
    @AppScope
    fun provideZenModeApi(notificationManager: NotificationManager): ZenModeApi =
        AndroidZenModeApi(notificationManager)

    @Provides
    @AppScope
    fun provideStorageApi(sharedPreferences: SharedPreferences): StorageApi =
        SharedPreferencesStorageApi(sharedPreferences)
}
