package io.github.gianpamx.pdd.notification

sealed class NotificationState(open val clock: String) {
    data class Pomodoro(override val clock: String) : NotificationState(clock)
    data class Break(override val clock: String) : NotificationState(clock)
}
