// InspectionRepository.kt
package org.example

import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.File
import java.util.UUID

object InspectionRepository {
    private val sessions = mutableListOf<InspectionSession>()
    private val json = Json { prettyPrint = true }
    private val dataFile = File("inspections.json")

    init {
        loadFromFile() // Загружаем данные при инициализации
    }

    fun saveSession(session: InspectionSession) {
        sessions.add(session)
        saveToFile() // Сохраняем автоматически при добавлении
    }

    fun getSessions(): List<InspectionSession> = sessions.toList()

    fun clearSessions() {
        sessions.clear()
        saveToFile() // Сохраняем при очистке
    }

    // Сделаем этот метод internal, чтобы он был доступен в том же модуле
    internal fun saveToFile() {
        try {
            dataFile.writeText(json.encodeToString(sessions))
            println("Данные сохранены в файл: ${dataFile.absolutePath}")
        } catch (e: Exception) {
            println("Ошибка сохранения: ${e.message}")
        }
    }

    private fun loadFromFile() {
        try {
            if (dataFile.exists()) {
                val fileContent = dataFile.readText()
                if (fileContent.isNotBlank()) {
                    sessions.clear()
                    sessions.addAll(json.decodeFromString<List<InspectionSession>>(fileContent))
                    println("Загружено ${sessions.size} сессий из файла")
                }
            } else {
                println("Файл данных не существует, будет создан при первом сохранении")
            }
        } catch (e: Exception) {
            println("Ошибка загрузки: ${e.message}")
            // Создаем backup поврежденного файла
            if (dataFile.exists()) {
                try {
                    val backupFile = File("inspections_backup_${System.currentTimeMillis()}.json")
                    dataFile.copyTo(backupFile)
                    println("Создан backup поврежденного файла: ${backupFile.name}")
                } catch (backupError: Exception) {
                    println("Ошибка создания backup: ${backupError.message}")
                }
            }
        }
    }

    // Исправленная функция удаления сессии
    fun deleteSession(sessionId: String) {
        val iterator = sessions.iterator()
        while (iterator.hasNext()) {
            if (iterator.next().id == sessionId) {
                iterator.remove()
                saveToFile()
                println("Сессия $sessionId удалена")
                return
            }
        }
        println("Сессия $sessionId не найдена")
    }
}


internal fun exportSessionsToCSV(fileName: String = "inspections_export_${System.currentTimeMillis()}.csv") {
    try {
        val file = File(fileName)
        // Явно указываем кодировку UTF-8 с BOM
        val writer = file.bufferedWriter(Charsets.UTF_8)

        // Добавляем BOM (Byte Order Mark) для UTF-8
        writer.write("\uFEFF")

        // Заголовок CSV с разделителем-точкой с запятой (стандарт для Excel)
        writer.write("Дата осмотра;Время осмотра;ОРУ;Напряжение;Оборудование;Тип оборудования;Параметр;Значение;Нормальное значение;Комментарии")
        writer.newLine()

        InspectionRepository.getSessions().forEach { session ->
            session.results.forEach { result ->
                result.parameters.forEach { (paramName, value) ->
                    // Находим нормальное значение для этого параметра
                    val normalValue = result.equipment.parameters
                        .find { it.name == paramName }
                        ?.normalValue ?: ""

                    // Разделяем дату и время для удобства сортировки в Excel
                    val dateTimeParts = session.dateTimeString.split(" ")
                    val date = dateTimeParts.getOrElse(0) { "" }
                    val time = dateTimeParts.getOrElse(1) { "" }

                    writer.write(
                        "$date;" +  // Дата
                                "$time;" +  // Время
                                "${session.oru.name};" +  // Название ОРУ
                                "${session.oru.voltage};" +  // Напряжение
                                "${result.equipment.name};" +  // Оборудование
                                "${result.equipment.type};" +  // Тип оборудования
                                "$paramName;" +  // Название параметра
                                "$value;" +  // Значение параметра
                                "$normalValue;" +  // Нормальное значение
                                "${result.comments}"  // Комментарии
                    )
                    writer.newLine()
                }
            }
        }

        writer.close()
        println("Данные экспортированы в: ${file.absolutePath}")

        // Показываем сообщение пользователю (опционально)
        showExportSuccessMessage(file.absolutePath)

    } catch (e: Exception) {
        println("Ошибка экспорта: ${e.message}")
    }
}

// Дополнительная функция для показа сообщения (если нужно)
private fun showExportSuccessMessage(filePath: String) {
    // Здесь можно добавить вывод в лог или всплывающее окно
    println("✅ Экспорт завершен! Файл: $filePath")
}