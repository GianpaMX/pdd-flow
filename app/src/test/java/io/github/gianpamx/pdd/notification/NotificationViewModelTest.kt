package io.github.gianpamx.pdd.notification

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.whenever
import io.github.gianpamx.MainCoroutineRule
import io.github.gianpamx.observeForTesting
import io.github.gianpamx.pdd.domain.ObserveState
import io.github.gianpamx.pdd.domain.ObserveState.State
import kotlinx.coroutines.channels.ConflatedBroadcastChannel
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runBlockingTest
import org.assertj.core.api.Assertions.assertThat
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestRule

class NotificationViewModelTest {
    private val errorChannel = ConflatedBroadcastChannel<Throwable>()
    private val observeState: ObserveState = mock()

    @get:Rule
    val coroutineRule = MainCoroutineRule()

    @get:Rule
    val rule: TestRule = InstantTaskExecutorRule()

    lateinit var viewModel: NotificationViewModel

    @Test
    fun `Pomodoro state`() = coroutineRule.testDispatcher.runBlockingTest {
        whenever(observeState.invoke()).thenReturn(flowOf(State.Pomodoro(0)))

        viewModel = NotificationViewModel(observeState, errorChannel, coroutineRule.testDispatcher)

        viewModel.notificationState.observeForTesting {
            assertThat(viewModel.notificationState.value).isInstanceOf(NotificationState.Pomodoro::class.java)
        }
    }

    @Test
    fun `Break state`() = coroutineRule.testDispatcher.runBlockingTest {
        whenever(observeState.invoke()).thenReturn(flowOf(State.Break(0)))

        viewModel = NotificationViewModel(observeState, errorChannel, coroutineRule.testDispatcher)

        viewModel.notificationState.observeForTesting {
            assertThat(viewModel.notificationState.value).isInstanceOf(NotificationState.Break::class.java)
        }
    }
}
