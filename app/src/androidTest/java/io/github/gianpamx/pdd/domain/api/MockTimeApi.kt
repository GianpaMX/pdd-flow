package io.github.gianpamx.pdd.domain.api

import io.github.gianpamx.NotMockedException
import kotlinx.coroutines.flow.flowOf

class MockTimeApi : TimeApi {
    var time: Int? = null

    override fun now(): Int = time ?: throw NotMockedException
    override fun ticker() = flowOf(0)
}
