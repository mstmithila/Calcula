package com.example.repository

import com.example.database.HistoryDao
import com.example.database.HistoryEntry
import kotlinx.coroutines.flow.Flow

class HistoryRepository(private val historyDao: HistoryDao) {
    val allHistory: Flow<List<HistoryEntry>> = historyDao.getAllHistory()
    val favorites: Flow<List<HistoryEntry>> = historyDao.getFavorites()

    fun searchHistory(query: String): Flow<List<HistoryEntry>> {
        return historyDao.searchHistory("%$query%")
    }

    suspend fun insert(entry: HistoryEntry): Long {
        return historyDao.insertHistory(entry)
    }

    suspend fun update(entry: HistoryEntry) {
        historyDao.updateHistory(entry)
    }

    suspend fun delete(entry: HistoryEntry) {
        historyDao.deleteHistory(entry)
    }

    suspend fun deleteById(id: Long) {
        historyDao.deleteHistoryById(id)
    }

    suspend fun clearAll() {
        historyDao.clearAllHistory()
    }

    suspend fun restoreAll(entries: List<HistoryEntry>) {
        historyDao.insertAll(entries)
    }
}
