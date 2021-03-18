package io.github.gianpamx.pdd.clock.composable

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.github.gianpamx.pdd.clock.ClockViewState

@Composable
fun BarClock(state: ClockViewState, totalTime: Int, modifier: Modifier = Modifier) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .height(48.dp)
            .wrapContentWidth()
    ) {
        val numberOfBars = totalTime / 60
        (0..numberOfBars.dec()).forEach {
            val currentStep = state.seconds % numberOfBars
            val height by animateFloatAsState(targetValue = if (it == currentStep) 1f else if (it % 5 == 0) 0.8f else .49f)
            val alpha by animateFloatAsState(targetValue = if (it <= state.seconds / 60) 1f else 0.5f)
            Surface(
                modifier = Modifier
                    .fillMaxHeight(height)
                    .width(4.dp),
                shape = RoundedCornerShape(5.dp),
                color = MaterialTheme.colors.secondary.copy(alpha = alpha)
            ) {}
            Spacer(Modifier.size(8.dp))
        }
    }
}
