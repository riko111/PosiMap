package com.isoffice.posimap.android

import android.content.Context
import android.content.Intent
import androidx.core.content.FileProvider
import com.isoffice.posimap.gateway.ShareGateway
import java.io.File

class ShareGateway(private val context: Context) : ShareGateway {
    override fun share(bytes: ByteArray, filename: String, mime: String) {
        val dir = File(context.cacheDir, "share").apply { mkdirs() }
        val f = File(dir, filename).apply { writeBytes(bytes) }
        val uri = FileProvider.getUriForFile(context, "${context.packageName}.provider", f)

        val intent = Intent(Intent.ACTION_SEND).apply {
            type = mime
            putExtra(Intent.EXTRA_STREAM, uri)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        context.startActivity(Intent.createChooser(intent, "共有"))
    }
}
