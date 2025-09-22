package com.isoffice.posimap.android

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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.content.edit

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val initialSettings = loadSettings()
        setContent {
            MaterialTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    PerformanceSettingsScreen(
                        initialSettings = initialSettings,
                        onSaveSettings = { saveSettings(it) }
                    )
                }
            }
        }
    }

    private fun loadSettings(): PerformanceSettings? {
        val prefs = getPreferences(MODE_PRIVATE)
        val title = prefs.getString(KEY_TITLE, null) ?: return null
        val width = prefs.getInt(KEY_STAGE_WIDTH, -1)
        val height = prefs.getInt(KEY_STAGE_HEIGHT, -1)
        if (width <= 0 || height <= 0) return null
        return PerformanceSettings(title, width, height)
    }

    private fun saveSettings(settings: PerformanceSettings) {
        val prefs = getPreferences(MODE_PRIVATE)
        prefs.edit {
            putString(KEY_TITLE, settings.title)
                .putInt(KEY_STAGE_WIDTH, settings.stageWidth)
                .putInt(KEY_STAGE_HEIGHT, settings.stageHeight)
        }
    }

    companion object {
        private const val KEY_TITLE = "performance_title"
        private const val KEY_STAGE_WIDTH = "stage_width"
        private const val KEY_STAGE_HEIGHT = "stage_height"
    }
}

private data class PerformanceSettings(
    val title: String,
    val stageWidth: Int,
    val stageHeight: Int
)

@Composable
private fun PerformanceSettingsScreen(
    initialSettings: PerformanceSettings?,
    onSaveSettings: (PerformanceSettings) -> Unit,
    modifier: Modifier = Modifier
) {
    var currentSettings by rememberSaveable { mutableStateOf(initialSettings) }
    var isEditing by rememberSaveable { mutableStateOf(initialSettings == null) }
    var title by rememberSaveable { mutableStateOf(initialSettings?.title ?: "") }
    var widthText by rememberSaveable { mutableStateOf(initialSettings?.stageWidth?.toString() ?: "") }
    var heightText by rememberSaveable { mutableStateOf(initialSettings?.stageHeight?.toString() ?: "") }

    var titleError by remember { mutableStateOf<String?>(null) }
    var widthError by remember { mutableStateOf<String?>(null) }
    var heightError by remember { mutableStateOf<String?>(null) }

    val requiredErrorMessage = stringResource(R.string.input_error_required)
    val positiveNumberErrorMessage = stringResource(R.string.input_error_positive_number)
    val scrollState = rememberScrollState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(horizontal = 24.dp, vertical = 24.dp)
    ) {
        if (isEditing) {
            PerformanceSetupForm(
                title = title,
                width = widthText,
                height = heightText,
                titleError = titleError,
                widthError = widthError,
                heightError = heightError,
                onTitleChange = {
                    title = it
                    if (titleError != null) titleError = null
                },
                onWidthChange = {
                    widthText = it
                    if (widthError != null) widthError = null
                },
                onHeightChange = {
                    heightText = it
                    if (heightError != null) heightError = null
                },
                onSubmit = {
                    val trimmedTitle = title.trim()
                    val widthValue = widthText.trim().toIntOrNull()
                    val heightValue = heightText.trim().toIntOrNull()

                    var hasError = false
                    if (trimmedTitle.isEmpty()) {
                        titleError = requiredErrorMessage
                        hasError = true
                    } else {
                        titleError = null
                    }
                    if (widthValue == null || widthValue <= 0) {
                        widthError = positiveNumberErrorMessage
                        hasError = true
                    } else {
                        widthError = null
                    }
                    if (heightValue == null || heightValue <= 0) {
                        heightError = positiveNumberErrorMessage
                        hasError = true
                    } else {
                        heightError = null
                    }

                    if (!hasError) {
                        val newSettings = PerformanceSettings(
                            title = trimmedTitle,
                            stageWidth = widthValue!!,
                            stageHeight = heightValue!!
                        )
                        onSaveSettings(newSettings)
                        currentSettings = newSettings
                        title = newSettings.title
                        widthText = newSettings.stageWidth.toString()
                        heightText = newSettings.stageHeight.toString()
                        isEditing = false
                    }
                },
                modifier = Modifier.fillMaxWidth()
            )
        } else {
            currentSettings?.let { settings ->
                PerformanceSummary(
                    settings = settings,
                    onEdit = {
                        title = settings.title
                        widthText = settings.stageWidth.toString()
                        heightText = settings.stageHeight.toString()
                        titleError = null
                        widthError = null
                        heightError = null
                        isEditing = true
                    },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}

@Composable
private fun PerformanceSetupForm(
    title: String,
    width: String,
    height: String,
    titleError: String?,
    widthError: String?,
    heightError: String?,
    onTitleChange: (String) -> Unit,
    onWidthChange: (String) -> Unit,
    onHeightChange: (String) -> Unit,
    onSubmit: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(
                text = stringResource(R.string.performance_setup_title),
                style = MaterialTheme.typography.headlineSmall
            )
            Text(
                text = stringResource(R.string.performance_setup_description),
                style = MaterialTheme.typography.bodyMedium
            )
        }

        OutlinedTextField(
            value = title,
            onValueChange = onTitleChange,
            modifier = Modifier.fillMaxWidth(),
            label = { Text(text = stringResource(R.string.performance_title_label)) },
            singleLine = true,
            isError = titleError != null,
            supportingText = {
                if (titleError != null) {
                    Text(
                        text = titleError,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            },
            keyboardOptions = KeyboardOptions(
                capitalization = KeyboardCapitalization.Sentences,
                imeAction = ImeAction.Next
            )
        )

        OutlinedTextField(
            value = width,
            onValueChange = onWidthChange,
            modifier = Modifier.fillMaxWidth(),
            label = { Text(text = stringResource(R.string.stage_width_label)) },
            singleLine = true,
            isError = widthError != null,
            supportingText = {
                if (widthError != null) {
                    Text(
                        text = widthError,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Number,
                imeAction = ImeAction.Next
            )
        )

        OutlinedTextField(
            value = height,
            onValueChange = onHeightChange,
            modifier = Modifier.fillMaxWidth(),
            label = { Text(text = stringResource(R.string.stage_height_label)) },
            singleLine = true,
            isError = heightError != null,
            supportingText = {
                if (heightError != null) {
                    Text(
                        text = heightError,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Number,
                imeAction = ImeAction.Done
            )
        )

        Spacer(modifier = Modifier.height(8.dp))

        Button(
            onClick = onSubmit,
            modifier = Modifier.fillMaxWidth()
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
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = stringResource(R.string.summary_title_format, settings.title),
            style = MaterialTheme.typography.titleMedium
        )
        Text(
            text = stringResource(
                R.string.summary_stage_format,
                settings.stageWidth,
                settings.stageHeight
            ),
            style = MaterialTheme.typography.bodyLarge
        )
        OutlinedButton(onClick = onEdit) {
            Text(text = stringResource(R.string.edit_button))
        }
    }
}

@Preview(showBackground = true, name = "Setup Form")
@Composable
private fun PerformanceSetupFormPreview() {
    MaterialTheme {
        Surface {
            PerformanceSettingsScreen(initialSettings = null, onSaveSettings = {})
        }
    }
}

@Preview(showBackground = true, name = "Summary")
@Composable
private fun PerformanceSummaryPreview() {
    MaterialTheme {
        Surface {
            PerformanceSettingsScreen(
                initialSettings = PerformanceSettings("文化祭", 10, 6),
                onSaveSettings = {}
            )
        }
    }
}
