package io.github.gianpamx.pdd.domain

import app.cash.turbine.test
import com.nhaarman.mockitokotlin2.doThrow
import com.nhaarman.mockitokotlin2.eq
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.spy
import com.nhaarman.mockitokotlin2.times
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import io.github.gianpamx.pdd.domain.api.StorageApi
import io.github.gianpamx.pdd.domain.api.TimeApi
import io.github.gianpamx.pdd.domain.api.TransitionApi
import io.github.gianpamx.pdd.domain.api.ZenModeApi
import io.github.gianpamx.pdd.domain.entity.Action
import io.github.gianpamx.pdd.domain.entity.State
import io.github.gianpamx.pdd.domain.entity.Transition
import io.github.gianpamx.pdd.domain.entity.ZenMode
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.ConflatedBroadcastChannel
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.runBlockingTest
import org.assertj.core.api.Assertions.assertThat
import org.junit.Before
import org.junit.Test
import kotlin.time.ExperimentalTime

private const val POMODORO_LENGTH = 25 * 60
private const val BREAK_LENGTH = 5 * 60

@ExperimentalTime
@ExperimentalCoroutinesApi
class ObserveStateTest {
    private val nextState: NextState = mock()
    private val transitionApi: TransitionApi = mock()
    private val timeApi: TimeApi = mock()
    private val zenModeApi = spy(object : ZenModeApi {
        override var mode: ZenMode = ZenMode.Off
    })
    private val storageApi: StorageApi = object : StorageApi {
        override var originalMode: ZenMode? = null
    }

    private val errorChannel: ConflatedBroadcastChannel<Throwable> = ConflatedBroadcastChannel()

    lateinit var observeState: ObserveState

    @Before
    fun setUp() {
        whenever(timeApi.ticker()).thenReturn(flowOf(0))
        observeState =
            ObserveState(nextState, transitionApi, timeApi, zenModeApi, storageApi, errorChannel)
    }

    @Test
    fun `Idle State`() = runBlockingTest {
        whenever(transitionApi.observeTransitionLog()).thenReturn(flowOf(Transition(State.IDLE, 0)))

        val observeStateFlow = observeState()

        observeStateFlow.test {
            assertThat(expectItem()).isEqualTo(ObserveState.State.Idle)
            expectComplete()
        }
    }

    @Test
    fun `Pomodoro State`() = runBlockingTest {
        whenever(transitionApi.observeTransitionLog())
            .thenReturn(flowOf(Transition(State.POMODORO, 0)))

        val observeStateFlow = observeState()

        observeStateFlow.test {
            assertThat(expectItem()).isEqualTo(ObserveState.State.Pomodoro(POMODORO_LENGTH))
            expectComplete()
        }
    }

    @Test
    fun `Done State`() = runBlockingTest {
        whenever(transitionApi.observeTransitionLog()).thenReturn(flowOf(Transition(State.DONE, 0)))

        val observeStateFlow = observeState()

        observeStateFlow.test {
            assertThat(expectItem()).isEqualTo(ObserveState.State.Done)
            expectComplete()
        }
    }

    @Test
    fun `Break State`() = runBlockingTest {
        whenever(transitionApi.observeTransitionLog())
            .thenReturn(flowOf(Transition(State.BREAK, 0)))

        val observeStateFlow = observeState()

        observeStateFlow.test {
            assertThat(expectItem()).isEqualTo(ObserveState.State.Break(BREAK_LENGTH))
            expectComplete()
        }
    }

    @Test
    fun `Complete break`() = runBlockingTest {
        whenever(timeApi.ticker()).thenReturn(flowOf(BREAK_LENGTH))
        whenever(transitionApi.observeTransitionLog())
            .thenReturn(flowOf(Transition(State.BREAK, 0)))
        observeState =
            ObserveState(nextState, transitionApi, timeApi, zenModeApi, storageApi, errorChannel)

        observeState().test { cancelAndIgnoreRemainingEvents() }

        verify(nextState).invoke(Action.COMPLETE)
    }

    @Test
    fun `Complete pomodoro`() = runBlockingTest {
        whenever(timeApi.ticker()).thenReturn(flowOf(POMODORO_LENGTH))
        whenever(transitionApi.observeTransitionLog())
            .thenReturn(flowOf(Transition(State.POMODORO, 0)))
        observeState =
            ObserveState(nextState, transitionApi, timeApi, zenModeApi, storageApi, errorChannel)

        observeState().test { cancelAndIgnoreRemainingEvents() }

        verify(nextState).invoke(Action.COMPLETE)
    }

    @Test
    fun `Turn on DND when Pomodoro starts`() = runBlockingTest {
        zenModeApi.mode = ZenMode.Off
        whenever(transitionApi.observeTransitionLog()).thenReturn(
            flowOf(
                Transition(State.IDLE, 0),
                Transition(State.POMODORO, 0),
                Transition(State.DONE, 0)
            )
        )

        observeState().test { cancelAndIgnoreRemainingEvents() }

        verify(zenModeApi).mode = ZenMode.AlarmsOnly
        assertThat(zenModeApi.mode).isEqualTo(ZenMode.Off)
    }

    @Test
    fun `Start a pomodoro with DND already set`() = runBlockingTest {
        fun arrangeTestOnly() = times(1)
        zenModeApi.mode = ZenMode.AlarmsOnly
        whenever(transitionApi.observeTransitionLog()).thenReturn(
            flowOf(
                Transition(State.IDLE, 0),
                Transition(State.POMODORO, 0),
                Transition(State.DONE, 0)
            )
        )

        observeState().test { cancelAndIgnoreRemainingEvents() }

        verify(zenModeApi, arrangeTestOnly()).mode = ZenMode.AlarmsOnly
    }

    @Test
    fun `Error turning on DND`() = runBlockingTest {
        whenever(transitionApi.observeTransitionLog())
            .thenReturn(flowOf(Transition(State.POMODORO, 0)))
        doThrow(ZenModeApi.AccessDeniedException(SecurityException()))
            .whenever(zenModeApi).mode = eq(ZenMode.AlarmsOnly)

        observeState().test { cancelAndIgnoreRemainingEvents() }

        assertThat(errorChannel.value).isInstanceOf(ZenModeApi.AccessDeniedException::class.java)
    }
}
