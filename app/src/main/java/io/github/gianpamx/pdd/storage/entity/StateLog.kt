package io.github.gianpamx.pdd.storage.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class StateLog(
    @PrimaryKey(autoGenerate = true) val id: Long,
    val timestamp: Int,
    val state: String
)
