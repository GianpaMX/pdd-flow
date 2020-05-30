package io.github.gianpamx.pdd.app

import android.app.Application
import dagger.BindsInstance
import dagger.Component
import io.github.gianpamx.pdd.MainActivity

@AppScope
@Component(
    modules = [AppModule::class]
)
interface AppComponent {
    fun inject(app: App)

    fun inject(app: MainActivity)

    @Component.Builder
    interface Builder {
        @BindsInstance
        fun application(application: Application): Builder

        fun build(): AppComponent
    }
}
