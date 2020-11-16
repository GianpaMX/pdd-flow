package io.github.gianpamx.pdd.clock

import io.github.gianpamx.pdd.toClock

sealed class ClockViewState(open val seconds: Int) {
    data class Idle(override val seconds: Int) : ClockViewState(seconds)
    data class Pomodoro(override val seconds: Int) : ClockViewState(seconds)
    data class Done(override val seconds: Int) : ClockViewState(seconds)
    data class Break(override val seconds: Int) : ClockViewState(seconds)

    fun clock() = seconds.toClock()
}
