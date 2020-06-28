package io.github.gianpamx.pdd.domain

import io.github.gianpamx.pdd.domain.entity.State
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test

class NextStateTest {
    @Test
    fun `Initial State`() {
        val nextState = NextState()

        val result = nextState.invoke(null, null)

        assertThat(result).isEqualTo(State.IDLE)
    }
}
