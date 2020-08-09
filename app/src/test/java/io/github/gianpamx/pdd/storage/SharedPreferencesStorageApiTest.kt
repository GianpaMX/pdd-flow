package io.github.gianpamx.pdd.storage

import android.content.SharedPreferences
import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.anyOrNull
import com.nhaarman.mockitokotlin2.eq
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import io.github.gianpamx.pdd.domain.entity.ZenMode
import org.assertj.core.api.Assertions.assertThat
import org.junit.Before
import org.junit.Test

class SharedPreferencesStorageApiTest {
    private val sharedPreferences: SharedPreferences = mock()
    private val editor: SharedPreferences.Editor = mock()

    private lateinit var storageApi: SharedPreferencesStorageApi

    @Before
    fun setUp() {
        whenever(sharedPreferences.edit()).thenReturn(editor)

        storageApi = SharedPreferencesStorageApi(sharedPreferences)
    }

    @Test
    fun `Mode not saved`() {
        val mode = storageApi.originalMode

        assertThat(mode).isNull()
    }

    @Test
    fun `Off mode saved`() {
        whenever(sharedPreferences.getString(any(), anyOrNull()))
            .thenReturn(ZenMode.Off::class.qualifiedName)

        val mode = storageApi.originalMode

        assertThat(mode).isEqualTo(ZenMode.Off)
    }

    @Test
    fun `AlarmsOnly mode saved`() {
        whenever(sharedPreferences.getString(any(), anyOrNull()))
            .thenReturn(ZenMode.AlarmsOnly::class.qualifiedName)

        val mode = storageApi.originalMode

        assertThat(mode).isEqualTo(ZenMode.AlarmsOnly)
    }

    @Test
    fun `Other mode saved`() {
        val value = 0
        whenever(sharedPreferences.getString(any(), anyOrNull()))
            .thenReturn(value.toString())

        val mode = storageApi.originalMode

        assertThat(mode).isEqualTo(ZenMode.Other(value))
    }

    @Test
    fun `Invalid mode saved`() {
        whenever(sharedPreferences.getString(any(), anyOrNull())).thenReturn("invalid")

        val mode = storageApi.originalMode

        assertThat(mode).isNull()
    }

    @Test
    fun `Save other mode`() {
        val value = 0

        storageApi.originalMode = ZenMode.Other(value)

        verify(editor).putString(any(), eq(value.toString()))
    }

    @Test
    fun `Save object mode`() {
        storageApi.originalMode = ZenMode.AlarmsOnly

        verify(editor).putString(any(), eq(ZenMode.AlarmsOnly::class.qualifiedName))
    }
}
