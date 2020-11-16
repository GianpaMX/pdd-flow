package io.github.gianpamx.pdd.clock.composable

import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp

@Composable
fun Clock(time: String) {
    Text(time, fontSize = 100.sp, fontWeight = FontWeight.Bold)
}

@Preview
@Composable
private fun previewClock() = Clock(time = "25:00")
