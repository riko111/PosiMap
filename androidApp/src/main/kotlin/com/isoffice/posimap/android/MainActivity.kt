package com.isoffice.posimap.android

import android.content.Context
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val formContainer: View = findViewById(R.id.formContainer)
        val summaryContainer: View = findViewById(R.id.summaryContainer)
        val titleLayout: TextInputLayout = findViewById(R.id.performanceTitleLayout)
        val titleInput: TextInputEditText = findViewById(R.id.performanceTitleInput)
        val widthLayout: TextInputLayout = findViewById(R.id.stageWidthLayout)
        val widthInput: TextInputEditText = findViewById(R.id.stageWidthInput)
        val heightLayout: TextInputLayout = findViewById(R.id.stageHeightLayout)
        val heightInput: TextInputEditText = findViewById(R.id.stageHeightInput)
        val summaryTitle: TextView = findViewById(R.id.summaryTitle)
        val summaryStage: TextView = findViewById(R.id.summaryStage)
        val saveButton: Button = findViewById(R.id.saveButton)
        val editButton: Button = findViewById(R.id.editButton)

        val settings = loadSettings()
        if (settings != null) {
            updateSummary(summaryTitle, summaryStage, settings)
            formContainer.visibility = View.GONE
            summaryContainer.visibility = View.VISIBLE
        } else {
            summaryContainer.visibility = View.GONE
            formContainer.visibility = View.VISIBLE
        }

        saveButton.setOnClickListener {
            titleLayout.error = null
            widthLayout.error = null
            heightLayout.error = null

            val title = titleInput.text?.toString()?.trim().orEmpty()
            val width = widthInput.text?.toString()?.trim()?.toIntOrNull()
            val height = heightInput.text?.toString()?.trim()?.toIntOrNull()

            var hasError = false
            if (title.isEmpty()) {
                titleLayout.error = getString(R.string.input_error_required)
                hasError = true
            }
            if (width == null || width <= 0) {
                widthLayout.error = getString(R.string.input_error_positive_number)
                hasError = true
            }
            if (height == null || height <= 0) {
                heightLayout.error = getString(R.string.input_error_positive_number)
                hasError = true
            }

            if (!hasError) {
                val newSettings = PerformanceSettings(title, width!!, height!!)
                saveSettings(newSettings)
                updateSummary(summaryTitle, summaryStage, newSettings)
                formContainer.visibility = View.GONE
                summaryContainer.visibility = View.VISIBLE
            }
        }

        editButton.setOnClickListener {
            val current = loadSettings()
            if (current != null) {
                titleInput.setText(current.title)
                widthInput.setText(current.stageWidth.toString())
                heightInput.setText(current.stageHeight.toString())
            }
            summaryContainer.visibility = View.GONE
            formContainer.visibility = View.VISIBLE
        }
    }

    private fun loadSettings(): PerformanceSettings? {
        val prefs = getPreferences(Context.MODE_PRIVATE)
        val title = prefs.getString(KEY_TITLE, null) ?: return null
        val width = prefs.getInt(KEY_STAGE_WIDTH, -1)
        val height = prefs.getInt(KEY_STAGE_HEIGHT, -1)
        if (width <= 0 || height <= 0) return null
        return PerformanceSettings(title, width, height)
    }

    private fun saveSettings(settings: PerformanceSettings) {
        val prefs = getPreferences(Context.MODE_PRIVATE)
        prefs.edit()
            .putString(KEY_TITLE, settings.title)
            .putInt(KEY_STAGE_WIDTH, settings.stageWidth)
            .putInt(KEY_STAGE_HEIGHT, settings.stageHeight)
            .apply()
    }

    private fun updateSummary(titleView: TextView, stageView: TextView, settings: PerformanceSettings) {
        titleView.text = getString(R.string.summary_title_format, settings.title)
        stageView.text = getString(R.string.summary_stage_format, settings.stageWidth, settings.stageHeight)
    }

    private data class PerformanceSettings(val title: String, val stageWidth: Int, val stageHeight: Int)

    companion object {
        private const val KEY_TITLE = "performance_title"
        private const val KEY_STAGE_WIDTH = "stage_width"
        private const val KEY_STAGE_HEIGHT = "stage_height"
    }
}
