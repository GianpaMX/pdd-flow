package io.github.gianpamx.pdd.zenmode

import android.app.NotificationManager
import android.app.NotificationManager.INTERRUPTION_FILTER_ALARMS
import android.app.NotificationManager.INTERRUPTION_FILTER_ALL
import io.github.gianpamx.pdd.domain.api.ZenModeApi
import io.github.gianpamx.pdd.domain.entity.ZenMode

class AndroidZenModeApi(
    private val notificationManager: NotificationManager
) : ZenModeApi {
    override var mode: ZenMode
        get() = when (val value = notificationManager.currentInterruptionFilter) {
            INTERRUPTION_FILTER_ALL -> ZenMode.Off
            INTERRUPTION_FILTER_ALARMS -> ZenMode.AlarmsOnly
            else -> ZenMode.Other(value)
        }
        set(zenMode) = try {
            notificationManager.setInterruptionFilter(
                when (zenMode) {
                    is ZenMode.Off -> INTERRUPTION_FILTER_ALL
                    is ZenMode.AlarmsOnly -> INTERRUPTION_FILTER_ALARMS
                    is ZenMode.Other -> zenMode.value
                }
            )
        } catch (e: SecurityException) {
            throw ZenModeApi.AccessDeniedException(e)
        }
}
