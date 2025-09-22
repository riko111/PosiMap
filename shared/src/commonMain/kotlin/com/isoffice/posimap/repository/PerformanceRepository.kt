package com.isoffice.posimap.repository

import com.isoffice.posimap.gateway.NoOpShareGateway
import com.isoffice.posimap.gateway.ShareGateway
import com.isoffice.posimap.model.Performance

class PerformanceRepository(
    private val shareGateway: ShareGateway = NoOpShareGateway
) {
    fun getPerformances(): List<Performance> = listOf(
        Performance(
            id = "1",
            title = "First Positive Step",
            description = "Celebrated the launch of our multiplatform prototype.",
            positivity = 92
        ),
        Performance(
            id = "2",
            title = "Team Collaboration",
            description = "Ran a cross-platform workshop to align the mobile teams.",
            positivity = 87
        ),
        Performance(
            id = "3",
            title = "Customer Feedback",
            description = "Gathered uplifting testimonials from early adopters.",
            positivity = 94
        )
    )

    fun sharePerformance(performance: Performance) {
        shareGateway.sharePerformance(performance)
    }
}
