package io.github.gianpamx.pdd.clock

import android.content.Intent
import android.os.Bundle
import android.provider.Settings.ACTION_NOTIFICATION_POLICY_ACCESS_SETTINGS
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.google.android.material.snackbar.Snackbar
import io.github.gianpamx.pdd.R
import io.github.gianpamx.pdd.clicks
import io.github.gianpamx.pdd.clock.ClockNavDirection.AskDndPermission
import io.github.gianpamx.pdd.clock.ClockViewState.Break
import io.github.gianpamx.pdd.clock.ClockViewState.Pomodoro
import io.github.gianpamx.pdd.notification.NOTIFICATION_SERVICE_COMMAND
import io.github.gianpamx.pdd.notification.NotificationCommand
import io.github.gianpamx.pdd.notification.NotificationCommand.HIDE
import io.github.gianpamx.pdd.notification.NotificationCommand.SHOW
import io.github.gianpamx.pdd.notification.NotificationService
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

        viewModel.navDirections
            .onEach { onNavDirection(it) }
            .launchIn(lifecycleScope)

        viewModel.errors
            .onEach { view.show(it) }
            .launchIn(lifecycleScope)

        viewModel.viewState.observe(viewLifecycleOwner, Observer {
            clockTextView.text = it.clock
            buttonFlipper.displayedChild = when (it) {
                is ClockViewState.Idle,
                is Break -> START_BUTTON
                is Pomodoro -> STOP_BUTTON
                is ClockViewState.Done -> TAKE_BUTTON
            }
        })
    }

    private fun onNavDirection(direction: ClockNavDirection) {
        if (direction is AskDndPermission) {
            Snackbar.make(
                requireView(),
                R.string.clock_pomodoro_permission,
                Snackbar.LENGTH_INDEFINITE
            ).apply {
                setAction(R.string.clock_pomodoro_permission_cta) {
                    startActivity(Intent(ACTION_NOTIFICATION_POLICY_ACCESS_SETTINGS))
                    dismiss()
                }
            }.show()
        }
    }

    override fun onStart() {
        super.onStart()
        requireActivity().sendNotificationCommand(HIDE)
    }

    override fun onStop() {
        super.onStop()
        when (viewModel.viewState.value) {
            is Break, is Pomodoro -> requireActivity().sendNotificationCommand(SHOW)
        }
    }

    private fun FragmentActivity.sendNotificationCommand(command: NotificationCommand) =
        startService(buildCommandIntent((command)))

    private fun FragmentActivity.buildCommandIntent(command: NotificationCommand) =
        Intent(this, NotificationService::class.java).apply {
            putExtra(NOTIFICATION_SERVICE_COMMAND, command.name)
        }

    private fun View.show(throwable: Throwable) {
        Timber.e(throwable)
        Snackbar.make(
            this,
            getString(R.string.error, throwable.localizedMessage),
            Snackbar.LENGTH_INDEFINITE
        ).apply {
            setAction(R.string.dismiss) { dismiss() }
        }.show()
    }
}
