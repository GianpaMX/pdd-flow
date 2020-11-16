package io.github.gianpamx.pdd.clock

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import io.github.gianpamx.pdd.domain.NextState
import io.github.gianpamx.pdd.domain.ObserveState
import io.github.gianpamx.pdd.domain.ObserveState.State
import io.github.gianpamx.pdd.domain.ObserveState.State.Idle.time
import io.github.gianpamx.pdd.domain.api.ZenModeApi
import io.github.gianpamx.pdd.domain.entity.Action
import io.github.gianpamx.pdd.toClock
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.channels.BroadcastChannel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.ConflatedBroadcastChannel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject

class ClockViewModel @Inject constructor(
    observeState: ObserveState,
    private val nextState: NextState,
    private val errorChannel: BroadcastChannel<Throwable>,
    private val defaultDispatcher: CoroutineDispatcher
) : ViewModel() {
    private val navigationChannel = ConflatedBroadcastChannel<ClockNavDirection>()

    val navDirections = navigationChannel.asFlow()

    val errors = errorChannel.asFlow().handleErrors()

    val viewState: LiveData<ClockViewState> = observeState()
        .map { it.toViewState() }
        .flowOn(defaultDispatcher)
        .catch { errorChannel.send(it) }
        .asLiveData(viewModelScope.coroutineContext)

    fun start() = launchNextState(Action.START)

    fun stop() = launchNextState(Action.STOP)

    fun take() = launchNextState(Action.TAKE)

    private fun launchNextState(action: Action) = viewModelScope.launch(defaultDispatcher) {
        try {
            nextState(action)
        } catch (t: Throwable) {
            errorChannel.send(t)
        }
    }

    private fun Flow<Throwable>.handleErrors(): Flow<Throwable> = flow {
        distinctUntilChanged().collect {
            if (it is ZenModeApi.AccessDeniedException) {
                navigationChannel.send(ClockNavDirection.AskDndPermission)
            } else {
                emit(it)
            }
        }
    }

    private fun State.toViewState(): ClockViewState = when (this) {
        is State.Idle -> ClockViewState.Idle(time)
        is State.Pomodoro -> ClockViewState.Pomodoro(time)
        is State.Done -> ClockViewState.Done(0)
        is State.Break -> ClockViewState.Break(time)
    }
}
