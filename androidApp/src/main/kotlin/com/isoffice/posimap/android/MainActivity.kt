package com.isoffice.posimap.android

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.weight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardOptions
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val initialSettings = loadSettings()
        setContent {
            MaterialTheme {
                PerformanceSetupScreen(
                    initialSettings = initialSettings,
                    onSaveSettings = { settings ->
                        saveSettings(settings)
                    }
                )
            }
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

    companion object {
        private const val KEY_TITLE = "performance_title"
        private const val KEY_STAGE_WIDTH = "stage_width"
        private const val KEY_STAGE_HEIGHT = "stage_height"
    }
}

@Composable
private fun PerformanceSetupScreen(
    initialSettings: PerformanceSettings?,
    onSaveSettings: (PerformanceSettings) -> Unit,
    modifier: Modifier = Modifier
) {
    var settings by remember { mutableStateOf(initialSettings) }
    var showForm by remember { mutableStateOf(initialSettings == null) }

    var title by remember { mutableStateOf(initialSettings?.title ?: "") }
    var stageWidth by remember { mutableStateOf(initialSettings?.stageWidth?.toString() ?: "") }
    var stageHeight by remember { mutableStateOf(initialSettings?.stageHeight?.toString() ?: "") }

    var titleError by remember { mutableStateOf<String?>(null) }
    var stageWidthError by remember { mutableStateOf<String?>(null) }
    var stageHeightError by remember { mutableStateOf<String?>(null) }

    val focusManager = LocalFocusManager.current
    val requiredError = stringResource(R.string.input_error_required)
    val positiveNumberError = stringResource(R.string.input_error_positive_number)

    Surface(modifier = modifier.fillMaxSize()) {
        if (showForm) {
            PerformanceSetupForm(
                title = title,
                onTitleChange = {
                    title = it
                    if (titleError != null) titleError = null
                },
                titleError = titleError,
                stageWidth = stageWidth,
                onStageWidthChange = {
                    stageWidth = it
                    if (stageWidthError != null) stageWidthError = null
                },
                stageWidthError = stageWidthError,
                stageHeight = stageHeight,
                onStageHeightChange = {
                    stageHeight = it
                    if (stageHeightError != null) stageHeightError = null
                },
                stageHeightError = stageHeightError,
                onSubmit = {
                    val trimmedTitle = title.trim()
                    val widthValue = stageWidth.trim().toIntOrNull()
                    val heightValue = stageHeight.trim().toIntOrNull()

                    val newTitleError = if (trimmedTitle.isEmpty()) requiredError else null
                    val newWidthError = if (widthValue == null || widthValue <= 0) positiveNumberError else null
                    val newHeightError = if (heightValue == null || heightValue <= 0) positiveNumberError else null

                    titleError = newTitleError
                    stageWidthError = newWidthError
                    stageHeightError = newHeightError

                    if (newTitleError == null && newWidthError == null && newHeightError == null) {
                        val newSettings = PerformanceSettings(trimmedTitle, widthValue!!, heightValue!!)
                        onSaveSettings(newSettings)
                        settings = newSettings
                        showForm = false
                        focusManager.clearFocus()
                    }
                }
            )
        } else {
            val current = settings
            if (current != null) {
                PerformanceSummary(
                    settings = current,
                    onEdit = {
                        title = current.title
                        stageWidth = current.stageWidth.toString()
                        stageHeight = current.stageHeight.toString()
                        titleError = null
                        stageWidthError = null
                        stageHeightError = null
                        showForm = true
                    }
                )
            } else {
                showForm = true
            }
        }
    }
}

@Composable
private fun PerformanceSetupForm(
    title: String,
    onTitleChange: (String) -> Unit,
    titleError: String?,
    stageWidth: String,
    onStageWidthChange: (String) -> Unit,
    stageWidthError: String?,
    stageHeight: String,
    onStageHeightChange: (String) -> Unit,
    stageHeightError: String?,
    onSubmit: () -> Unit,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(horizontal = 24.dp, vertical = 32.dp),
        verticalArrangement = Arrangement.Top
    ) {
        Text(
            text = stringResource(R.string.performance_setup_title),
            style = MaterialTheme.typography.headlineSmall
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = stringResource(R.string.performance_setup_description),
            style = MaterialTheme.typography.bodyMedium
        )
        Spacer(modifier = Modifier.height(24.dp))
        OutlinedTextField(
            value = title,
            onValueChange = onTitleChange,
            modifier = Modifier.fillMaxWidth(),
            label = { Text(text = stringResource(R.string.performance_title_label)) },
            singleLine = true,
            isError = titleError != null
        )
        if (titleError != null) {
            ErrorText(text = titleError)
        }
        Spacer(modifier = Modifier.height(16.dp))
        OutlinedTextField(
            value = stageWidth,
            onValueChange = onStageWidthChange,
            modifier = Modifier.fillMaxWidth(),
            label = { Text(text = stringResource(R.string.stage_width_label)) },
            singleLine = true,
            isError = stageWidthError != null,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
        )
        if (stageWidthError != null) {
            ErrorText(text = stageWidthError)
        }
        Spacer(modifier = Modifier.height(16.dp))
        OutlinedTextField(
            value = stageHeight,
            onValueChange = onStageHeightChange,
            modifier = Modifier.fillMaxWidth(),
            label = { Text(text = stringResource(R.string.stage_height_label)) },
            singleLine = true,
            isError = stageHeightError != null,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
        )
        if (stageHeightError != null) {
            ErrorText(text = stageHeightError)
        }
        Spacer(modifier = Modifier.height(24.dp))
        Button(
            onClick = onSubmit,
            modifier = Modifier.align(Alignment.End)
        ) {
            Text(text = stringResource(R.string.setup_submit))
        }
    }
}

@Composable
private fun PerformanceSummary(
    settings: PerformanceSettings,
    onEdit: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp, vertical = 32.dp),
        verticalArrangement = Arrangement.Top
    ) {
        Text(
            text = stringResource(R.string.performance_setup_title),
            style = MaterialTheme.typography.headlineSmall
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = stringResource(R.string.summary_title_format, settings.title),
            style = MaterialTheme.typography.bodyLarge
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = stringResource(
                R.string.summary_stage_format,
                settings.stageWidth,
                settings.stageHeight
            ),
            style = MaterialTheme.typography.bodyLarge
        )
        Spacer(modifier = Modifier.weight(1f))
        Button(
            onClick = onEdit,
            modifier = Modifier.align(Alignment.End)
        ) {
            Text(text = stringResource(R.string.edit_button))
        }
    }
}

@Composable
private fun ErrorText(text: String, modifier: Modifier = Modifier) {
    Text(
        text = text,
        color = MaterialTheme.colorScheme.error,
        style = MaterialTheme.typography.bodySmall,
        modifier = modifier
            .padding(top = 4.dp)
    )
}

private data class PerformanceSettings(
    val title: String,
    val stageWidth: Int,
    val stageHeight: Int
)
