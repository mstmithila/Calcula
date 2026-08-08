package com.example.viewmodel

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.repository.HistoryRepository
import com.example.repository.SettingsRepository

class ViewModelFactory(
    private val application: Application,
    private val historyRepository: HistoryRepository,
    private val settingsRepository: SettingsRepository
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(CalculatorViewModel::class.java) -> {
                CalculatorViewModel(application, historyRepository, settingsRepository) as T
            }
            modelClass.isAssignableFrom(ConvertersViewModel::class.java) -> {
                ConvertersViewModel() as T
            }
            else -> throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
        }
    }
}
