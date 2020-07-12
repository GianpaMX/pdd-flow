package io.github.gianpamx.pdd.domain.api

import kotlinx.coroutines.flow.Flow

interface TimeApi {
    fun now(): Int
    fun ticker(): Flow<Int>
}
