package com.isoffice.posimap.model

data class Performance(
    val id: String,
    val title: String,
    val description: String,
    val positivity: Int
) {
    init {
        require(positivity in 0..100) { "positivity must be between 0 and 100" }
    }
}
