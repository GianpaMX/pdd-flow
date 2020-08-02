package io.github.gianpamx.pdd.clock

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import io.github.gianpamx.MainCoroutineRule
import io.github.gianpamx.TestCollector
import io.github.gianpamx.observeForTesting
import io.github.gianpamx.pdd.domain.NextState
import io.github.gianpamx.pdd.domain.ObserveState
import io.github.gianpamx.pdd.domain.ObserveState.State
import io.github.gianpamx.pdd.domain.entity.Action
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.channels.BroadcastChannel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runBlockingTest
import org.assertj.core.api.Assertions.assertThat
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestRule

private const val POMODORO_LENGTH = 25 * 60
private const val BREAK_LENGTH = 5 * 60

@ExperimentalCoroutinesApi
class ClockViewModelTest {
    private val observeState: ObserveState = mock()
    private val nextState: NextState = mock()
    private val errorChannel: BroadcastChannel<Throwable> = BroadcastChannel(Channel.CONFLATED)

    @get:Rule
    val coroutineRule = MainCoroutineRule()

    @get:Rule
    val rule: TestRule = InstantTaskExecutorRule()

    lateinit var viewModel: ClockViewModel

    @Before
    fun setUp() {
        viewModel =
            ClockViewModel(observeState, nextState, errorChannel, coroutineRule.testDispatcher)
    }

    @Test
    fun `Idle state`() = coroutineRule.testDispatcher.runBlockingTest {
        whenever(observeState.invoke()).thenReturn(flowOf(State.Idle))

        viewModel =
            ClockViewModel(observeState, nextState, errorChannel, coroutineRule.testDispatcher)

        viewModel.viewState.observeForTesting {
            assertThat(viewModel.viewState.value).isInstanceOf(ClockViewState.Idle::class.java)
        }
    }

    @Test
    fun `Pomodoro state`() = coroutineRule.testDispatcher.runBlockingTest {
        whenever(observeState.invoke()).thenReturn(flowOf(State.Pomodoro(0)))

        viewModel =
            ClockViewModel(observeState, nextState, errorChannel, coroutineRule.testDispatcher)

        viewModel.viewState.observeForTesting {
            assertThat(viewModel.viewState.value).isInstanceOf(ClockViewState.Pomodoro::class.java)
        }
    }

    @Test
    fun `Done state`() = coroutineRule.testDispatcher.runBlockingTest {
        whenever(observeState.invoke()).thenReturn(flowOf(State.Done))

        viewModel =
            ClockViewModel(observeState, nextState, errorChannel, coroutineRule.testDispatcher)

        viewModel.viewState.observeForTesting {
            assertThat(viewModel.viewState.value).isInstanceOf(ClockViewState.Done::class.java)
        }
    }

    @Test
    fun `Break state`() = coroutineRule.testDispatcher.runBlockingTest {
        whenever(observeState.invoke()).thenReturn(flowOf(State.Break(0)))

        viewModel =
            ClockViewModel(observeState, nextState, errorChannel, coroutineRule.testDispatcher)

        viewModel.viewState.observeForTesting {
            assertThat(viewModel.viewState.value).isInstanceOf(ClockViewState.Break::class.java)
        }
    }

    @Test
    fun `0 seconds clock`() = coroutineRule.testDispatcher.runBlockingTest {
        whenever(observeState.invoke()).thenReturn(flowOf(State.Pomodoro(0)))

        viewModel =
            ClockViewModel(observeState, nextState, errorChannel, coroutineRule.testDispatcher)

        viewModel.viewState.observeForTesting {
            assertThat(viewModel.viewState.value?.clock).isEqualTo("0:00")
        }
    }

    @Test
    fun `60 seconds clock`() = coroutineRule.testDispatcher.runBlockingTest {
        whenever(observeState.invoke()).thenReturn(flowOf(State.Pomodoro(60)))

        viewModel =
            ClockViewModel(observeState, nextState, errorChannel, coroutineRule.testDispatcher)

        viewModel.viewState.observeForTesting {
            assertThat(viewModel.viewState.value?.clock).isEqualTo("1:00")
        }
    }

    @Test
    fun `25 minutes clock`() = coroutineRule.testDispatcher.runBlockingTest {
        whenever(observeState.invoke()).thenReturn(flowOf(State.Pomodoro(POMODORO_LENGTH)))

        viewModel =
            ClockViewModel(observeState, nextState, errorChannel, coroutineRule.testDispatcher)

        viewModel.viewState.observeForTesting {
            assertThat(viewModel.viewState.value?.clock).isEqualTo("25:00")
        }
    }

    @Test
    fun `5 minutes clock`() = coroutineRule.testDispatcher.runBlockingTest {
        whenever(observeState.invoke()).thenReturn(flowOf(State.Break(BREAK_LENGTH)))

        viewModel =
            ClockViewModel(observeState, nextState, errorChannel, coroutineRule.testDispatcher)

        viewModel.viewState.observeForTesting {
            assertThat(viewModel.viewState.value?.clock).isEqualTo("5:00")
        }
    }

    @Test
    fun `Start pomodoro`() = coroutineRule.testDispatcher.runBlockingTest {
        whenever(observeState.invoke()).thenReturn(flowOf(State.Idle))

        viewModel.start()

        verify(nextState).invoke(Action.START)
    }

    @Test
    @InternalCoroutinesApi
    fun `Failure tu start a pomodoro`() = coroutineRule.testDispatcher.runBlockingTest {
        whenever(nextState.invoke(any())).thenAnswer { throw Throwable() }
        val testCollector = TestCollector<Throwable>()
        val job = testCollector.test(this, viewModel.errors)

        viewModel.start()

        assertThat(testCollector.values.last()).isInstanceOf(Throwable::class.java)
        job.cancel()
    }

    @Test
    fun `Stop pomodoro`() = coroutineRule.testDispatcher.runBlockingTest {
        whenever(observeState.invoke()).thenReturn(flowOf(State.Pomodoro(0)))

        viewModel.stop()

        verify(nextState).invoke(Action.STOP)
    }

    @Test
    fun `Take break`() = coroutineRule.testDispatcher.runBlockingTest {
        whenever(observeState.invoke()).thenReturn(flowOf(State.Done))

        viewModel.take()

        verify(nextState).invoke(Action.TAKE)
    }
}
