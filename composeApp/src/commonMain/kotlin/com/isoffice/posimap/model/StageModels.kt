package com.isoffice.posimap.model

import androidx.compose.ui.graphics.Color

/** Stage information such as title and dimensions in meters. */
data class StageInfo(
    val title: String,
    val width: Float,
    val depth: Float,
)

/** Member placed on stage. Position is measured in meters from the stage left (x) and front (y). */
data class Member(
    val id: String,
    val name: String,
    val displayChar: String,
    val color: Color,
    var x: Float,
    var y: Float,
)

/** A formation state for a given scene. */
data class Scene(
    val id: String,
    var memo: String,
    val positions: MutableMap<String, Pair<Float, Float>>
)

