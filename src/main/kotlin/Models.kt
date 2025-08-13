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
    EQUIPMENT_SELECTION,
    INSPECTION_ENTRY
}

object SubstationData {
    val oru500 = Oru(
        voltage = "500",
        name = "ОРУ-500",
        equipments = listOf(
            Equipment(
                id = "2АТГ",
                name = "Автотрансформатор 2АТГ",
                type = EquipmentType.POWER_TRANSFORMER,
                parameters = listOf(
                    InspectionParameter("Уровень масла", "см", "30±2"),
                    InspectionParameter("Температура", "°C", "<85")
                )
            )
        )
    )

    val allOru = listOf(oru500)
}