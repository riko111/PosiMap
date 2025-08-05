package com.isoffice.posimap

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import org.jetbrains.compose.ui.tooling.preview.Preview
import com.isoffice.posimap.model.StageInfo

@Composable
@Preview
fun App() {
    MaterialTheme {
        var stageInfo by remember { mutableStateOf<StageInfo?>(null) }
        if (stageInfo == null) {
            StageSizeScreen { info -> stageInfo = info }
        } else {
            FormationScreen(stageInfo!!)
        }
    }
}

