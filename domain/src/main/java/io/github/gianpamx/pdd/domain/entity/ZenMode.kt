package io.github.gianpamx.pdd.domain.entity

sealed class ZenMode {
    object Off : ZenMode()
    object AlarmsOnly : ZenMode()
    data class Other(val value: Int) : ZenMode()
}
