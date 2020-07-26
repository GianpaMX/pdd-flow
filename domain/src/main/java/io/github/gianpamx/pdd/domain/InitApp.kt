package io.github.gianpamx.pdd.domain

import io.github.gianpamx.pdd.domain.api.TransitionApi
import io.github.gianpamx.pdd.domain.api.TimeApi
import io.github.gianpamx.pdd.domain.entity.State

class InitApp(
    private val transitionApi: TransitionApi,
    private val timeApi: TimeApi
) {
    suspend operator fun invoke(): State? = when {
        transitionApi.getLastTransition() == null -> State.IDLE.also {
            transitionApi.newTransition(timeApi.now(), it)
        }
        else -> null
    }
}
