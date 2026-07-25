package qdvc.csv.android.app

import android.app.Application
import android.net.Uri
import android.provider.OpenableColumns
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.BufferedReader
import java.io.InputStreamReader
import java.nio.charset.StandardCharsets

sealed interface CsvUiState {
    data object Empty : CsvUiState
    data object Loading : CsvUiState
    data class Loaded(
        val fileName: String,
        val header: List<String>,
        val rows: List<List<String>>,
        val columnCount: Int,
    ) : CsvUiState
    data class Error(val message: String) : CsvUiState
}

class CsvViewModel(app: Application) : AndroidViewModel(app) {

    private val _state = MutableStateFlow<CsvUiState>(CsvUiState.Empty)
    val state: StateFlow<CsvUiState> = _state.asStateFlow()

    fun load(uri: Uri) {
        _state.value = CsvUiState.Loading
        viewModelScope.launch {
            val result = withContext(Dispatchers.IO) { readAndParse(uri) }
            _state.value = result
        }
    }

    private fun readAndParse(uri: Uri): CsvUiState {
        val context = getApplication<Application>()
        return try {
            val text = context.contentResolver.openInputStream(uri)?.use { stream ->
                BufferedReader(InputStreamReader(stream, StandardCharsets.UTF_8))
                    .readText()
            } ?: return CsvUiState.Error("Unable to open the selected file.")

            // Strip a UTF-8 BOM if present.
            val clean = text.removePrefix("\uFEFF")

            val parsed = CsvParser.parse(clean)
            if (parsed.rows.isEmpty()) {
                return CsvUiState.Error("The file appears to be empty.")
            }

            val header = parsed.rows.first()
            val body = parsed.rows.drop(1)
            val columnCount = parsed.rows.maxOf { it.size }

            CsvUiState.Loaded(
                fileName = queryDisplayName(uri),
                header = header,
                rows = body,
                columnCount = columnCount,
            )
        } catch (e: Exception) {
            CsvUiState.Error(e.message ?: "Failed to read the CSV file.")
        }
    }

    private fun queryDisplayName(uri: Uri): String {
        val context = getApplication<Application>()
        var name = uri.lastPathSegment ?: "CSV"
        try {
            context.contentResolver.query(uri, null, null, null, null)?.use { cursor ->
                val idx = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                if (idx >= 0 && cursor.moveToFirst()) {
                    cursor.getString(idx)?.let { name = it }
                }
            }
        } catch (_: Exception) {
            // Fall back to the path segment.
        }
        return name
    }
}
