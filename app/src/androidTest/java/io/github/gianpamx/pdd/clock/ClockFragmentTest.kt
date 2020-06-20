package io.github.gianpamx.pdd.clock

import androidx.fragment.app.FragmentFactory
import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.nhaarman.mockitokotlin2.whenever
import io.github.gianpamx.pdd.R
import io.github.gianpamx.pdd.app.ComponentApp
import io.github.gianpamx.pdd.app.TestComponent
import io.github.gianpamx.pdd.domain.ObserveState
import io.github.gianpamx.pdd.domain.ObserveState.State
import kotlinx.coroutines.flow.flowOf
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import javax.inject.Inject

@RunWith(AndroidJUnit4::class)
class ClockFragmentTest {
    @Inject
    lateinit var observeStateMock: ObserveState

    @Inject
    lateinit var fragmentFactory: FragmentFactory

    @Before
    fun setUp() {
        val testApp = InstrumentationRegistry.getInstrumentation()
            .targetContext
            .applicationContext as ComponentApp

        val testComponent = testApp.component as TestComponent

        testComponent.inject(this)
    }

    @Test
    fun idleState() {
        whenever(observeStateMock.invoke()).thenReturn(flowOf(State.Idle))

        launchFragmentInContainer<ClockFragment>(factory = fragmentFactory)

        onView(withId(R.id.startButton)).check(matches(isDisplayed()))
    }

    @Test
    fun pomodoroState() {
        whenever(observeStateMock.invoke()).thenReturn(flowOf(State.Pomodoro(25)))

        launchFragmentInContainer<ClockFragment>(factory = fragmentFactory)

        onView(withId(R.id.stopButton)).check(matches(isDisplayed()))
    }

    @Test
    fun doneState() {
        whenever(observeStateMock.invoke()).thenReturn(flowOf(State.Done))

        launchFragmentInContainer<ClockFragment>(factory = fragmentFactory)

        onView(withId(R.id.takeButton)).check(matches(isDisplayed()))
    }

    @Test
    fun breakState() {
        whenever(observeStateMock.invoke()).thenReturn(flowOf(State.Break(5)))

        launchFragmentInContainer<ClockFragment>(factory = fragmentFactory)

        onView(withId(R.id.startButton)).check(matches(isDisplayed()))
    }
}
