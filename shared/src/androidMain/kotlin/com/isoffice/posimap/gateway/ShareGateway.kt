package com.isoffice.posimap.gateway

import android.content.Context
import android.content.Intent
import androidx.core.content.FileProvider
import java.io.File

actual typealias ShareGatewayContext = Context

actual class ShareGateway actual constructor(
    context: ShareGatewayContext?,
) {
    private val applicationContext = requireNotNull(context) {
        "ShareGateway on Android requires a Context instance."
    }.applicationContext

    actual fun share(bytes: ByteArray, filename: String, mime: String) {
        val directory = File(applicationContext.cacheDir, "share").apply { mkdirs() }
        val file = File(directory, filename).apply { writeBytes(bytes) }
        val uri = FileProvider.getUriForFile(
            applicationContext,
            "${applicationContext.packageName}.provider",
            file,
        )

        val intent = Intent(Intent.ACTION_SEND).apply {
            type = mime
            putExtra(Intent.EXTRA_STREAM, uri)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }

        val chooser = Intent.createChooser(intent, "共有").apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }

        applicationContext.startActivity(chooser)
    }
}
