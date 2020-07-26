package io.github.gianpamx.pdd.domain

import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import io.github.gianpamx.pdd.domain.api.TransitionApi
import io.github.gianpamx.pdd.domain.api.TimeApi
import io.github.gianpamx.pdd.domain.entity.Action
import io.github.gianpamx.pdd.domain.entity.State
import io.github.gianpamx.pdd.domain.entity.Transition
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.runBlockingTest
import org.assertj.core.api.Assertions.assertThat
import org.junit.Before
import org.junit.Test

private const val POMODORO_LENGTH = 25 * 60
private const val BREAK_LENGTH = 5 * 60

@ExperimentalCoroutinesApi
class ObserveStateTest {
    private val nextState: NextState = mock()
    private val transitionApi: TransitionApi = mock()
    private val timeApi: TimeApi = mock()

    lateinit var observeState: ObserveState

    @Before
    fun setUp() {
        whenever(timeApi.ticker()).thenReturn(flowOf(0))
        observeState = ObserveState(nextState, transitionApi, timeApi)
    }

    @Test
    fun `Idle State`() = runBlockingTest {
        whenever(transitionApi.observeTransitionLog()).thenReturn(flowOf(Transition(State.IDLE, 0)))

        val result = observeState.invoke().toList()

        assertThat(result).isEqualTo(listOf(ObserveState.State.Idle))
    }

    @Test
    fun `Pomodoro State`() = runBlockingTest {
        whenever(transitionApi.observeTransitionLog()).thenReturn(flowOf(Transition(State.POMODORO, 0)))

        val result = observeState.invoke().toList()

        assertThat(result).isEqualTo(listOf(ObserveState.State.Pomodoro(POMODORO_LENGTH)))
    }

    @Test
    fun `Done State`() = runBlockingTest {
        whenever(transitionApi.observeTransitionLog()).thenReturn(flowOf(Transition(State.DONE, 0)))

        val result = observeState.invoke().toList()

        assertThat(result).isEqualTo(listOf(ObserveState.State.Done))
    }

    @Test
    fun `Break State`() = runBlockingTest {
        whenever(transitionApi.observeTransitionLog()).thenReturn(flowOf(Transition(State.BREAK, 0)))

        val result = observeState.invoke().toList()

        assertThat(result).isEqualTo(listOf(ObserveState.State.Break(BREAK_LENGTH)))
    }

    @Test
    fun `Complete break`() = runBlockingTest {
        whenever(timeApi.ticker()).thenReturn((0..BREAK_LENGTH).asFlow())
        whenever(transitionApi.observeTransitionLog()).thenReturn(flowOf(Transition(State.BREAK, 0)))
        observeState = ObserveState(nextState, transitionApi, timeApi)

        observeState.invoke().toList()

        verify(nextState).invoke(Action.COMPLETE)
    }

    @Test
    fun `Complete pomodoro`() = runBlockingTest {
        whenever(timeApi.ticker()).thenReturn((0..POMODORO_LENGTH).asFlow())
        whenever(transitionApi.observeTransitionLog()).thenReturn(flowOf(Transition(State.POMODORO, 0)))
        observeState = ObserveState(nextState, transitionApi, timeApi)

        observeState.invoke().toList()

        verify(nextState).invoke(Action.COMPLETE)
    }
}
