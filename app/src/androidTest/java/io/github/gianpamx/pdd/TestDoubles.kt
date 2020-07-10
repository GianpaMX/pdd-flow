package io.github.gianpamx.pdd

import io.github.gianpamx.pdd.room.entity.StateLog

fun dummyStateLog(
    id: Long = 0,
    timestamp: Int = 0,
    state: String = ""
) = StateLog(
    id = id,
    timestamp = timestamp,
    state = state
)
