package io.github.gianpamx.pdd.app

import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap
import io.github.gianpamx.pdd.clock.ClockFragment
import io.github.gianpamx.pdd.clock.ClockViewModel

@Module
abstract class BindingModule {
    @Binds
    @IntoMap
    @FragmentKey(ClockFragment::class)
    abstract fun bindClockFragment(fragment: ClockFragment): Fragment

    @Binds
    abstract fun bindClockViewModel(viewModel: ClockViewModel): ViewModel
}
