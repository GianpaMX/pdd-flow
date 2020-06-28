package io.github.gianpamx.pdd.app

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import io.github.gianpamx.pdd.domain.api.PersistenceApi
import io.github.gianpamx.pdd.domain.api.TimeApi
import io.github.gianpamx.pdd.room.AppDatabase
import io.github.gianpamx.pdd.room.RoomPersistenceApi
import io.github.gianpamx.pdd.room.TransitionDao
import org.threeten.bp.Instant

@Module
class ApiModule {
    @Provides
    fun provideTimeApi() = object : TimeApi {
        override fun now() = Instant.now().epochSecond.toInt()
    }

    @Provides
    fun providePersistenceApi(transitionDao: TransitionDao): PersistenceApi =
        RoomPersistenceApi(transitionDao)

    @Provides
    fun provideTransitionDao(appDatabase: AppDatabase): TransitionDao = appDatabase.transitionDao()

    @Provides
    fun provideAppDatabase(applicationContext: Context) =
        Room.databaseBuilder(applicationContext, AppDatabase::class.java, "pdd-flow")
            .fallbackToDestructiveMigration()
            .build()

}
