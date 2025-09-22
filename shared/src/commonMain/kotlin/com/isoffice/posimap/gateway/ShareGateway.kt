package com.isoffice.posimap.gateway


interface ShareGateway {
    fun share(bytes: ByteArray, filename: String, mime: String = "application/vnd.posimap+json")
}

