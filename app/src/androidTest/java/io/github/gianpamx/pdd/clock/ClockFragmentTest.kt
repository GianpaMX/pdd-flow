package io.github.gianpamx.pdd.clock

import androidx.fragment.app.FragmentFactory
import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import io.github.gianpamx.pdd.R
import io.github.gianpamx.pdd.app.ComponentApp
import io.github.gianpamx.pdd.app.TestComponent
import io.github.gianpamx.pdd.domain.ObserveState.State
import io.github.gianpamx.pdd.domain.ObserveStateMock
import kotlinx.coroutines.flow.flowOf
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import javax.inject.Inject

@RunWith(AndroidJUnit4::class)
class ClockFragmentTest {
    @Inject
    lateinit var observeStateMock: ObserveStateMock

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
        observeStateMock.onInvoke = flowOf(State.Idle)

        launchFragmentInContainer<ClockFragment>(factory = fragmentFactory)

        onView(withId(R.id.startButton)).check(matches(isDisplayed()))
    }

    @Test
    fun pomodoroState() {
        observeStateMock.onInvoke = flowOf(State.Pomodoro(25))

        launchFragmentInContainer<ClockFragment>(factory = fragmentFactory)

        onView(withId(R.id.stopButton)).check(matches(isDisplayed()))
    }

    @Test
    fun doneState() {
        observeStateMock.onInvoke = flowOf(State.Done)

        launchFragmentInContainer<ClockFragment>(factory = fragmentFactory)

        onView(withId(R.id.takeButton)).check(matches(isDisplayed()))
    }

    @Test
    fun breakState() {
        observeStateMock.onInvoke = flowOf(State.Break(5))

        launchFragmentInContainer<ClockFragment>(factory = fragmentFactory)

        onView(withId(R.id.startButton)).check(matches(isDisplayed()))
    }
}
