package io.github.gianpamx.pdd.app

import android.app.Application
import dagger.BindsInstance
import dagger.Component
import io.github.gianpamx.pdd.MainActivity
import io.github.gianpamx.pdd.notification.NotificationService

@AppScope
@Component(
    modules = [
        AppModule::class,
        FactoriesModule::class,
        BindingModule::class,
        DomainModule::class,
        ApiModule::class
    ]
)
interface AppComponent {
    fun inject(app: App)

    fun inject(activity: MainActivity)

    fun inject(service: NotificationService)

    @Component.Builder
    interface Builder {
        @BindsInstance
        fun application(application: Application): Builder

        fun build(): AppComponent
    }
}
