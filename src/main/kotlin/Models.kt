package org.example

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

enum class EquipmentType {
    POWER_TRANSFORMER,
    CIRCUIT_BREAKER,
    CURRENT_TRANSFORMER,
    VOLTAGE_TRANSFORMER,
    BUILDING,
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
    val atg_reactor = Oru(
        voltage = "500/200/35",
        name = "АТГ, Р-500 2С",
        equipments = listOf(
            // АТГ
            Equipment(
                id = "2АТГ ф.С",
                name = "2АТГ ф.С",
                type = EquipmentType.POWER_TRANSFORMER,
                parameters = listOf(
                    InspectionParameter("Уровень масла Бак", "°C", "<70"),
                    InspectionParameter("Уровень масла РПН", "°C", "<70"),
                    InspectionParameter("Температура ТС1", "°C", "<70"),
                    InspectionParameter("Температура ТС2", "°C", "<70"),
                    InspectionParameter("Давление во вводе 500кВ", "кгс/см²", ""),
                    InspectionParameter("Давление во вводе 220кВ", "кгс/см²", ""),
                    InspectionParameter("Давление манометра маслонасоса 1гр", "кгс/см²", ""),
                    InspectionParameter("Давление манометра маслонасоса 2гр", "кгс/см²", ""),
                    InspectionParameter("Давление манометра маслонасоса 3гр", "кгс/см²", ""),
                    InspectionParameter("Давление манометра маслонасоса 4гр", "кгс/см²", ""),
                )
            )
            ,Equipment(
                id = "2АТГ ф.В",
                name = "2АТГ ф.В",
                type = EquipmentType.POWER_TRANSFORMER,
                parameters = listOf(
                    InspectionParameter("Уровень масла Бак", "°C", "<70"),
                    InspectionParameter("Уровень масла РПН", "°C", "<70"),
                    InspectionParameter("Температура ТС1", "°C", "<70"),
                    InspectionParameter("Температура ТС2", "°C", "<70"),
                    InspectionParameter("Давление во вводе 500кВ", "кгс/см²", ""),
                    InspectionParameter("Давление во вводе 220кВ", "кгс/см²", ""),
                    InspectionParameter("Давление манометра маслонасоса 1гр", "кгс/см²", ""),
                    InspectionParameter("Давление манометра маслонасоса 2гр", "кгс/см²", ""),
                    InspectionParameter("Давление манометра маслонасоса 3гр", "кгс/см²", ""),
                    InspectionParameter("Давление манометра маслонасоса 4гр", "кгс/см²", ""),
                )
            ),
            Equipment(
                id = "2АТГ ф.А",
                name = "2АТГ ф.А",
                type = EquipmentType.POWER_TRANSFORMER,
                parameters = listOf(
                    InspectionParameter("Уровень масла Бак", "°C", "<70"),
                    InspectionParameter("Уровень масла РПН", "°C", "<70"),
                    InspectionParameter("Температура ТС1", "°C", "<70"),
                    InspectionParameter("Температура ТС2", "°C", "<70"),
                    InspectionParameter("Давление во вводе 500кВ", "кгс/см²", ""),
                    InspectionParameter("Давление во вводе 220кВ", "кгс/см²", ""),
                    InspectionParameter("Давление манометра маслонасоса 1гр", "кгс/см²", ""),
                    InspectionParameter("Давление манометра маслонасоса 2гр", "кгс/см²", ""),
                    InspectionParameter("Давление манометра маслонасоса 3гр", "кгс/см²", ""),
                    InspectionParameter("Давление манометра маслонасоса 4гр", "кгс/см²", ""),
                )
            ),
            Equipment(
                id = "АТГ-резерв",
                name = "АТГ-резерв",
                type = EquipmentType.POWER_TRANSFORMER,
                parameters = listOf(
                    InspectionParameter("Уровень масла Бак", "°C", "<70"),
                    InspectionParameter("Уровень масла РПН", "°C", "<70"),
                    InspectionParameter("Температура ТС1", "°C", "<70"),
                    InspectionParameter("Температура ТС2", "°C", "<70"),
                    InspectionParameter("Давление во вводе 500кВ", "кгс/см²", ""),
                    InspectionParameter("Давление во вводе 220кВ", "кгс/см²", ""),
                    InspectionParameter("Давление манометра маслонасоса 1гр", "кгс/см²", ""),
                    InspectionParameter("Давление манометра маслонасоса 2гр", "кгс/см²", ""),
                    InspectionParameter("Давление манометра маслонасоса 3гр", "кгс/см²", ""),
                    InspectionParameter("Давление манометра маслонасоса 4гр", "кгс/см²", ""),
                    InspectionParameter("Пломбы на вент. задвижках", "", "установлены"),
                    InspectionParameter("Пломбы на задвижках в откр. полож.", "", "установлены"),
                )
            ),
            Equipment(
                id = "3АТГ ф.С",
                name = "3АТГ ф.С",
                type = EquipmentType.POWER_TRANSFORMER,
                parameters = listOf(
                    InspectionParameter("Уровень масла Бак", "°C", "<70"),
                    InspectionParameter("Уровень масла РПН", "°C", "<70"),
                    InspectionParameter("Температура ТС1", "°C", "<70"),
                    InspectionParameter("Температура ТС2", "°C", "<70"),
                    InspectionParameter("Давление во вводе 500кВ", "кгс/см²", ""),
                    InspectionParameter("Давление во вводе 220кВ", "кгс/см²", ""),
                    InspectionParameter("Давление манометра маслонасоса 1гр", "кгс/см²", ""),
                    InspectionParameter("Давление манометра маслонасоса 2гр", "кгс/см²", ""),
                    InspectionParameter("Давление манометра маслонасоса 3гр", "кгс/см²", ""),
                    InspectionParameter("Давление манометра маслонасоса 4гр", "кгс/см²", ""),
                )
            )
            ,Equipment(
                id = "3АТГ ф.В",
                name = "3АТГ ф.В",
                type = EquipmentType.POWER_TRANSFORMER,
                parameters = listOf(
                    InspectionParameter("Уровень масла Бак", "°C", "<70"),
                    InspectionParameter("Уровень масла РПН", "°C", "<70"),
                    InspectionParameter("Температура ТС1", "°C", "<70"),
                    InspectionParameter("Температура ТС2", "°C", "<70"),
                    InspectionParameter("Давление во вводе 500кВ", "кгс/см²", ""),
                    InspectionParameter("Давление во вводе 220кВ", "кгс/см²", ""),
                    InspectionParameter("Давление манометра маслонасоса 1гр", "кгс/см²", ""),
                    InspectionParameter("Давление манометра маслонасоса 2гр", "кгс/см²", ""),
                    InspectionParameter("Давление манометра маслонасоса 3гр", "кгс/см²", ""),
                    InspectionParameter("Давление манометра маслонасоса 4гр", "кгс/см²", ""),
                )
            ),
            Equipment(
                id = "3АТГ ф.А",
                name = "3АТГ ф.А",
                type = EquipmentType.POWER_TRANSFORMER,
                parameters = listOf(
                    InspectionParameter("Уровень масла Бак", "°C", "<70"),
                    InspectionParameter("Уровень масла РПН", "°C", "<70"),
                    InspectionParameter("Температура ТС1", "°C", "<70"),
                    InspectionParameter("Температура ТС2", "°C", "<70"),
                    InspectionParameter("Давление во вводе 500кВ", "кгс/см²", ""),
                    InspectionParameter("Давление во вводе 220кВ", "кгс/см²", ""),
                    InspectionParameter("Давление манометра маслонасоса 1гр", "кгс/см²", ""),
                    InspectionParameter("Давление манометра маслонасоса 2гр", "кгс/см²", ""),
                    InspectionParameter("Давление манометра маслонасоса 3гр", "кгс/см²", ""),
                    InspectionParameter("Давление манометра маслонасоса 4гр", "кгс/см²", ""),
                )
            ),
            //Реактор
            Equipment(
                id = "Р-500 2С ф.С",
                name = "Р-500 2С ф.С",
                type = EquipmentType.POWER_TRANSFORMER,
                parameters = listOf(
                    InspectionParameter("Уровень масла Бак", "°C", "<70"),
                    InspectionParameter("Температура ТС", "°C", "<70"),
                    InspectionParameter("Давление во вводе 500кВ", "кгс/см²", ""),
                    InspectionParameter("Давление манометра маслонасоса 1гр", "кгс/см²", ""),
                    InspectionParameter("Давление манометра маслонасоса 2гр", "кгс/см²", ""),
                    InspectionParameter("Давление манометра маслонасоса 3гр", "кгс/см²", ""),
                )
            )
            ,Equipment(
                id = "Р-500 2С ф.В",
                name = "Р-500 2С ф.В",
                type = EquipmentType.POWER_TRANSFORMER,
                parameters = listOf(
                    InspectionParameter("Уровень масла Бак", "°C", "<70"),
                    InspectionParameter("Температура ТС", "°C", "<70"),
                    InspectionParameter("Давление во вводе 500кВ", "кгс/см²", ""),
                    InspectionParameter("Давление манометра маслонасоса 1гр", "кгс/см²", ""),
                    InspectionParameter("Давление манометра маслонасоса 2гр", "кгс/см²", ""),
                    InspectionParameter("Давление манометра маслонасоса 3гр", "кгс/см²", ""),
                )
            ),
            Equipment(
                id = "Р-500 2С ф.А",
                name = "Р-500 2С ф.А",
                type = EquipmentType.POWER_TRANSFORMER,
                parameters = listOf(
                    InspectionParameter("Уровень масла Бак", "°C", "<70"),
                    InspectionParameter("Температура ТС", "°C", "<70"),
                    InspectionParameter("Давление во вводе 500кВ", "кгс/см²", ""),
                    InspectionParameter("Давление манометра маслонасоса 1гр", "кгс/см²", ""),
                    InspectionParameter("Давление манометра маслонасоса 2гр", "кгс/см²", ""),
                    InspectionParameter("Давление манометра маслонасоса 3гр", "кгс/см²", ""),
                )
            ),
            //Реактор резерв
            Equipment(
                id = "Р-500 резерв",
                name = "Р-500 резерв",
                type = EquipmentType.POWER_TRANSFORMER,
                parameters = listOf(
                    InspectionParameter("Пломбы на вент. задвижках", "", "установлены"),
                    InspectionParameter("Пломбы на задвижках в откр. полож.", "", "установлены")
                )
            ),

        )
    )
    val oru500 = Oru(
        voltage = "500",
        name = "ОРУ-500",
        listOf(
            //В-500 Р-500 2С
            Equipment(
                id = "В-500 Р-500 2С",
                name = "В-500 Р-500 2С",
                type = EquipmentType.CIRCUIT_BREAKER,
                parameters = listOf(
                    InspectionParameter("Уровень продувки ф.А I эл.", "", "0,5 - 1"),
                    InspectionParameter("Уровень продувки ф.А II эл.", "", "0,5 - 1"),
                    InspectionParameter("Уровень продувки ф.В I эл.", "", "0,5 - 1"),
                    InspectionParameter("Уровень продувки ф.В II эл.", "", "0,5 - 1"),
                    InspectionParameter("Уровень продувки ф.С I эл.", "", "0,5 - 1"),
                    InspectionParameter("Уровень продувки ф.С II эл.", "", "0,5 - 1"),
                )
            ),
            //яч. В-500 ВШТ-31
            Equipment(
                id = "В-500 ВШТ-31",
                name = "В-500 ВШТ-31",
                type = EquipmentType.CIRCUIT_BREAKER,
                parameters = listOf(
                    InspectionParameter("Давление элегаза ф.А", "", ""),
                    InspectionParameter("Давление элегаза ф.В", "", ""),
                    InspectionParameter("Давление элегаза ф.С", "", ""),
                )
            ),
            Equipment(
                id = "ТТ-500 ВШТ-31",
                name = "ТТ-500 ВШТ-31",
                type = EquipmentType.CURRENT_TRANSFORMER,
                parameters = listOf(
                    InspectionParameter("Уровень масла ф.А", "", ""),
                    InspectionParameter("Уровень масла ф.В", "", ""),
                    InspectionParameter("Уровень масла ф.С", "", ""),
                )
            ),
            //яч. В-500 ВЛТ-30
            Equipment(
                id = "В-500 ВЛТ-30",
                name = "В-500 ВЛТ-30",
                type = EquipmentType.CIRCUIT_BREAKER,
                parameters = listOf(
                    InspectionParameter("Давление элегаза ф.А", "", ""),
                    InspectionParameter("Давление элегаза ф.В", "", ""),
                    InspectionParameter("Давление элегаза ф.С", "", ""),
                )
            ),
            Equipment(
                id = "ТТ-500 ВЛТ-30",
                name = "ТТ-500 ВЛТ-30",
                type = EquipmentType.CURRENT_TRANSFORMER,
                parameters = listOf(
                    InspectionParameter("Уровень масла ф.А", "", ""),
                    InspectionParameter("Уровень масла ф.В", "", ""),
                    InspectionParameter("Уровень масла ф.С", "", ""),
                )
            ),
            //яч. В-500 ВШЛ-32
            Equipment(
                id = "В-500 ВШЛ-32",
                name = "В-500 ВШЛ-32",
                type = EquipmentType.CIRCUIT_BREAKER,
                parameters = listOf(
                    InspectionParameter("Уровень продувки ф.А I эл.", "", "0,5 - 1"),
                    InspectionParameter("Уровень продувки ф.А II эл.", "", "0,5 - 1"),
                    InspectionParameter("Уровень продувки ф.В I эл.", "", "0,5 - 1"),
                    InspectionParameter("Уровень продувки ф.В II эл.", "", "0,5 - 1"),
                    InspectionParameter("Уровень продувки ф.С I эл.", "", "0,5 - 1"),
                    InspectionParameter("Уровень продувки ф.С II эл.", "", "0,5 - 1"),
                )
            ),
            Equipment(
                id = "ТТ-500 ВШЛ-32",
                name = "ТТ-500 ВШЛ-32",
                type = EquipmentType.CURRENT_TRANSFORMER,
                parameters = listOf(
                    InspectionParameter("Уровень масла ф.А", "", ""),
                    InspectionParameter("Уровень масла ф.В", "", ""),
                    InspectionParameter("Уровень масла ф.С", "", ""),
                )
            ),
            //яч. В-500 ВШЛ-21
            Equipment(
                id = "В-500 ВШЛ-21",
                name = "В-500 ВШЛ-21",
                type = EquipmentType.CIRCUIT_BREAKER,
                parameters = listOf(
                    InspectionParameter("Уровень продувки ф.А I эл.", "", "0,5 - 1"),
                    InspectionParameter("Уровень продувки ф.А II эл.", "", "0,5 - 1"),
                    InspectionParameter("Уровень продувки ф.В I эл.", "", "0,5 - 1"),
                    InspectionParameter("Уровень продувки ф.В II эл.", "", "0,5 - 1"),
                    InspectionParameter("Уровень продувки ф.С I эл.", "", "0,5 - 1"),
                    InspectionParameter("Уровень продувки ф.С II эл.", "", "0,5 - 1"),
                )
            ),
            Equipment(
                id = "ТТ-500 ВШЛ-21",
                name = "ТТ-500 ВШЛ-21",
                type = EquipmentType.CURRENT_TRANSFORMER,
                parameters = listOf(
                    InspectionParameter("Уровень масла ф.А", "", ""),
                    InspectionParameter("Уровень масла ф.В", "", ""),
                    InspectionParameter("Уровень масла ф.С", "", ""),
                )
            ),
            //яч. В-500 ВШТ-22
            Equipment(
                id = "В-500 ВШТ-22",
                name = "В-500 ВШТ-22",
                type = EquipmentType.CIRCUIT_BREAKER,
                parameters = listOf(
                    InspectionParameter("Уровень продувки ф.А I эл.", "", "0,5 - 1"),
                    InspectionParameter("Уровень продувки ф.А II эл.", "", "0,5 - 1"),
                    InspectionParameter("Уровень продувки ф.В I эл.", "", "0,5 - 1"),
                    InspectionParameter("Уровень продувки ф.В II эл.", "", "0,5 - 1"),
                    InspectionParameter("Уровень продувки ф.С I эл.", "", "0,5 - 1"),
                    InspectionParameter("Уровень продувки ф.С II эл.", "", "0,5 - 1"),
                )
            ),
            Equipment(
                id = "ТТ-500 ВШТ-22",
                name = "ТТ-500 ВШТ-22",
                type = EquipmentType.CURRENT_TRANSFORMER,
                parameters = listOf(
                    InspectionParameter("Уровень масла ф.А", "", ""),
                    InspectionParameter("Уровень масла ф.В", "", ""),
                    InspectionParameter("Уровень масла ф.С", "", ""),
                )
            ),
            //яч. В-500 ВЛТ-20
            Equipment(
                id = "В-500 ВЛТ-20",
                name = "В-500 ВЛТ-20",
                type = EquipmentType.CIRCUIT_BREAKER,
                parameters = listOf(
                    InspectionParameter("Уровень продувки ф.А I эл.", "", "0,5 - 1"),
                    InspectionParameter("Уровень продувки ф.А II эл.", "", "0,5 - 1"),
                    InspectionParameter("Уровень продувки ф.В I эл.", "", "0,5 - 1"),
                    InspectionParameter("Уровень продувки ф.В II эл.", "", "0,5 - 1"),
                    InspectionParameter("Уровень продувки ф.С I эл.", "", "0,5 - 1"),
                    InspectionParameter("Уровень продувки ф.С II эл.", "", "0,5 - 1"),
                )
            ),
            Equipment(
                id = "ТТ-500 ВЛТ-20",
                name = "ТТ-500 ВЛТ-20",
                type = EquipmentType.CURRENT_TRANSFORMER,
                parameters = listOf(
                    InspectionParameter("Уровень масла ф.А", "", ""),
                    InspectionParameter("Уровень масла ф.В", "", ""),
                    InspectionParameter("Уровень масла ф.С", "", ""),
                )
            ),
            //яч. В-500 ВШТ-11
            Equipment(
                id = "В-500 ВШТ-11",
                name = "В-500 ВШТ-11",
                type = EquipmentType.CIRCUIT_BREAKER,
                parameters = listOf(
                    InspectionParameter("Уровень продувки ф.А I эл.", "", "0,5 - 1"),
                    InspectionParameter("Уровень продувки ф.А II эл.", "", "0,5 - 1"),
                    InspectionParameter("Уровень продувки ф.В I эл.", "", "0,5 - 1"),
                    InspectionParameter("Уровень продувки ф.В II эл.", "", "0,5 - 1"),
                    InspectionParameter("Уровень продувки ф.С I эл.", "", "0,5 - 1"),
                    InspectionParameter("Уровень продувки ф.С II эл.", "", "0,5 - 1"),
                )
            ),
            Equipment(
                id = "ТТ-500 ВШТ-11",
                name = "ТТ-500 ВШТ-11",
                type = EquipmentType.CURRENT_TRANSFORMER,
                parameters = listOf(
                    InspectionParameter("Уровень масла ф.А", "", ""),
                    InspectionParameter("Уровень масла ф.В", "", ""),
                    InspectionParameter("Уровень масла ф.С", "", ""),
                )
            ),
            //яч. В-500 ВШЛ-12
            Equipment(
                id = "В-500 ВШЛ-12",
                name = "В-500 ВШЛ-12",
                type = EquipmentType.CIRCUIT_BREAKER,
                parameters = listOf(
                    InspectionParameter("Уровень продувки ф.А I эл.", "", "0,5 - 1"),
                    InspectionParameter("Уровень продувки ф.А II эл.", "", "0,5 - 1"),
                    InspectionParameter("Уровень продувки ф.В I эл.", "", "0,5 - 1"),
                    InspectionParameter("Уровень продувки ф.В II эл.", "", "0,5 - 1"),
                    InspectionParameter("Уровень продувки ф.С I эл.", "", "0,5 - 1"),
                    InspectionParameter("Уровень продувки ф.С II эл.", "", "0,5 - 1"),
                )
            ),
            Equipment(
                id = "ТТ-500 ВШЛ-12",
                name = "ТТ-500 ВШЛ-12",
                type = EquipmentType.CURRENT_TRANSFORMER,
                parameters = listOf(
                    InspectionParameter("Уровень масла ф.А", "", ""),
                    InspectionParameter("Уровень масла ф.В", "", ""),
                    InspectionParameter("Уровень масла ф.С", "", ""),
                )
            ),
            //ТТ-500 Трачуковская
            Equipment(
                id = "ТТ-500 Трачуковская",
                name = "ТТ-500 Трачуковская",
                type = EquipmentType.CURRENT_TRANSFORMER,
                parameters = listOf(
                    InspectionParameter("Уровень масла ф.А", "", ""),
                    InspectionParameter("Уровень масла ф.В", "", ""),
                    InspectionParameter("Уровень масла ф.С", "", ""),
                )
            ),
            //Трансформаторы напряжения
            Equipment(
                id = "1ТН-500 Трачуковская",
                name = "1ТН-500 Трачуковская",
                type = EquipmentType.VOLTAGE_TRANSFORMER,
                parameters = listOf(
                    InspectionParameter("Уровень масла ф.А", "", ""),
                    InspectionParameter("Уровень масла ф.В", "", ""),
                    InspectionParameter("Уровень масла ф.С", "", ""),
                )
            ),
            Equipment(
                id = "2ТН-500 Трачуковская",
                name = "2ТН-500 Трачуковская",
                type = EquipmentType.VOLTAGE_TRANSFORMER,
                parameters = listOf(
                    InspectionParameter("Уровень масла ф.А", "", ""),
                    InspectionParameter("Уровень масла ф.В", "", ""),
                    InspectionParameter("Уровень масла ф.С", "", ""),
                )
            ),
            Equipment(
                id = "ТН-500 Белозёрная",
                name = "ТН-500 Белозёрная",
                type = EquipmentType.VOLTAGE_TRANSFORMER,
                parameters = listOf(
                    InspectionParameter("Уровень масла ф.А", "", ""),
                    InspectionParameter("Уровень масла ф.В", "", ""),
                    InspectionParameter("Уровень масла ф.С", "", ""),
                )
            ),
            Equipment(
                id = "1ТН-500",
                name = "1ТН-500",
                type = EquipmentType.VOLTAGE_TRANSFORMER,
                parameters = listOf(
                    InspectionParameter("Уровень масла ф.А 1 каскад", "", ""),
                    InspectionParameter("Уровень масла ф.А 2 каскад", "", ""),
                    InspectionParameter("Уровень масла ф.А 3 каскад", "", ""),
                    InspectionParameter("Уровень масла ф.А 4 каскад", "", ""),
                    InspectionParameter("Уровень масла ф.В 1 каскад", "", ""),
                    InspectionParameter("Уровень масла ф.В 2 каскад", "", ""),
                    InspectionParameter("Уровень масла ф.В 3 каскад", "", ""),
                    InspectionParameter("Уровень масла ф.В 4 каскад", "", ""),
                    InspectionParameter("Уровень масла ф.С 1 каскад", "", ""),
                    InspectionParameter("Уровень масла ф.С 2 каскад", "", ""),
                    InspectionParameter("Уровень масла ф.С 3 каскад", "", ""),
                    InspectionParameter("Уровень масла ф.С 4 каскад", "", ""),
                )
            ),
            Equipment(
                id = "2ТН-500",
                name = "2ТН-500",
                type = EquipmentType.VOLTAGE_TRANSFORMER,
                parameters = listOf(
                    InspectionParameter("Уровень масла ф.А 1 каскад", "", ""),
                    InspectionParameter("Уровень масла ф.А 2 каскад", "", ""),
                    InspectionParameter("Уровень масла ф.А 3 каскад", "", ""),
                    InspectionParameter("Уровень масла ф.А 4 каскад", "", ""),
                    InspectionParameter("Уровень масла ф.В 1 каскад", "", ""),
                    InspectionParameter("Уровень масла ф.В 2 каскад", "", ""),
                    InspectionParameter("Уровень масла ф.В 3 каскад", "", ""),
                    InspectionParameter("Уровень масла ф.В 4 каскад", "", ""),
                    InspectionParameter("Уровень масла ф.С 1 каскад", "", ""),
                    InspectionParameter("Уровень масла ф.С 2 каскад", "", ""),
                    InspectionParameter("Уровень масла ф.С 3 каскад", "", ""),
                    InspectionParameter("Уровень масла ф.С 4 каскад", "", ""),
                )
            ),
            Equipment(
                id = "ТН-500 СГРЭС-1",
                name = "ТН-500 СГРЭС-1",
                type = EquipmentType.VOLTAGE_TRANSFORMER,
                parameters = listOf(
                    InspectionParameter("Уровень масла ф.А 1 каскад", "", ""),
                    InspectionParameter("Уровень масла ф.А 2 каскад", "", ""),
                    InspectionParameter("Уровень масла ф.А 3 каскад", "", ""),
                    InspectionParameter("Уровень масла ф.А 4 каскад", "", ""),
                    InspectionParameter("Уровень масла ф.В 1 каскад", "", ""),
                    InspectionParameter("Уровень масла ф.В 2 каскад", "", ""),
                    InspectionParameter("Уровень масла ф.В 3 каскад", "", ""),
                    InspectionParameter("Уровень масла ф.В 4 каскад", "", ""),
                    InspectionParameter("Уровень масла ф.С 1 каскад", "", ""),
                    InspectionParameter("Уровень масла ф.С 2 каскад", "", ""),
                    InspectionParameter("Уровень масла ф.С 3 каскад", "", ""),
                    InspectionParameter("Уровень масла ф.С 4 каскад", "", ""),
                )
            ),
            //Проверка трубопроводов воздушной сети
            Equipment(
                id = "air_pipes_500",
                name = "Трубопроводы воздушной сети ОРУ-500",
                type = EquipmentType.BUILDING,
                parameters = listOf(
                    InspectionParameter("Состояние трубопроводов", "", "исправно"),
                    InspectionParameter("Отсутствие утечек", "", "норма"),
                    InspectionParameter("Давление в системе", "кгс/см²", "норма"),
                    InspectionParameter("Примечание", "", "")
                )
            )
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
            //проверка трубопроводов
            Equipment(
                id = "air_pipes_220",
                name = "Трубопроводы воздушной сети ОРУ-220",
                type = EquipmentType.BUILDING,
                parameters = listOf(
                    InspectionParameter("Состояние трубопроводов", "", "исправно"),
                    InspectionParameter("Отсутствие утечек", "", "норма"),
                    InspectionParameter("Давление в системе", "кгс/см²", "норма"),
                    InspectionParameter("Примечание", "", "")
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
            //Проверка трубопроводов
            Equipment(
                id = "air_pipes_35",
                name = "Трубопроводы воздушной сети ОРУ-35",
                type = EquipmentType.BUILDING,
                parameters = listOf(
                    InspectionParameter("Состояние трубопроводов", "", "исправно"),
                    InspectionParameter("Отсутствие утечек", "", "норма"),
                    InspectionParameter("Давление в системе", "кгс/см²", "норма"),
                    InspectionParameter("Примечание", "", "")
                )
            ),

        )
    )

    val buildingsOru = Oru(
        voltage = "0",
        name = "Здания и сооружения",
        equipments = listOf(
            Equipment(
                id = "compressor1",
                name = "Компрессорная №1",
                type = EquipmentType.BUILDING,
                parameters = listOf(
                    InspectionParameter("Работоспособность обогрева", "", "исправно"),
                    InspectionParameter("Температура", "°C", "+5...+30"),
                    InspectionParameter("Примечание", "", "")
                )
            ),
            Equipment(
                id = "balloon1",
                name = "Баллонная №1",
                type = EquipmentType.BUILDING,
                parameters = listOf(
                    InspectionParameter("Работоспособность обогрева", "", "исправно"),
                    InspectionParameter("Температура", "°C", "+5...+30"),
                    InspectionParameter("Примечание", "", "")
                )
            ),
            Equipment(
                id = "compressor2",
                name = "Компрессорная №2",
                type = EquipmentType.BUILDING,
                parameters = listOf(
                    InspectionParameter("Работоспособность обогрева", "", "исправно"),
                    InspectionParameter("Температура", "°C", "+5...+30"),
                    InspectionParameter("Примечание", "", "")
                )
            ),
            Equipment(
                id = "balloon2",
                name = "Баллонная №2",
                type = EquipmentType.BUILDING,
                parameters = listOf(
                    InspectionParameter("Работоспособность обогрева", "", "исправно"),
                    InspectionParameter("Температура", "°C", "+5...+30"),
                    InspectionParameter("Примечание", "", "")
                )
            ),
            Equipment(
                id = "kpz_opu",
                name = "КПЗ ОПУ",
                type = EquipmentType.BUILDING,
                parameters = listOf(
                    InspectionParameter("Работоспособность обогрева", "", "исправно"),
                    InspectionParameter("Температура", "°C", "+18...+24"),
                    InspectionParameter("Примечание", "", "")
                )
            ),
            Equipment(
                id = "kpz2",
                name = "КПЗ-2",
                type = EquipmentType.BUILDING,
                parameters = listOf(
                    InspectionParameter("Работоспособность обогрева", "", "исправно"),
                    InspectionParameter("Температура", "°C", "+18...+24"),
                    InspectionParameter("Примечание", "", "")
                )
            ),
            Equipment(
                id = "fire_pump",
                name = "Насосная пожаротушения",
                type = EquipmentType.BUILDING,
                parameters = listOf(
                    InspectionParameter("Работоспособность обогрева", "", "исправно"),
                    InspectionParameter("Температура", "°C", "+5...+30"),
                    InspectionParameter("Примечание", "", "")
                )
            ),
            Equipment(
                id = "vv_workshop",
                name = "Мастерская по ремонту ВВ",
                type = EquipmentType.BUILDING,
                parameters = listOf(
                    InspectionParameter("Температура", "°C", "+5...+30"),
                    InspectionParameter("Примечание", "", "")
                )
            ),
            Equipment(
                id = "art_well",
                name = "Артскважина",
                type = EquipmentType.BUILDING,
                parameters = listOf(
                    InspectionParameter("Температура", "°C", "+5...+30"),
                    InspectionParameter("Примечание", "", "")
                )
            ),
            Equipment(
                id = "art_well_building",
                name = "Здание артезианской скважины",
                type = EquipmentType.BUILDING,
                parameters = listOf(
                    InspectionParameter("Температура", "°C", "+5...+30"),
                    InspectionParameter("Примечание", "", "")
                )
            ),
            Equipment(
                id = "ab_room",
                name = "Помещение 1 (2) АБ",
                type = EquipmentType.BUILDING,
                parameters = listOf(
                    InspectionParameter("Температура", "°C", "+15...+25"),
                    InspectionParameter("Примечание", "", "")
                )
            ),
            Equipment(
                id = "basement_rooms",
                name = "Помещение п/этажа №1,2,3",
                type = EquipmentType.BUILDING,
                parameters = listOf(
                    InspectionParameter("Температура", "°C", "+5...+30"),
                    InspectionParameter("Примечание", "", "")
                )
            )
        )
    )


    val allOru = listOf(atg_reactor, oru500, oru220, oru35, buildingsOru)

    fun getEquipmentGrouped(oru: Oru): Map<EquipmentType, List<Equipment>> {
        return when (oru.voltage) {
            "220" -> getEquipmentGrouped220(oru)
            "500" -> oru.equipments.groupBy { it.type } // Для ОРУ-500 используем стандартную группировку
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