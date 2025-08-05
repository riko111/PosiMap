package com.isoffice.posimap

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
@Preview
fun App() {
    MaterialTheme {
        var stageSize by remember { mutableStateOf<Pair<Float, Float>?>(null) }
        if (stageSize == null) {
            StageSizeScreen { width, depth ->
                stageSize = width to depth
            }
        } else {
            StageSizeResult(stageSize!!)
        }
    }
}

@Composable
private fun StageSizeResult(stage: Pair<Float, Float>) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .safeContentPadding(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("舞台サイズ：${stage.first}m × ${stage.second}m")
    }
}
