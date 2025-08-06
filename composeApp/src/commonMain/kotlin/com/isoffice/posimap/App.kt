package com.isoffice.posimap

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import org.jetbrains.compose.ui.tooling.preview.Preview
import com.isoffice.posimap.model.StageInfo

/** アプリ全体のエントリーポイント */
@Composable
@Preview
fun App() {
    // Material3のテーマを適用したルートコンポーネント
    MaterialTheme {
        // 舞台情報を保持するステート。nullの場合は未設定
        var stageInfo by remember { mutableStateOf<StageInfo?>(null) }
        if (stageInfo == null) {
            // 舞台サイズ入力画面を表示し、確定したら情報をステートに保存
            StageSizeScreen { info -> stageInfo = info }
        } else {
            // 舞台情報がある場合はフォーメーション編集画面を表示
            FormationScreen(stageInfo!!)
        }
    }
}
