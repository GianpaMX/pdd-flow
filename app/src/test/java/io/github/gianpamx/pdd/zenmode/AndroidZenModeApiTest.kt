package io.github.gianpamx.pdd.zenmode

import android.app.NotificationManager
import android.app.NotificationManager.INTERRUPTION_FILTER_ALARMS
import android.app.NotificationManager.INTERRUPTION_FILTER_ALL
import android.app.NotificationManager.INTERRUPTION_FILTER_UNKNOWN
import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.eq
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import io.github.gianpamx.pdd.domain.api.ZenModeApi
import io.github.gianpamx.pdd.domain.entity.ZenMode
import org.assertj.core.api.Assertions.assertThat
import org.junit.Before
import org.junit.Test

class AndroidZenModeApiTest {
    private val notificationManager: NotificationManager = mock()

    private lateinit var androidZenModeApi: AndroidZenModeApi

    @Before
    fun setUp() {
        androidZenModeApi = AndroidZenModeApi(notificationManager)
    }

    @Test
    fun `Get Turn Off`() {
        doReturn(INTERRUPTION_FILTER_ALL).whenever(notificationManager).currentInterruptionFilter

        val mode = androidZenModeApi.mode

        assertThat(mode).isInstanceOf(ZenMode.Off::class.java)
    }

    @Test
    fun `Get Alarms Only`() {
        doReturn(INTERRUPTION_FILTER_ALARMS).whenever(notificationManager).currentInterruptionFilter

        val mode = androidZenModeApi.mode

        assertThat(mode).isInstanceOf(ZenMode.AlarmsOnly::class.java)
    }

    @Test
    fun `Get Other value`() {
        doReturn(INTERRUPTION_FILTER_UNKNOWN).whenever(notificationManager).currentInterruptionFilter

        val mode = androidZenModeApi.mode

        assertThat(mode).isEqualTo(ZenMode.Other(INTERRUPTION_FILTER_UNKNOWN))
    }

    @Test
    fun `Set Turn Off`() {
        androidZenModeApi.mode = ZenMode.Off

        verify(notificationManager).setInterruptionFilter(INTERRUPTION_FILTER_ALL)
    }

    @Test
    fun `Set Alarms Only`() {
        androidZenModeApi.mode = ZenMode.AlarmsOnly

        verify(notificationManager).setInterruptionFilter(INTERRUPTION_FILTER_ALARMS)
    }

    @Test
    fun `Set Other value`() {
        androidZenModeApi.mode = ZenMode.Other(INTERRUPTION_FILTER_UNKNOWN)

        verify(notificationManager).setInterruptionFilter(eq(INTERRUPTION_FILTER_UNKNOWN))
    }

    @Test(expected = ZenModeApi.AccessDeniedException::class)
    fun `Security Exception when do not have permissions`() {
        whenever(notificationManager.setInterruptionFilter(any())).thenThrow(SecurityException())

        androidZenModeApi.mode = ZenMode.AlarmsOnly

        // assert ZenModeApi.AccessDeniedException
    }
}
