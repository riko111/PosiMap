package com.isoffice.posimap.repository

import com.isoffice.posimap.gateway.ShareGateway
import com.isoffice.posimap.model.Performance

import kotlinx.serialization.json.Json

class PerformanceRepository(
    private val shareGateway: ShareGateway? = null,
) {
    fun export(performance: Performance): ByteArray =
        Json.encodeToString(performance).encodeToByteArray()

    fun import(bytes: ByteArray): Performance =
        Json.decodeFromString(bytes.decodeToString())
}