package io.github.gianpamx.pdd.clock

import androidx.fragment.app.FragmentFactory
import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import io.github.gianpamx.pdd.MainActivity
import io.github.gianpamx.pdd.R
import io.github.gianpamx.pdd.app.ComponentApp
import io.github.gianpamx.pdd.app.TestComponent
import io.github.gianpamx.pdd.domain.api.MockTimeApi
import io.github.gianpamx.pdd.domain.entity.State
import io.github.gianpamx.pdd.dummyStateLog
import io.github.gianpamx.pdd.storage.AppDatabase
import io.github.gianpamx.pdd.storage.StateLogDao
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.lang.Thread.sleep
import javax.inject.Inject


@RunWith(AndroidJUnit4::class)
class ClockFragmentTest {
    @Inject
    lateinit var appDatabase: AppDatabase

    @Inject
    lateinit var stateLogDao: StateLogDao

    @Inject
    lateinit var timeApi: MockTimeApi

    @Inject
    lateinit var fragmentFactory: FragmentFactory

    @Before
    fun setUp() {
        val testComponent = InstrumentationRegistry.getInstrumentation().targetContext.run {
            deleteDatabase("pdd-flow")
            deleteSharedPreferences("internal")

            val testApp = applicationContext as ComponentApp
            testApp.component as TestComponent
        }

        testComponent.inject(this)

        timeApi.time = 0
    }

    @After
    fun tearDown() {
        appDatabase.close()
    }

    @Test
    fun startPomodoro() {
        stateLogDao.insertBlocking(dummyStateLog(state = State.IDLE.name))
        ActivityScenario.launch(MainActivity::class.java).use {

            sleep(1_000)
            onView(withId(R.id.startButton)).perform(click())

            sleep(1_000)
            onView(withId(R.id.stopButton)).check(matches(isDisplayed()))
        }
    }

    @Test
    fun stopPomodoro() {
        stateLogDao.insertBlocking(dummyStateLog(state = State.POMODORO.name))
        ActivityScenario.launch(MainActivity::class.java).use {

            sleep(1_000)
            onView(withId(R.id.stopButton)).perform(click())

            sleep(1_000)
            onView(withId(R.id.startButton)).check(matches(isDisplayed()))
        }
    }

    @Test
    fun takeBreak() {
        stateLogDao.insertBlocking(dummyStateLog(state = State.DONE.name))
        ActivityScenario.launch(MainActivity::class.java).use {

            sleep(1_000)
            onView(withId(R.id.takeButton)).perform(click())

            sleep(1_000)
            onView(withId(R.id.startButton)).check(matches(isDisplayed()))
        }
    }
}
