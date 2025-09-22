package com.isoffice.posimap.android

import android.content.Context
import android.content.Intent
import androidx.core.content.ContextCompat.startActivity
import com.isoffice.posimap.gateway.ShareGateway
import com.isoffice.posimap.model.Performance

class ShareGatewayAndroid(private val context: Context) : ShareGateway {
    override fun sharePerformance(performance: Performance) {
        val shareIntent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_SUBJECT, performance.title)
            putExtra(
                Intent.EXTRA_TEXT,
                "${performance.title} (${performance.positivity}%)\n${performance.description}"
            )
        }

        val chooser = Intent.createChooser(shareIntent, context.getString(android.R.string.share))
        chooser.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(context, chooser, null)
    }
}
