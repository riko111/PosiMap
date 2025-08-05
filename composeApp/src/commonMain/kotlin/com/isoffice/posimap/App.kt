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
        var stageInfo by remember { mutableStateOf<StageInfo?>(null) }
        if (stageInfo == null) {
            StageSizeScreen { title, width, depth ->
                stageInfo = StageInfo(title, width, depth)
            }
        } else {
            StageSizeResult(stageInfo!!)
        }
    }
}

@Composable
private fun StageSizeResult(stage: StageInfo) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .safeContentPadding(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("『${stage.title}』の舞台サイズ：${stage.width}m × ${stage.depth}m")
    }
}

private data class StageInfo(
    val title: String,
    val width: Float,
    val depth: Float,
)
