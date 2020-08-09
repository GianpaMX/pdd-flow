package io.github.gianpamx.pdd.storage

import android.content.SharedPreferences
import androidx.core.content.edit
import io.github.gianpamx.pdd.domain.api.StorageApi
import io.github.gianpamx.pdd.domain.entity.ZenMode

private const val ORIGINAL_MODE_KEY = "originalMode"

class SharedPreferencesStorageApi(private val sharedPreferences: SharedPreferences) : StorageApi {
    override var originalMode: ZenMode?
        get() {
            return sharedPreferences.getString(ORIGINAL_MODE_KEY, null)?.toZenMode()
        }
        set(value) {
            sharedPreferences.edit {
                putString(ORIGINAL_MODE_KEY, value?.convertToString())
            }
        }

    private fun ZenMode.convertToString() = if (this is ZenMode.Other)
        value.toString()
    else
        this::class.qualifiedName.orEmpty()

    private fun String.toZenMode() = when (this) {
        ZenMode.Off::class.qualifiedName -> ZenMode.Off
        ZenMode.AlarmsOnly::class.qualifiedName -> ZenMode.AlarmsOnly
        else -> try {
            ZenMode.Other(this.toInt())
        } catch (e: Exception) {
            null
        }
    }
}
