package io.github.gianpamx.pdd.domain

import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.whenever
import io.github.gianpamx.pdd.domain.api.PersistenceApi
import io.github.gianpamx.pdd.domain.api.TimeApi
import io.github.gianpamx.pdd.domain.entity.State
import io.github.gianpamx.pdd.domain.entity.Transition
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.runBlockingTest
import org.assertj.core.api.Assertions.assertThat
import org.junit.Before
import org.junit.Test

@ExperimentalCoroutinesApi
class ObserveStateTest {
    private val persistenceApi: PersistenceApi = mock()
    private val timeApi: TimeApi = mock()

    lateinit var observeState: ObserveState

    @Before
    fun setUp() {
        observeState = ObserveState(persistenceApi, timeApi)
    }

    @Test
    fun `Idle State`() = runBlockingTest {
        whenever(persistenceApi.observeStateLog()).thenReturn(flowOf(Transition(State.IDLE, 0)))

        val result = observeState.invoke().toList()

        assertThat(result).isEqualTo(listOf(ObserveState.State.Idle))
    }

    @Test
    fun `Pomodoro State`() = runBlockingTest {
        whenever(persistenceApi.observeStateLog()).thenReturn(flowOf(Transition(State.POMODORO, 0)))
        whenever(timeApi.now()).thenReturn(0)

        val result = observeState.invoke().toList()

        assertThat(result).isEqualTo(listOf(ObserveState.State.Pomodoro(0)))
    }

    @Test
    fun `Done State`() = runBlockingTest {
        whenever(persistenceApi.observeStateLog()).thenReturn(flowOf(Transition(State.DONE, 0)))

        val result = observeState.invoke().toList()

        assertThat(result).isEqualTo(listOf(ObserveState.State.Done))
    }

    @Test
    fun `Break State`() = runBlockingTest {
        whenever(persistenceApi.observeStateLog()).thenReturn(flowOf(Transition(State.BREAK, 0)))
        whenever(timeApi.now()).thenReturn(0)

        val result = observeState.invoke().toList()

        assertThat(result).isEqualTo(listOf(ObserveState.State.Break(0)))
    }
}
