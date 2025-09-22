package com.isoffice.posimap.gateway

import com.isoffice.posimap.model.Performance

interface ShareGateway {
    fun sharePerformance(performance: Performance)
}

object NoOpShareGateway : ShareGateway {
    override fun sharePerformance(performance: Performance) = Unit
}
