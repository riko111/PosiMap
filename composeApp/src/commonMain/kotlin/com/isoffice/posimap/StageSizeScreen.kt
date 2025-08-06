package com.isoffice.posimap

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.isoffice.posimap.model.StageInfo

/** 舞台のサイズを入力する画面 */
@Composable
fun StageSizeScreen(onStart: (StageInfo) -> Unit) {
    // ユーザーが入力した演目名
    var titleInput by remember { mutableStateOf("") }
    // 舞台の幅(m)
    var widthInput by remember { mutableStateOf("") }
    // 舞台の奥行(m)
    var depthInput by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterVertically),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // 演目名入力欄
        OutlinedTextField(
            value = titleInput,
            onValueChange = { titleInput = it },
            label = { Text("演目名") },
            singleLine = true
        )
        // 舞台の幅入力欄
        OutlinedTextField(
            value = widthInput,
            onValueChange = { widthInput = it },
            label = { Text("舞台の幅 (m)") },
            singleLine = true
        )
        // 舞台の奥行入力欄
        OutlinedTextField(
            value = depthInput,
            onValueChange = { depthInput = it },
            label = { Text("舞台の奥行 (m)") },
            singleLine = true
        )
        Button(
            onClick = {
                // 入力値を数値に変換しStageInfoを作成してコールバック
                val width = widthInput.toFloatOrNull() ?: 0f
                val depth = depthInput.toFloatOrNull() ?: 0f
                onStart(StageInfo(titleInput, width, depth))
            }
        ) {
            Text("設定する")
        }
    }
}
