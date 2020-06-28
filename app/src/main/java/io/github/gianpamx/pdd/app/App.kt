package io.github.gianpamx.pdd.app

import android.app.Application
import com.jakewharton.threetenabp.AndroidThreeTen
import io.github.gianpamx.pdd.domain.InitApp
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import javax.inject.Inject

interface ComponentApp {
    val component: AppComponent
}

class App : Application(), ComponentApp {
    override val component: AppComponent by lazy {
        DaggerAppComponent.builder()
            .application(this)
            .build()
    }

    @Inject
    lateinit var initApp: InitApp

    override fun onCreate() {
        super.onCreate()
        AndroidThreeTen.init(this)

        component.inject(this)

        GlobalScope.launch {
            initApp()
        }
    }
}
