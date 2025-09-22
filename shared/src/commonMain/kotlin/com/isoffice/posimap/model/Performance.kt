package com.isoffice.posimap.model

import kotlinx.serialization.Serializable

@Serializable
data class Stage(val width: Int, val height: Int)

@Serializable
data class Member(
    val id: String,
    val name: String,
    val label: String,
    val color: String
)

@Serializable
data class Position(val memberId: String, val x: Int, val y: Int)

@Serializable
data class FormationScene(
    val id: String,
    val title: String,
    val positions: List<Position>,
    val version: Int = 1
)

@Serializable
data class Performance(
    val schemaVersion: Int = 1,
    val title: String,
    val stage: Stage,
    val members: List<Member>,
    val scenes: List<FormationScene>,
    val revision: Int = 1
)
