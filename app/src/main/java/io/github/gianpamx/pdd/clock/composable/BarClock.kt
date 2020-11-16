package io.github.gianpamx.pdd.clock.composable

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun BarClock(time: Int, modifier: Modifier = Modifier) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .height(48.dp)
            .wrapContentWidth()
    ) {
        (0..time.dec()).forEach {
            Surface(
                modifier = Modifier
                    .fillMaxHeight(if (it % 5 == 0) 1f else .59f)
                    .width(4.dp),
                shape = RoundedCornerShape(5.dp),
                color = MaterialTheme.colors.secondary
            ) {

            }
            Spacer(Modifier.size(8.dp))
        }
    }
}

@Preview
@Composable
private fun PreviewBarClock() {
    Column {
        BarClock(time = 25)
    }
}
