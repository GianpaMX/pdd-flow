package io.github.gianpamx.pdd.domain

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import io.github.gianpamx.pdd.domain.api.PersistenceApi
import io.github.gianpamx.pdd.domain.api.TimeApi
import io.github.gianpamx.pdd.domain.entity.State
import io.github.gianpamx.pdd.domain.entity.Transition
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Before
import org.junit.Test

@ExperimentalCoroutinesApi
class StartPomodoroTest {
    private val nextState: NextState = mock()
    private val persistenceApi: PersistenceApi = mock()
    private val timeApi: TimeApi = mock()

    lateinit var startPomodoro: StartPomodoro

    @Before
    fun setUp() {
        startPomodoro = StartPomodoro(nextState, persistenceApi, timeApi)
    }

    @Test
    fun `Start a pomodoro from Idle state successfully`() = runBlockingTest {
        whenever(persistenceApi.getLastStateLog()).thenReturn(dummyTransition(state = State.IDLE))
        whenever(nextState.invoke(any(), any())).thenReturn(State.POMODORO)
        whenever(timeApi.now()).thenReturn(0)

        startPomodoro.invoke()

        verify(persistenceApi).newStateLog(any(), any())
    }

    @Test(expected = NullStateException::class)
    fun `Failure to start a pomodoro from null state`() = runBlockingTest {
        whenever(persistenceApi.getLastStateLog()).thenReturn(null)

        startPomodoro.invoke()

        // assert NullStateException
    }

    @Test(expected = NotIdleStateException::class)
    fun `Failure to start a pomodoro from not Idle state`() = runBlockingTest {
        whenever(persistenceApi.getLastStateLog()).thenReturn(dummyTransition(state = State.POMODORO))

        startPomodoro.invoke()

        // assert NotIdleStateException
    }
}

fun dummyTransition(
    state: State = State.IDLE,
    timestamp: Int = 0
) = Transition(
    state = state,
    timestamp = timestamp
)
