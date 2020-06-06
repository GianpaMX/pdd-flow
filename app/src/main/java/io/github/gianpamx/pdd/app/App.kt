package io.github.gianpamx.pdd.app

import android.app.Application

interface ComponentApp {
    val component: AppComponent
}

class App : Application(), ComponentApp {
    override val component: AppComponent by lazy {
        DaggerAppComponent.builder()
            .application(this)
            .build()
    }
}
