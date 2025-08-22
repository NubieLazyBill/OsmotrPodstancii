package org.example

import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.File
import java.util.UUID

// Добавим новый файл InspectionRepository.kt
object InspectionRepository {
    private val sessions = mutableListOf<InspectionSession>()
    private val json = Json { prettyPrint = true }

    fun saveSession(session: InspectionSession) {
        sessions.add(session)
        saveToFile()
    }

    fun getSessions(): List<InspectionSession> = sessions.toList()

    fun saveToFile() { // Убрал private
        try {
            val file = File("inspections.json")
            file.writeText(json.encodeToString(sessions))
        } catch (e: Exception) {
            println("Ошибка сохранения: ${e.message}")
        }
    }

    fun loadFromFile() {
        try {
            val file = File("inspections.json")
            if (file.exists()) {
                sessions.clear()
                sessions.addAll(json.decodeFromString<List<InspectionSession>>(file.readText()))
            }
        } catch (e: Exception) {
            println("Ошибка загрузки: ${e.message}")
        }
    }
}