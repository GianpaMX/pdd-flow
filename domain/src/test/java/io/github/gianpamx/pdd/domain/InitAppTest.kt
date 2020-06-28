package io.github.gianpamx.pdd.domain

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import io.github.gianpamx.pdd.domain.api.PersistenceApi
import io.github.gianpamx.pdd.domain.api.TimeApi
import io.github.gianpamx.pdd.domain.entity.State
import io.github.gianpamx.pdd.domain.entity.Transition
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Before
import org.junit.Test
import org.mockito.internal.verification.Times

@ExperimentalCoroutinesApi
class InitAppTest {
    private val nextState: NextState = mock()
    private val persistenceApi: PersistenceApi = mock()
    private val timeApi: TimeApi = mock()

    lateinit var initApp: InitApp

    @Before
    fun setUp() {
        initApp = InitApp(nextState, persistenceApi, timeApi)
    }


    @Test
    fun `Log already contains an entry`() = runBlockingTest {
        whenever(persistenceApi.getLastStateLog()).thenReturn(Transition(State.IDLE, 0))

        initApp.invoke()

        verify(nextState, Times(0)).invoke(any(), any())
        verify(persistenceApi, Times(0)).newStateLog(any(), any())
    }

    @Test
    fun `Insert first entry`() = runBlockingTest {
        whenever(persistenceApi.getLastStateLog()).thenReturn(null)
        whenever(nextState.invoke(null, null)).thenReturn(State.IDLE)
        whenever(timeApi.now()).thenReturn(0)

        initApp.invoke()

        verify(persistenceApi).newStateLog(any(), any())
    }
}
