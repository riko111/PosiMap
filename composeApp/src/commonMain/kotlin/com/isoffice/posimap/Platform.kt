package com.isoffice.posimap

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform