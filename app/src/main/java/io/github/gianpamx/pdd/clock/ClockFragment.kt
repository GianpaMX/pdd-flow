package io.github.gianpamx.pdd.clock

import android.content.Intent
import android.os.Bundle
import android.provider.Settings.ACTION_NOTIFICATION_POLICY_ACCESS_SETTINGS
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat.getColor
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.google.android.material.composethemeadapter.MdcTheme
import com.google.android.material.snackbar.Snackbar
import io.github.gianpamx.pdd.R
import io.github.gianpamx.pdd.clicks
import io.github.gianpamx.pdd.clock.ClockNavDirection.AskDndPermission
import io.github.gianpamx.pdd.clock.ClockViewState.Break
import io.github.gianpamx.pdd.clock.ClockViewState.Pomodoro
import io.github.gianpamx.pdd.clock.composable.BarClock
import io.github.gianpamx.pdd.clock.composable.Clock
import io.github.gianpamx.pdd.databinding.ClockFragmentBinding
import io.github.gianpamx.pdd.notification.NOTIFICATION_SERVICE_COMMAND
import io.github.gianpamx.pdd.notification.NotificationCommand
import io.github.gianpamx.pdd.notification.NotificationCommand.HIDE
import io.github.gianpamx.pdd.notification.NotificationCommand.SHOW
import io.github.gianpamx.pdd.notification.NotificationService
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import timber.log.Timber
import javax.inject.Inject

private const val START_BUTTON = 0
private const val STOP_BUTTON = 1
private const val TAKE_BUTTON = 2

class ClockFragment @Inject constructor(
    viewModelFactory: ViewModelProvider.Factory
) : Fragment() {
    private var binding: ClockFragmentBinding? = null
    private val viewModel: ClockViewModel by viewModels { viewModelFactory }

    private val mutableState: MutableState<ClockViewState> = mutableStateOf(ClockViewState.Idle())

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ) = ClockFragmentBinding.inflate(inflater)
        .also { binding = it }
        .root

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding?.startButton
            ?.clicks()
            ?.onEach { viewModel.start() }
            ?.launchIn(lifecycleScope)

        binding?.stopButton
            ?.clicks()
            ?.onEach { viewModel.stop() }
            ?.launchIn(lifecycleScope)

        binding?.takeButton
            ?.clicks()
            ?.onEach { viewModel.take() }
            ?.launchIn(lifecycleScope)

        viewModel.navDirections
            .onEach { onNavDirection(it) }
            .launchIn(lifecycleScope)

        viewModel.errors
            .onEach { showError(it) }
            .launchIn(lifecycleScope)

        viewModel.viewState.observe(viewLifecycleOwner, Observer {
            binding?.root?.updateUi(it)
            binding?.buttonFlipper?.displayedChild = when (it) {
                is ClockViewState.Idle,
                is Break -> START_BUTTON
                is Pomodoro -> STOP_BUTTON
                is ClockViewState.Done -> TAKE_BUTTON
            }
        })

        binding?.composeView?.setContent {
            val state: ClockViewState by remember { mutableState }

            MdcTheme(setTextColors = true) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Clock(time = state.clock())
                    Spacer(modifier = Modifier.height(36.dp))
                    BarClock(state = state, 25 * 60)
                }
            }
        }
    }

    private fun View.updateUi(clockViewState: ClockViewState) {
        updateClock(clockViewState)
        updateColors(clockViewState)
    }

    private fun updateClock(clockViewState: ClockViewState) {
        mutableState.value = clockViewState
    }

    private fun View.updateColors(clockViewState: ClockViewState) {
        if (clockViewState is Break)
            updateColors(R.color.breakColor, R.color.darkBreakColor)
        else
            updateColors(R.color.pomodoroColor, R.color.darkPomodoroColor)
    }

    private fun View.updateColors(backgroundColor: Int, statusBarColor: Int) {
        setBackgroundColor(getColor(context, backgroundColor))
        requireActivity().window.statusBarColor = getColor(context, statusBarColor)
    }

    private fun onNavDirection(direction: ClockNavDirection) {
        if (direction is AskDndPermission) {
            binding?.root?.let { rootView ->
                Snackbar.make(
                    rootView,
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

    private fun showError(throwable: Throwable) = binding?.root?.run {
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
