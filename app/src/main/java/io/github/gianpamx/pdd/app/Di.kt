package io.github.gianpamx.pdd.app

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentFactory
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import dagger.MapKey
import dagger.Module
import dagger.Provides
import javax.inject.Provider
import kotlin.annotation.AnnotationTarget.FUNCTION
import kotlin.annotation.AnnotationTarget.PROPERTY_GETTER
import kotlin.annotation.AnnotationTarget.PROPERTY_SETTER
import kotlin.reflect.KClass


@MustBeDocumented
@Target(FUNCTION, PROPERTY_GETTER, PROPERTY_SETTER)
@Retention(AnnotationRetention.RUNTIME)
@MapKey
annotation class FragmentKey(val value: KClass<out Fragment>)

@Module
class FactoriesModule {
    @Provides
    fun provideFragmentFactory(creators: Map<Class<out Fragment>, @JvmSuppressWildcards Provider<Fragment>>): FragmentFactory =
        object : FragmentFactory() {
            override fun instantiate(classLoader: ClassLoader, className: String): Fragment {
                val fragmentClass = loadFragmentClass(classLoader, className)
                return creators[fragmentClass]?.get() ?: super.instantiate(classLoader, className)
            }
        }

    @Provides
    fun provideViewModelFactory(modelProvider: Provider<ViewModel>): ViewModelProvider.Factory =
        object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel?> create(modelClass: Class<T>): T = modelProvider.get() as T
        }
}
