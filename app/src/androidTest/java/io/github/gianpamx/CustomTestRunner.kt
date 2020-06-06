package io.github.gianpamx

import android.app.Application
import android.content.Context
import androidx.test.runner.AndroidJUnitRunner
import io.github.gianpamx.pdd.app.TestApp

class CustomTestRunner : AndroidJUnitRunner() {
    @Throws(
        InstantiationException::class,
        IllegalAccessException::class,
        ClassNotFoundException::class
    )
    override fun newApplication(cl: ClassLoader, className: String, context: Context): Application {
        return super.newApplication(cl, TestApp::class.java.name, context)
    }
}
