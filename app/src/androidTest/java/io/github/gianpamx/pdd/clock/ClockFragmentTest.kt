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
import io.github.gianpamx.pdd.domain.api.MockTimeApi
import io.github.gianpamx.pdd.domain.entity.State
import io.github.gianpamx.pdd.dummyStateLog
import io.github.gianpamx.pdd.room.MockTransitionDao
import kotlinx.coroutines.flow.flowOf
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import javax.inject.Inject

@RunWith(AndroidJUnit4::class)
class ClockFragmentTest {
    @Inject
    lateinit var transitionDao: MockTransitionDao

    @Inject
    lateinit var timeApi: MockTimeApi

    @Inject
    lateinit var fragmentFactory: FragmentFactory

    @Before
    fun setUp() {
        val testApp = InstrumentationRegistry.getInstrumentation()
            .targetContext
            .applicationContext as ComponentApp

        val testComponent = testApp.component as TestComponent

        testComponent.inject(this)

        timeApi.time = 0
        transitionDao.onLastStateLog = { null }
    }

    @Test
    fun idleState() {
        transitionDao.onObserveStateLog = { flowOf(dummyStateLog(state = State.IDLE.name)) }

        launchFragmentInContainer<ClockFragment>(factory = fragmentFactory)

        onView(withId(R.id.startButton)).check(matches(isDisplayed()))
    }

    @Test
    fun pomodoroState() {
        transitionDao.onObserveStateLog = { flowOf(dummyStateLog(state = State.POMODORO.name)) }

        launchFragmentInContainer<ClockFragment>(factory = fragmentFactory)

        onView(withId(R.id.stopButton)).check(matches(isDisplayed()))
    }

    @Test
    fun doneState() {
        transitionDao.onObserveStateLog = { flowOf(dummyStateLog(state = State.DONE.name)) }

        launchFragmentInContainer<ClockFragment>(factory = fragmentFactory)

        onView(withId(R.id.takeButton)).check(matches(isDisplayed()))
    }

    @Test
    fun breakState() {
        transitionDao.onObserveStateLog = { flowOf(dummyStateLog(state = State.BREAK.name)) }

        launchFragmentInContainer<ClockFragment>(factory = fragmentFactory)

        onView(withId(R.id.startButton)).check(matches(isDisplayed()))
    }
}
