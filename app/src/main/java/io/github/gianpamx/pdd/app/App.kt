package io.github.gianpamx.pdd.app

import android.app.Application

class App : Application() {
    val component: AppComponent by lazy {
        DaggerAppComponent.builder()
            .application(this)
            .build()
    }
}
