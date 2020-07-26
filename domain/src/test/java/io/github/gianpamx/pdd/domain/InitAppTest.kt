package io.github.gianpamx.pdd.domain

import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.whenever
import io.github.gianpamx.pdd.domain.api.TransitionApi
import io.github.gianpamx.pdd.domain.api.TimeApi
import io.github.gianpamx.pdd.domain.entity.State
import io.github.gianpamx.pdd.domain.entity.Transition
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.assertj.core.api.Assertions.assertThat
import org.junit.Before
import org.junit.Test

@ExperimentalCoroutinesApi
class InitAppTest {
    private val transitionApi: TransitionApi = mock()
    private val timeApi: TimeApi = mock()

    lateinit var initApp: InitApp

    @Before
    fun setUp() {
        initApp = InitApp(transitionApi, timeApi)
    }


    @Test
    fun `Log already contains an entry`() = runBlockingTest {
        whenever(transitionApi.getLastTransition()).thenReturn(Transition(State.IDLE, 0))

        val result = initApp.invoke()

        assertThat(result).isNull()
    }

    @Test
    fun `Insert first entry`() = runBlockingTest {
        whenever(transitionApi.getLastTransition()).thenReturn(null)
        whenever(timeApi.now()).thenReturn(0)

        val result = initApp.invoke()

        assertThat(result).isEqualTo(State.IDLE)
    }
}
