package com.example.repository

import android.content.Context
import android.content.SharedPreferences
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

enum class AppThemeMode {
    SYSTEM, LIGHT, DARK
}

enum class AngleMode {
    DEGREE, RADIAN
}

data class AppSettings(
    val themeMode: AppThemeMode = AppThemeMode.SYSTEM,
    val vibrateEnabled: Boolean = true,
    val soundEnabled: Boolean = false,
    val decimalPrecision: Int = 6,
    val angleMode: AngleMode = AngleMode.DEGREE,
    val colorTheme: String = "classic" // classic, teal, orange, purple, dynamic
)

class SettingsRepository(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("calc_settings", Context.MODE_PRIVATE)
    
    private val _settings = MutableStateFlow(loadSettings())
    val settings: StateFlow<AppSettings> = _settings.asStateFlow()

    private fun loadSettings(): AppSettings {
        val themeStr = prefs.getString("theme_mode", AppThemeMode.SYSTEM.name) ?: AppThemeMode.SYSTEM.name
        val angleStr = prefs.getString("angle_mode", AngleMode.DEGREE.name) ?: AngleMode.DEGREE.name
        
        return AppSettings(
            themeMode = try { AppThemeMode.valueOf(themeStr) } catch(e: Exception) { AppThemeMode.SYSTEM },
            vibrateEnabled = prefs.getBoolean("vibrate_enabled", true),
            soundEnabled = prefs.getBoolean("sound_enabled", false),
            decimalPrecision = prefs.getInt("decimal_precision", 6),
            angleMode = try { AngleMode.valueOf(angleStr) } catch(e: Exception) { AngleMode.DEGREE },
            colorTheme = prefs.getString("color_theme", "classic") ?: "classic"
        )
    }

    fun updateThemeMode(mode: AppThemeMode) {
        prefs.edit().putString("theme_mode", mode.name).apply()
        notifyChange()
    }

    fun updateVibrateEnabled(enabled: Boolean) {
        prefs.edit().putBoolean("vibrate_enabled", enabled).apply()
        notifyChange()
    }

    fun updateSoundEnabled(enabled: Boolean) {
        prefs.edit().putBoolean("sound_enabled", enabled).apply()
        notifyChange()
    }

    fun updateDecimalPrecision(precision: Int) {
        prefs.edit().putInt("decimal_precision", precision.coerceIn(0, 10)).apply()
        notifyChange()
    }

    fun updateAngleMode(mode: AngleMode) {
        prefs.edit().putString("angle_mode", mode.name).apply()
        notifyChange()
    }

    fun updateColorTheme(theme: String) {
        prefs.edit().putString("color_theme", theme).apply()
        notifyChange()
    }

    fun resetSettings() {
        prefs.edit().clear().apply()
        notifyChange()
    }

    private fun notifyChange() {
        _settings.value = loadSettings()
    }
}
