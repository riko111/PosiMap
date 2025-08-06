package com.isoffice.posimap.model

import androidx.compose.ui.graphics.Color

/** 舞台のタイトルや寸法（メートル）を保持するデータクラス */
data class StageInfo(
    val title: String,
    val width: Float,
    val depth: Float,
)

/** 舞台上に配置されたメンバー。位置は舞台左端からのxと前方からのyをメートルで表す */
data class Member(
    val id: String,
    val name: String,
    val displayChar: String,
    val color: Color,
    var x: Float,
    var y: Float,
)

/** 各シーンのフォーメーション状態を表す */
data class Scene(
    val id: String,
    var memo: String,
    val positions: MutableMap<String, Pair<Float, Float>>
)

