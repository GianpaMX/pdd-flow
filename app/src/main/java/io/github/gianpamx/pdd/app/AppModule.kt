package io.github.gianpamx.pdd.app

import androidx.fragment.app.FragmentFactory
import dagger.Module

@Module
class AppModule {
    fun provideFragmentFactory() : FragmentFactory = object : FragmentFactory() {

    }
}
