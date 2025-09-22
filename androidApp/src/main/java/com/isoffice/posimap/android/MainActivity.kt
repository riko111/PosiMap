package com.isoffice.posimap.android

import android.os.Bundle
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.isoffice.posimap.model.Performance
import com.isoffice.posimap.repository.PerformanceRepository

class MainActivity : AppCompatActivity() {

    private val repository by lazy {
        PerformanceRepository(ShareGatewayAndroid(this))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val container = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(48, 48, 48, 48)
        }

        container.addView(
            TextView(this).apply {
                textSize = 22f
                text = "PosiMap Performances"
            }
        )

        repository.getPerformances()
            .forEach { performance ->
                container.addView(createPerformanceView(performance))
            }

        setContentView(container)
    }

    private fun createPerformanceView(performance: Performance): TextView =
        TextView(this).apply {
            textSize = 16f
            text = "• ${performance.title} (${performance.positivity}%)\n${performance.description}"
            setOnClickListener { repository.sharePerformance(performance) }
        }
}
