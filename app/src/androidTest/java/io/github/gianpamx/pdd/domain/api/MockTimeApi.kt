package io.github.gianpamx.pdd.domain.api

import io.github.gianpamx.NotMockedException

class MockTimeApi : TimeApi {
    var time: Int? = null

    override fun now(): Int = time ?: throw NotMockedException
}
