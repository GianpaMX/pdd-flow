package io.github.gianpamx.pdd.clock

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.google.android.material.snackbar.Snackbar
import io.github.gianpamx.pdd.R
import io.github.gianpamx.pdd.clicks
import kotlinx.android.synthetic.main.clock_fragment.*
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import timber.log.Timber
import javax.inject.Inject

private const val START_BUTTON = 0
private const val STOP_BUTTON = 1
private const val TAKE_BUTTON = 2

class ClockFragment @Inject constructor(
    viewModelFactory: ViewModelProvider.Factory
) : Fragment(R.layout.clock_fragment) {
    private val viewModel: ClockViewModel by viewModels { viewModelFactory }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        startButton.clicks()
            .onEach { viewModel.start() }
            .launchIn(lifecycleScope)

        stopButton.clicks()
            .onEach { viewModel.stop() }
            .launchIn(lifecycleScope)

        takeButton.clicks()
            .onEach { viewModel.take() }
            .launchIn(lifecycleScope)

        viewModel.errors
            .onEach { view.show(it) }
            .launchIn(lifecycleScope)

        viewModel.viewState.observe(viewLifecycleOwner, Observer {
            clockTextView.text = it.clock
            buttonFlipper.displayedChild = when (it) {
                is ClockViewState.Idle,
                is ClockViewState.Break -> START_BUTTON
                is ClockViewState.Pomodoro -> STOP_BUTTON
                is ClockViewState.Done -> TAKE_BUTTON
            }
        })
    }

    private fun View.show(throwable: Throwable) {
        Timber.e(throwable)
        Snackbar
            .make(this, R.string.error, Snackbar.LENGTH_INDEFINITE)
            .apply {
                setAction(R.string.dismiss) { dismiss() }
            }.show()
    }
}
