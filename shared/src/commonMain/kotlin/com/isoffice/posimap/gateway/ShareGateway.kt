package com.isoffice.posimap.gateway

expect class ShareGateway(
    context: ShareGatewayContext? = null,
) {
    fun share(
        bytes: ByteArray,
        filename: String,
        mime: String = "application/vnd.posimap+json",
    )
}

expect class ShareGatewayContext
