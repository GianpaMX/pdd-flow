package io.github.gianpamx.pdd.domain

import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.whenever
import io.github.gianpamx.pdd.domain.api.TransitionApi
import io.github.gianpamx.pdd.domain.api.TimeApi
import io.github.gianpamx.pdd.domain.entity.Action
import io.github.gianpamx.pdd.domain.entity.State
import io.github.gianpamx.pdd.dummyTransition
import kotlinx.coroutines.test.runBlockingTest
import org.assertj.core.api.Assertions.assertThat
import org.junit.Before
import org.junit.Test

class NextStateTest {
    private val transitionApi: TransitionApi = mock()
    private val timeApi: TimeApi = mock()

    private lateinit var nextState: NextState

    @Before
    fun setUp() {
        nextState = NextState(transitionApi, timeApi)
    }

    @Test(expected = IllegalNullStateException::class)
    fun `Init App should have been executed before`() = runBlockingTest {
        whenever(transitionApi.getLastTransition()).thenReturn(null)

        nextState.invoke(Action.START)

        // assert IllegalNullStateException
    }

    @Test(expected = IllegalActionException::class)
    fun `Invalid Stop on Idle State`() = runBlockingTest {
        whenever(transitionApi.getLastTransition()).thenReturn(dummyTransition(state = State.IDLE))

        nextState.invoke(Action.STOP)

        // assert IllegalActionException
    }

    @Test
    fun `Start Pomodoro`() = runBlockingTest {
        whenever(transitionApi.getLastTransition()).thenReturn(dummyTransition(state = State.IDLE))

        val state = nextState.invoke(Action.START)

        assertThat(state).isEqualTo(State.POMODORO)
    }

    @Test
    fun `Stop Pomodoro`() = runBlockingTest {
        whenever(transitionApi.getLastTransition()).thenReturn(dummyTransition(state = State.POMODORO))

        val state = nextState.invoke(Action.STOP)

        assertThat(state).isEqualTo(State.IDLE)
    }

    @Test
    fun `Complete Pomodoro`() = runBlockingTest {
        whenever(transitionApi.getLastTransition()).thenReturn(dummyTransition(state = State.POMODORO))

        val state = nextState.invoke(Action.COMPLETE)

        assertThat(state).isEqualTo(State.DONE)
    }

    @Test
    fun `Take Break`() = runBlockingTest {
        whenever(transitionApi.getLastTransition()).thenReturn(dummyTransition(state = State.DONE))

        val state = nextState.invoke(Action.TAKE)

        assertThat(state).isEqualTo(State.BREAK)
    }

    @Test
    fun `Start Pomodoro from Break`() = runBlockingTest {
        whenever(transitionApi.getLastTransition()).thenReturn(dummyTransition(state = State.BREAK))

        val state = nextState.invoke(Action.START)

        assertThat(state).isEqualTo(State.POMODORO)
    }

    @Test
    fun `Complete Break`() = runBlockingTest {
        whenever(transitionApi.getLastTransition()).thenReturn(dummyTransition(state = State.BREAK))

        val state = nextState.invoke(Action.COMPLETE)

        assertThat(state).isEqualTo(State.IDLE)
    }
}
