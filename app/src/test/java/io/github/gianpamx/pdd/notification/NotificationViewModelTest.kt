package io.github.gianpamx.pdd.notification

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import io.github.gianpamx.MainCoroutineRule
import io.github.gianpamx.observeForTesting
import io.github.gianpamx.pdd.domain.NextState
import io.github.gianpamx.pdd.domain.ObserveState
import io.github.gianpamx.pdd.domain.ObserveState.State
import io.github.gianpamx.pdd.domain.entity.Action
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.test.runBlockingTest
import org.assertj.core.api.Assertions.assertThat
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestRule

private const val POMODORO_LENGTH = 25 * 60
private const val BREAK_LENGTH = 5 * 60

class NotificationViewModelTest {
    private val observeState: ObserveState = mock()

    private val nextState: NextState = mock()

    @get:Rule
    val coroutineRule = MainCoroutineRule()

    @get:Rule
    val rule: TestRule = InstantTaskExecutorRule()

    lateinit var viewModel: NotificationViewModel

    @Test
    fun `Pomodoro state`() = coroutineRule.testDispatcher.runBlockingTest {
        whenever(observeState.invoke()).thenReturn(flowOf(State.Pomodoro(0)))

        viewModel = NotificationViewModel(observeState, nextState, coroutineRule.testDispatcher)

        viewModel.notificationState.observeForTesting {
            assertThat(viewModel.notificationState.value).isInstanceOf(NotificationState.Pomodoro::class.java)
        }
    }

    @Test
    fun `Break state`() = coroutineRule.testDispatcher.runBlockingTest {
        whenever(observeState.invoke()).thenReturn(flowOf(State.Break(0)))

        viewModel = NotificationViewModel(observeState, nextState, coroutineRule.testDispatcher)

        viewModel.notificationState.observeForTesting {
            assertThat(viewModel.notificationState.value).isInstanceOf(NotificationState.Break::class.java)
        }
    }

    @Test
    fun `Complete pomodoro`() = coroutineRule.testDispatcher.runBlockingTest {
        whenever(observeState.invoke())
            .thenReturn((POMODORO_LENGTH downTo 0).asFlow().map { State.Pomodoro(it) })
        viewModel = NotificationViewModel(observeState, nextState, coroutineRule.testDispatcher)

        viewModel.notificationState.observeForTesting {
            assertThat(viewModel.notificationState.value).isInstanceOf(NotificationState.Pomodoro::class.java)
        }

        verify(nextState).invoke(Action.COMPLETE)
    }

    @Test
    fun `Complete break`() = coroutineRule.testDispatcher.runBlockingTest {
        whenever(observeState.invoke())
            .thenReturn((BREAK_LENGTH downTo 0).asFlow().map { State.Break(it) })
        viewModel = NotificationViewModel(observeState, nextState, coroutineRule.testDispatcher)

        viewModel.notificationState.observeForTesting {
            assertThat(viewModel.notificationState.value).isInstanceOf(NotificationState.Break::class.java)
        }

        verify(nextState).invoke(Action.COMPLETE)
    }
}
