package io.github.gianpamx.pdd.app

import dagger.Component
import io.github.gianpamx.pdd.clock.ClockFragmentTest
import javax.inject.Singleton

@Component(
    modules = [
        AppModule::class,
        FactoriesModule::class,
        BindingModule::class,
        TestModule::class
    ]
)
@Singleton
interface TestComponent : AppComponent {
    fun inject(clockFragmentTest: ClockFragmentTest)
}
