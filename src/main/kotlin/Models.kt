package org.example

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

enum class EquipmentType {
    POWER_TRANSFORMER,
    CIRCUIT_BREAKER,
    CURRENT_TRANSFORMER,
    VOLTAGE_TRANSFORMER,
}

data class Oru(
    val voltage: String,
    val name: String,
    val equipments: List<Equipment>
)

data class Equipment(
    val id: String,
    val name: String,
    val type: EquipmentType,
    val parameters: List<InspectionParameter>
)

data class InspectionParameter(
    val name: String,
    val unit: String?,
    val normalValue: String
)

data class InspectionResult(
    val oru: Oru,
    val equipment: Equipment,
    val parameters: Map<String, String>,
    val dateTime: String = LocalDateTime.now()
        .format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm")),
    val comments: String = ""
)

enum class AppScreen {
    ORU_SELECTION,
    ORU_INSPECTION  // Новый экран для осмотра всего ОРУ
}

object SubstationData {
    val oru500 = Oru(
        voltage = "500",
        name = "ОРУ-500",
        equipments = listOf(
            // АТГ
            Equipment(
                id = "2АТГ",
                name = "2АТГ",
                type = EquipmentType.POWER_TRANSFORMER,
                parameters = listOf(
                    InspectionParameter("Уровень масла РУМ", "°C", "30±2"),
                    InspectionParameter("Температура ТС", "°C", "<85")
                )
            ),

        )
    )

    val oru220 = Oru(
        voltage = "220",
        name = "ОРУ-220",
        equipments = listOf(
            //Мирная
            Equipment(
                id = "В-220 Мирная",
                name = "В-220 Мирная",
                type = EquipmentType.CIRCUIT_BREAKER,
                parameters = listOf(
                    InspectionParameter("Уровень продувки ф.А", "", "0.5-1"),
                    InspectionParameter("Уровень продувки ф.В", "", "0.5-1"),
                    InspectionParameter("Уровень продувки ф.С", "", "0.5-1"),
                )
            ),
            Equipment(
                id = "ТТ-220 Мирная",
                name = "ТТ-220 Мирная",
                type = EquipmentType.CURRENT_TRANSFORMER,
                parameters = listOf(
                    InspectionParameter("Уровень масла ф.А", "", ""),
                    InspectionParameter("Уровень масла ф.В", "", ""),
                    InspectionParameter("Уровень масла ф.С", "", ""),
                )
            ),
            //Топаз
            Equipment(
                id = "В-220 Топаз",
                name = "В-220 Топаз",
                type = EquipmentType.CIRCUIT_BREAKER,
                parameters = listOf(
                    InspectionParameter("Уровень продувки ф.А", "", "0.5-1"),
                    InspectionParameter("Уровень продувки ф.В", "", "0.5-1"),
                    InspectionParameter("Уровень продувки ф.С", "", "0.5-1"),
                )
            ),
            Equipment(
                id = "ТТ-220 Топаз",
                name = "ТТ-220 Топаз",
                type = EquipmentType.CURRENT_TRANSFORMER,
                parameters = listOf(
                    InspectionParameter("Уровень масла ф.А", "", ""),
                    InspectionParameter("Уровень масла ф.В", "", ""),
                    InspectionParameter("Уровень масла ф.С", "", ""),
                )
            ),
            //ОВ-220
            Equipment(
                id = "ОВ-220",
                name = "ОВ-220",
                type = EquipmentType.CIRCUIT_BREAKER,
                parameters = listOf(
                    InspectionParameter("Уровень продувки ф.А", "", "0.5-1"),
                    InspectionParameter("Уровень продувки ф.В", "", "0.5-1"),
                    InspectionParameter("Уровень продувки ф.С", "", "0.5-1"),
                )
            ),
            Equipment(
                id = "ТТ-220 ОВ",
                name = "ТТ-220 ОВ",
                type = EquipmentType.CURRENT_TRANSFORMER,
                parameters = listOf(
                    InspectionParameter("Уровень масла ф.А", "", ""),
                    InspectionParameter("Уровень масла ф.В", "", ""),
                    InspectionParameter("Уровень масла ф.С", "", ""),
                )
            ),
            //В-220 2АТГ
            Equipment(
                id = "В-220 2АТГ",
                name = "В-220 2АТГ",
                type = EquipmentType.CIRCUIT_BREAKER,
                parameters = listOf(
                    InspectionParameter("Уровень продувки ф.А", "", "0.5-1"),
                    InspectionParameter("Уровень продувки ф.В", "", "0.5-1"),
                    InspectionParameter("Уровень продувки ф.С", "", "0.5-1"),
                )
            ),
            Equipment(
                id = "ТТ-220 2АТГ",
                name = "ТТ-220 2АТГ",
                type = EquipmentType.CURRENT_TRANSFORMER,
                parameters = listOf(
                    InspectionParameter("Уровень масла ф.А", "", ""),
                    InspectionParameter("Уровень масла ф.В", "", ""),
                    InspectionParameter("Уровень масла ф.С", "", ""),
                )
            ),
            //ШСВ-220
            Equipment(
                id = "ШСВ-220",
                name = "ШСВ-220",
                type = EquipmentType.CIRCUIT_BREAKER,
                parameters = listOf(
                    InspectionParameter("Уровень продувки ф.А", "", "0.5-1"),
                    InspectionParameter("Уровень продувки ф.В", "", "0.5-1"),
                    InspectionParameter("Уровень продувки ф.С", "", "0.5-1"),
                )
            ),
            Equipment(
                id = "ТТ-220 ШСВ",
                name = "ТТ-220 ШСВ",
                type = EquipmentType.CURRENT_TRANSFORMER,
                parameters = listOf(
                    InspectionParameter("Уровень масла ф.А", "", ""),
                    InspectionParameter("Уровень масла ф.В", "", ""),
                    InspectionParameter("Уровень масла ф.С", "", ""),
                )
            ),
            //В-220 3АТГ
            Equipment(
                id = "В-220 3АТГ",
                name = "В-220 3АТГ",
                type = EquipmentType.CIRCUIT_BREAKER,
                parameters = listOf(
                    InspectionParameter("Уровень продувки ф.А", "", "0.5-1"),
                    InspectionParameter("Уровень продувки ф.В", "", "0.5-1"),
                    InspectionParameter("Уровень продувки ф.С", "", "0.5-1"),
                )
            ),
            Equipment(
                id = "ТТ-220 3АТГ",
                name = "ТТ-220 3АТГ",
                type = EquipmentType.CURRENT_TRANSFORMER,
                parameters = listOf(
                    InspectionParameter("Уровень масла ф.А", "", ""),
                    InspectionParameter("Уровень масла ф.В", "", ""),
                    InspectionParameter("Уровень масла ф.С", "", ""),
                )
            ),
            //Орбита
            Equipment(
                id = "В-220 Орбита",
                name = "В-220 Орбита",
                type = EquipmentType.CIRCUIT_BREAKER,
                parameters = listOf(
                    InspectionParameter("Уровень продувки ф.А", "", "0.5-1"),
                    InspectionParameter("Уровень продувки ф.В", "", "0.5-1"),
                    InspectionParameter("Уровень продувки ф.С", "", "0.5-1"),
                )
            ),
            Equipment(
                id = "ТТ-220 Орбита",
                name = "ТТ-220 Орбита",
                type = EquipmentType.CURRENT_TRANSFORMER,
                parameters = listOf(
                    InspectionParameter("Уровень масла ф.А", "", ""),
                    InspectionParameter("Уровень масла ф.В", "", ""),
                    InspectionParameter("Уровень масла ф.С", "", ""),
                )
            ),
            //Факел
            Equipment(
                id = "В-220 Факел",
                name = "В-220 Факел",
                type = EquipmentType.CIRCUIT_BREAKER,
                parameters = listOf(
                    InspectionParameter("Уровень продувки ф.А", "", "0.5-1"),
                    InspectionParameter("Уровень продувки ф.В", "", "0.5-1"),
                    InspectionParameter("Уровень продувки ф.С", "", "0.5-1"),
                )
            ),
            Equipment(
                id = "ТТ-220 Факел",
                name = "ТТ-220 Факел",
                type = EquipmentType.CURRENT_TRANSFORMER,
                parameters = listOf(
                    InspectionParameter("Уровень масла ф.А", "", ""),
                    InspectionParameter("Уровень масла ф.В", "", ""),
                    InspectionParameter("Уровень масла ф.С", "", ""),
                )
            ),
            //Комета-2
            Equipment(
                id = "В-220 Комета-2",
                name = "В-220 Комета-2",
                type = EquipmentType.CIRCUIT_BREAKER,
                parameters = listOf(
                    InspectionParameter("Уровень продувки ф.А", "", "0.5-1"),
                    InspectionParameter("Уровень продувки ф.В", "", "0.5-1"),
                    InspectionParameter("Уровень продувки ф.С", "", "0.5-1"),
                )
            ),
            Equipment(
                id = "ТТ-220 Комета-2",
                name = "ТТ-220 Комета-2",
                type = EquipmentType.CURRENT_TRANSFORMER,
                parameters = listOf(
                    InspectionParameter("Уровень масла ф.А", "", ""),
                    InspectionParameter("Уровень масла ф.В", "", ""),
                    InspectionParameter("Уровень масла ф.С", "", ""),
                )
            ),
            //Комета-1
            Equipment(
                id = "В-220 Комета-1",
                name = "В-220 Комета-1",
                type = EquipmentType.CIRCUIT_BREAKER,
                parameters = listOf(
                    InspectionParameter("Уровень продувки ф.А", "", "0.5-1"),
                    InspectionParameter("Уровень продувки ф.В", "", "0.5-1"),
                    InspectionParameter("Уровень продувки ф.С", "", "0.5-1"),
                )
            ),
            Equipment(
                id = "ТТ-220 Комета-1",
                name = "ТТ-220 Комета-1",
                type = EquipmentType.CURRENT_TRANSFORMER,
                parameters = listOf(
                    InspectionParameter("Уровень масла ф.А", "", ""),
                    InspectionParameter("Уровень масла ф.В", "", ""),
                    InspectionParameter("Уровень масла ф.С", "", ""),
                )
            ),
            //Трансформаторы напряжения
            Equipment(
                id = "1ТН-220",
                name = "1ТН-220",
                type = EquipmentType.VOLTAGE_TRANSFORMER,
                parameters = listOf(
                    InspectionParameter("Уровень масла ф.А нижний каскад", "", ""),
                    InspectionParameter("Уровень масла ф.А верхний каскад", "", ""),
                    InspectionParameter("Уровень масла ф.В нижний каскад", "", ""),
                    InspectionParameter("Уровень масла ф.В верхний каскад", "", ""),
                    InspectionParameter("Уровень масла ф.С нижний каскад", "", ""),
                    InspectionParameter("Уровень масла ф.С верхний каскад", "", ""),
                )
            ),
            Equipment(
                id = "2ТН-220",
                name = "2ТН-220",
                type = EquipmentType.VOLTAGE_TRANSFORMER,
                parameters = listOf(
                    InspectionParameter("Уровень масла ф.А нижний каскад", "", ""),
                    InspectionParameter("Уровень масла ф.А верхний каскад", "", ""),
                    InspectionParameter("Уровень масла ф.В нижний каскад", "", ""),
                    InspectionParameter("Уровень масла ф.В верхний каскад", "", ""),
                    InspectionParameter("Уровень масла ф.С нижний каскад", "", ""),
                    InspectionParameter("Уровень масла ф.С верхний каскад", "", ""),
                )
            ),
            Equipment(
                id = "ТН-220 ОСШ",
                name = "ТН-220 ОСШ",
                type = EquipmentType.VOLTAGE_TRANSFORMER,
                parameters = listOf(
                    InspectionParameter("Уровень масла ф.В нижний каскад", "", ""),
                    InspectionParameter("Уровень масла ф.В верхний каскад", "", ""),
                )
            ),

        )
    )

    val oru35 = Oru(
        voltage = "35",
        name = "ОРУ-35",
        equipments = listOf(
            // Трансформаторы собственных нужд
            Equipment(
                id = "2ТСН",
                name = "2ТСН",
                type = EquipmentType.POWER_TRANSFORMER,
                parameters = listOf(
                    InspectionParameter("Уровень масла РУМ", "°C", "-40 до +60"),
                    InspectionParameter("Температура ТС", "°C", "<85")
                )
            ),
            Equipment(
                id = "3ТСН",
                name = "3ТСН",
                type = EquipmentType.POWER_TRANSFORMER,
                parameters = listOf(
                    InspectionParameter("Уровень масла РУМ", "°C", "-40 до +60"),
                    InspectionParameter("Температура ТС", "°C", "<85")
                )
            ),
            Equipment(
                id = "4ТСН",
                name = "4ТСН",
                type = EquipmentType.POWER_TRANSFORMER,
                parameters = listOf(
                    InspectionParameter("Уровень масла РУМ", "°C", "-40 до +60"),
                    InspectionParameter("Температура ТС", "°C", "<85")
                )
            ),
            // Трансформаторы тока
            Equipment(
                id = "TT-35 2ТСН",
                name = "TT-35 2ТСН",
                type = EquipmentType.CURRENT_TRANSFORMER,
                parameters = listOf(
                    InspectionParameter("Уровень масла ф.А", " ", "от 0,3 до 0,9"),
                    InspectionParameter("Уровень масла ф.В", " ", "от 0,3 до 0,9"),
                    InspectionParameter("Уровень масла ф.С", " ", "от 0,3 до 0,9"),
                )
            ),
            Equipment(
                id = "TT-35 3ТСН",
                name = "TT-35 3ТСН",
                type = EquipmentType.CURRENT_TRANSFORMER,
                parameters = listOf(
                    InspectionParameter("Уровень масла ф.А", " ", "от 0,3 до 0,9"),
                    InspectionParameter("Уровень масла ф.В", " ", "от 0,3 до 0,9"),
                    InspectionParameter("Уровень масла ф.С", " ", "от 0,3 до 0,9"),
                )
            ),
            // Выключатели
            Equipment(
                id = "В-35 2ТСН",
                name = "В-35 2ТСН",
                type = EquipmentType.CIRCUIT_BREAKER,
                parameters = listOf(
                    InspectionParameter("Уровень продувки ф.А", " ", "от 0,5 до 1"),
                    InspectionParameter("Уровень продувки ф.В", " ", "от 0,5 до 1"),
                    InspectionParameter("Уровень продувки ф.С", " ", "от 0,5 до 1"),
                )
            ),
            Equipment(
                id = "В-35 3ТСН",
                name = "В-35 3ТСН",
                type = EquipmentType.CIRCUIT_BREAKER,
                parameters = listOf(
                    InspectionParameter("Уровень продувки ф.А", " ", "от 0,5 до 1"),
                    InspectionParameter("Уровень продувки ф.В", " ", "от 0,5 до 1"),
                    InspectionParameter("Уровень продувки ф.С", " ", "от 0,5 до 1"),
                )
            ),
            //Трансформаторы напряжения
            Equipment(
                id = "ТН-35 2АТГ",
                name = "ТН-35 2АТГ",
                type = EquipmentType.VOLTAGE_TRANSFORMER,
                parameters = listOf(
                    InspectionParameter("Уровень масла", " ", "от -60 до 40"),
                )
            ),
            Equipment(
                id = "ТН-35 3АТГ",
                name = "ТН-35 3АТГ",
                type = EquipmentType.VOLTAGE_TRANSFORMER,
                parameters = listOf(
                    InspectionParameter("Уровень масла", " ", "от -60 до 40"),
                )
            ),

        )
    )

    val allOru = listOf(oru500, oru220, oru35)

    fun getEquipmentGrouped(oru: Oru): Map<EquipmentType, List<Equipment>> {
        return when (oru.voltage) {
            "220" -> getEquipmentGrouped220(oru)
            else -> oru.equipments.groupBy { it.type }
        }
    }

    private fun getEquipmentGrouped220(oru: Oru): Map<EquipmentType, List<Equipment>> {
        val result = mutableMapOf<EquipmentType, MutableList<Equipment>>()
        val equipmentList = oru.equipments

        // Создаем мапы для быстрого поиска
        val breakers = equipmentList.filter { it.type == EquipmentType.CIRCUIT_BREAKER }
        val transformers = equipmentList.filter { it.type == EquipmentType.CURRENT_TRANSFORMER }

        // Функция для поиска соответствующего ТТ
        fun findMatchingTT(breaker: Equipment): Equipment? {
            val breakerName = breaker.name
            return transformers.find { tt ->
                when {
                    // Стандартные пары: "В-220 Мирная" -> "ТТ-220 Мирная"
                    breakerName.startsWith("В-220 ") && tt.name == "ТТ-220 ${breakerName.removePrefix("В-220 ")}" -> true

                    // Специальные случаи:
                    breakerName == "ОВ-220" && tt.name == "ТТ-220 ОВ" -> true
                    breakerName == "ШСВ-220" && tt.name == "ТТ-220 ШСВ" -> true

                    // Общий случай: если имя выключателя содержится в имени ТТ
                    tt.name.contains(breakerName) -> true
                    else -> false
                }
            }
        }

        // Обрабатываем все выключатели
        breakers.forEach { breaker ->
            val matchingTT = findMatchingTT(breaker)

            if (matchingTT != null) {
                // Добавляем пару выключатель + ТТ
                result.getOrPut(EquipmentType.CIRCUIT_BREAKER) { mutableListOf() }.apply {
                    add(breaker)
                    add(matchingTT)
                }
            } else {
                // Добавляем выключатель без пары
                result.getOrPut(EquipmentType.CIRCUIT_BREAKER) { mutableListOf() }.add(breaker)
            }
        }

        // Добавляем ТТ, которые не были добавлены в пары
        transformers.forEach { tt ->
            val isAlreadyAdded = result[EquipmentType.CIRCUIT_BREAKER]?.any { it.id == tt.id } == true
            if (!isAlreadyAdded) {
                result.getOrPut(EquipmentType.CURRENT_TRANSFORMER) { mutableListOf() }.add(tt)
            }
        }

        // Добавляем все остальное оборудование (ТН и т.д.)
        equipmentList.forEach { equipment ->
            if (equipment.type != EquipmentType.CIRCUIT_BREAKER &&
                equipment.type != EquipmentType.CURRENT_TRANSFORMER) {
                result.getOrPut(equipment.type) { mutableListOf() }.add(equipment)
            }
        }

        return result
    }
}