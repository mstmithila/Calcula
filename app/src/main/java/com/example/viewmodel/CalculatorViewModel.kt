package com.example.viewmodel

import android.app.Application
import android.content.Context
import android.media.AudioManager
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import android.os.Build
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.database.HistoryEntry
import com.example.repository.AngleMode
import com.example.repository.HistoryRepository
import com.example.repository.SettingsRepository
import com.example.utils.MathParser
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class CalculatorViewModel(
    application: Application,
    private val historyRepository: HistoryRepository,
    private val settingsRepository: SettingsRepository
) : AndroidViewModel(application) {

    private val _expression = MutableStateFlow("")
    val expression: StateFlow<String> = _expression.asStateFlow()

    private val _result = MutableStateFlow("")
    val result: StateFlow<String> = _result.asStateFlow()

    private val _memoryValue = MutableStateFlow(0.0)
    val memoryValue: StateFlow<Double> = _memoryValue.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    // Observe settings reactively
    val settings = settingsRepository.settings

    // Observe history reactively
    val historyList: StateFlow<List<HistoryEntry>> = _searchQuery
        .flatMapLatest { query ->
            if (query.trim().isEmpty()) {
                historyRepository.allHistory
            } else {
                historyRepository.searchHistory(query)
            }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val favoritesList: StateFlow<List<HistoryEntry>> = historyRepository.favorites
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val audioManager = application.getSystemService(Context.AUDIO_SERVICE) as? AudioManager
    private val vibrator = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        val vibratorManager = application.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager
        vibratorManager?.defaultVibrator
    } else {
        @Suppress("DEPRECATION")
        application.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
    }

    // Append standard character/operators
    fun append(value: String) {
        playFeedback()
        val current = _expression.value
        
        // Handle nice auto-multiplication additions:
        // If current expression ends with digit, pi, e, or ')' and appending '(' or 'sin' etc., auto-append '×'
        val isAppendingFunction = value.endsWith("(") || value == "π" || value == "e"
        if (current.isNotEmpty() && isAppendingFunction) {
            val lastChar = current.last()
            if (lastChar.isDigit() || lastChar == ')' || lastChar == 'π' || lastChar == 'e') {
                _expression.value = current + "×" + value
                return
            }
        }
        
        _expression.value = current + value
    }

    // Append function and open parenthesis
    fun appendFunction(func: String) {
        append("$func(")
    }

    // Toggle degree and radian mode
    fun toggleAngleMode() {
        playFeedback()
        val currentMode = settings.value.angleMode
        val nextMode = if (currentMode == AngleMode.DEGREE) AngleMode.RADIAN else AngleMode.DEGREE
        settingsRepository.updateAngleMode(nextMode)
    }

    // Backspace removes last character or whole function name
    fun backspace() {
        playFeedback()
        val current = _expression.value
        if (current.isEmpty()) return

        // Check if expression ends with common function calls to delete them in one go
        val functions = listOf(
            "sin(", "cos(", "tan(", "asin(", "acos(", "atan(",
            "sinh(", "cosh(", "tanh(", "log(", "ln(", "sqrt(", "cbrt(", "abs(", "exp("
        )
        for (func in functions) {
            if (current.endsWith(func)) {
                _expression.value = current.substring(0, current.length - func.length)
                return
            }
        }

        // Default backspace
        _expression.value = current.substring(0, current.length - 1)
    }

    // Clear current expression
    fun clear() {
        playFeedback()
        _expression.value = ""
        _result.value = ""
    }

    // Evaluate the expression and automatically save to Room database history
    fun evaluate() {
        playFeedback()
        val currentExpr = _expression.value
        if (currentExpr.trim().isEmpty()) return

        viewModelScope.launch {
            try {
                val isDeg = settings.value.angleMode == AngleMode.DEGREE
                val parser = MathParser(isDegreeMode = isDeg)
                val rawResult = parser.evaluate(currentExpr)
                
                // Format the result based on decimal precision
                val precision = settings.value.decimalPrecision
                val formattedResult = formatResult(rawResult, precision)
                _result.value = formattedResult

                // Auto-save history
                historyRepository.insert(
                    HistoryEntry(
                        expression = currentExpr,
                        result = formattedResult,
                        timestamp = System.currentTimeMillis()
                    )
                )
            } catch (e: Exception) {
                Log.e("CalculatorViewModel", "Evaluation error", e)
                _result.value = e.message ?: "Error"
            }
        }
    }

    private fun formatResult(value: Double, precision: Int): String {
        if (value.isNaN()) return "NaN"
        if (value.isInfinite()) return if (value > 0) "Infinity" else "-Infinity"
        
        // If it's a whole number, format without decimals
        if (value == value.toLong().toDouble()) {
            return value.toLong().toString()
        }

        // Limit decimal places to precision
        return try {
            val formatStr = "%.${precision}f".format(java.util.Locale.US, value)
            // Strip trailing zeros and point if needed
            var cleaned = formatStr
            if (cleaned.contains(".")) {
                cleaned = cleaned.trimEnd('0').trimEnd('.')
            }
            cleaned
        } catch (e: Exception) {
            value.toString()
        }
    }

    // Memory operations
    fun memoryClear() {
        playFeedback()
        _memoryValue.value = 0.0
    }

    fun memoryRecall() {
        playFeedback()
        val mem = _memoryValue.value
        // If decimal or integer
        val memStr = if (mem == mem.toLong().toDouble()) mem.toLong().toString() else mem.toString()
        append(memStr)
    }

    fun memoryAdd() {
        playFeedback()
        val currentExpr = _expression.value
        if (currentExpr.isEmpty()) return
        viewModelScope.launch {
            try {
                val isDeg = settings.value.angleMode == AngleMode.DEGREE
                val parser = MathParser(isDegreeMode = isDeg)
                val evaluated = parser.evaluate(currentExpr)
                _memoryValue.value += evaluated
            } catch (e: Exception) {
                _result.value = "Error"
            }
        }
    }

    fun memorySubtract() {
        playFeedback()
        val currentExpr = _expression.value
        if (currentExpr.isEmpty()) return
        viewModelScope.launch {
            try {
                val isDeg = settings.value.angleMode == AngleMode.DEGREE
                val parser = MathParser(isDegreeMode = isDeg)
                val evaluated = parser.evaluate(currentExpr)
                _memoryValue.value -= evaluated
            } catch (e: Exception) {
                _result.value = "Error"
            }
        }
    }

    fun memoryStore() {
        playFeedback()
        val currentExpr = _expression.value
        if (currentExpr.isEmpty()) return
        viewModelScope.launch {
            try {
                val isDeg = settings.value.angleMode == AngleMode.DEGREE
                val parser = MathParser(isDegreeMode = isDeg)
                val evaluated = parser.evaluate(currentExpr)
                _memoryValue.value = evaluated
            } catch (e: Exception) {
                _result.value = "Error"
            }
        }
    }

    // Toggle favorite status of a history entry
    fun toggleFavorite(entry: HistoryEntry) {
        viewModelScope.launch {
            historyRepository.update(entry.copy(isFavorite = !entry.isFavorite))
        }
    }

    // Delete a specific history entry
    fun deleteHistoryEntry(entry: HistoryEntry) {
        viewModelScope.launch {
            historyRepository.delete(entry)
        }
    }

    // Clear all history
    fun clearHistory() {
        viewModelScope.launch {
            historyRepository.clearAll()
        }
    }

    fun search(query: String) {
        _searchQuery.value = query
    }

    // Backup current history list as a simple JSON string
    fun exportHistoryAsJson(): String {
        val list = historyList.value
        val sb = java.lang.StringBuilder()
        sb.append("[")
        for (i in list.indices) {
            val entry = list[i]
            sb.append("{")
            sb.append("\"expression\":\"${entry.expression.replace("\"", "\\\"")}\",")
            sb.append("\"result\":\"${entry.result.replace("\"", "\\\"")}\",")
            sb.append("\"timestamp\":${entry.timestamp},")
            sb.append("\"isFavorite\":${entry.isFavorite}")
            sb.append("}")
            if (i < list.size - 1) sb.append(",")
        }
        sb.append("]")
        return sb.toString()
    }

    // Restore history list from simple JSON string
    fun importHistoryFromJson(json: String): Boolean {
        return try {
            val list = mutableListOf<HistoryEntry>()
            // Very basic but perfectly safe manual JSON parsing for backup recovery
            val normalized = json.trim()
            if (!normalized.startsWith("[") || !normalized.endsWith("]")) return false
            val content = normalized.substring(1, normalized.length - 1)
            if (content.trim().isEmpty()) return true
            
            val objects = content.split("},{")
            for (obj in objects) {
                var cleanedObj = obj.trim()
                if (!cleanedObj.startsWith("{")) cleanedObj = "{$cleanedObj"
                if (!cleanedObj.endsWith("}")) cleanedObj = "$cleanedObj}"
                
                val expr = extractJsonKeyValue(cleanedObj, "expression") ?: ""
                val res = extractJsonKeyValue(cleanedObj, "result") ?: ""
                val ts = extractJsonKeyValue(cleanedObj, "timestamp")?.toLongOrNull() ?: System.currentTimeMillis()
                val isFav = extractJsonKeyValue(cleanedObj, "isFavorite")?.toBoolean() ?: false
                
                list.add(HistoryEntry(expression = expr, result = res, timestamp = ts, isFavorite = isFav))
            }
            
            if (list.isNotEmpty()) {
                viewModelScope.launch {
                    historyRepository.restoreAll(list)
                }
            }
            true
        } catch (e: Exception) {
            Log.e("CalculatorViewModel", "JSON restore failed", e)
            false
        }
    }

    private fun extractJsonKeyValue(json: String, key: String): String? {
        val target = "\"$key\":"
        val index = json.indexOf(target)
        if (index == -1) return null
        val startVal = index + target.length
        val endChar = json.getOrNull(startVal)
        return if (endChar == '"') {
            // String value
            val endQuoteIndex = json.indexOf('"', startVal + 1)
            if (endQuoteIndex == -1) return null
            json.substring(startVal + 1, endQuoteIndex)
        } else {
            // Number or boolean value
            var endIndex = startVal
            while (endIndex < json.length && json[endIndex] != ',' && json[endIndex] != '}') {
                endIndex++
            }
            json.substring(startVal, endIndex).trim()
        }
    }

    // Tactile & audio feedback
    private fun playFeedback() {
        val sets = settings.value
        if (sets.vibrateEnabled) {
            try {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    vibrator?.vibrate(VibrationEffect.createOneShot(15, VibrationEffect.DEFAULT_AMPLITUDE))
                } else {
                    @Suppress("DEPRECATION")
                    vibrator?.vibrate(15)
                }
            } catch (e: Exception) {
                // Ignore silent vibrator errors
            }
        }
        if (sets.soundEnabled) {
            try {
                audioManager?.playSoundEffect(AudioManager.FX_KEYPRESS_STANDARD)
            } catch (e: Exception) {
                // Ignore sound errors
            }
        }
    }

    fun updateThemeMode(mode: com.example.repository.AppThemeMode) {
        settingsRepository.updateThemeMode(mode)
    }

    fun updateVibrateEnabled(enabled: Boolean) {
        settingsRepository.updateVibrateEnabled(enabled)
    }

    fun updateSoundEnabled(enabled: Boolean) {
        settingsRepository.updateSoundEnabled(enabled)
    }

    fun updateDecimalPrecision(precision: Int) {
        settingsRepository.updateDecimalPrecision(precision)
    }

    fun updateColorTheme(theme: String) {
        settingsRepository.updateColorTheme(theme)
    }

    fun resetSettings() {
        settingsRepository.resetSettings()
    }
}
