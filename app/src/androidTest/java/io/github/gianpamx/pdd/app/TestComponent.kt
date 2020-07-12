package io.github.gianpamx.pdd.app

import android.app.Application
import dagger.BindsInstance
import dagger.Component
import io.github.gianpamx.pdd.clock.ClockFragmentTest

@Component(
    modules = [
        AppModule::class,
        FactoriesModule::class,
        BindingModule::class,
        DomainModule::class,
        TestModule::class
    ]
)
@AppScope
interface TestComponent : AppComponent {
    fun inject(clockFragmentStatesTest: ClockFragmentTest)

    @Component.Builder
    interface Builder {
        @BindsInstance
        fun application(testApp: TestApp): Builder

        fun build(): TestComponent
    }
}
