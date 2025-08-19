package org.example

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

enum class EquipmentType {
    POWER_TRANSFORMER,
    CIRCUIT_BREAKER,
    CURRENT_TRANSFORMER,
    VOLTAGE_TRANSFORMER,
    AUXILIARY_TRANSFORMER
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

    val allOru = listOf(oru500, oru35)
}