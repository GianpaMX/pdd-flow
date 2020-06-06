package io.github.gianpamx.pdd.clock

sealed class ClockViewState(open val clock: String) {
    data class Idle(override val clock: String) : ClockViewState(clock)
    data class Pomodoro(override val clock: String) : ClockViewState(clock)
    data class Done(override val clock: String) : ClockViewState(clock)
    data class Break(override val clock: String) : ClockViewState(clock)
}
