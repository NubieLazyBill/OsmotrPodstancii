package org.example

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.clickable
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.FileDownload
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.File
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.UUID

@Composable
fun App() {
    var currentScreen by remember { mutableStateOf(AppScreen.ORU_SELECTION) }
    var selectedOru by remember { mutableStateOf<Oru?>(null) }
    var selectedSession by remember { mutableStateOf<InspectionSession?>(null) }

    MaterialTheme {
        when (currentScreen) {
            AppScreen.ORU_SELECTION -> OruSelectionScreen(
                oruList = SubstationData.allOru,
                onOruSelected = {
                    selectedOru = it
                    currentScreen = AppScreen.ORU_INSPECTION
                },
                onAbout = { currentScreen = AppScreen.ABOUT },
                onHistory = { currentScreen = AppScreen.HISTORY },
                onExport = { currentScreen = AppScreen.EXPORT } // ← Добавьте эту строку
            )

            AppScreen.HISTORY -> HistoryScreen(
                onBack = { currentScreen = AppScreen.ORU_SELECTION },
                onViewDetails = { session ->
                    selectedSession = session
                    currentScreen = AppScreen.INSPECTION_DETAILS
                }
            )

            AppScreen.INSPECTION_DETAILS -> selectedSession?.let { session ->
                InspectionDetailsScreen(
                    session = session,
                    onBack = { currentScreen = AppScreen.HISTORY }
                )
            } ?: run {
                currentScreen = AppScreen.HISTORY
                Box {}
            }

            AppScreen.ORU_INSPECTION -> selectedOru?.let { oru ->
                OruInspectionScreen(
                    oru = oru,
                    onBack = { currentScreen = AppScreen.ORU_SELECTION }
                )
            } ?: run {
                currentScreen = AppScreen.ORU_SELECTION
                Box {}
            }

            AppScreen.ABOUT -> AboutScreen(
                onBack = { currentScreen = AppScreen.ORU_SELECTION }
            )

            AppScreen.EXPORT -> ExportScreen( // ← Добавьте этот case
                onBack = { currentScreen = AppScreen.ORU_SELECTION }
            )
        }
    }
}


@Composable
fun OruSelectionScreen(
    oruList: List<Oru>,
    onOruSelected: (Oru) -> Unit,
    onAbout: () -> Unit,
    onHistory: () -> Unit,
    onExport: () -> Unit // ← Добавьте этот параметр
) {
    Column(Modifier.fillMaxSize().padding(16.dp)) {
        Text("Выберите ОРУ", style = MaterialTheme.typography.h4)
        Spacer(Modifier.height(16.dp))

        oruList.forEach { oru ->
            Button(
                onClick = { onOruSelected(oru) },
                modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)
            ) {
                Text(oru.name)
            }
        }

        // Кнопка "История осмотров"
        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = onHistory,
            modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
            colors = ButtonDefaults.buttonColors(backgroundColor = MaterialTheme.colors.secondaryVariant)
        ) {
            Text("История осмотров")
        }

        // Кнопка "Экспорт данных" ← Добавьте эту кнопку
        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = onExport,
            modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
            colors = ButtonDefaults.buttonColors(backgroundColor = MaterialTheme.colors.primaryVariant)
        ) {
            Text("Экспорт данных")
        }

        // Кнопка "О программе"
        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = onAbout,
            modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
            colors = ButtonDefaults.buttonColors(backgroundColor = MaterialTheme.colors.secondary)
        ) {
            Text("О программе")
        }
    }
}

fun main() = application {

    Window(
        onCloseRequest = {
            // Сохраняем при закрытии на всякий случай
            InspectionRepository.saveToFile()
            exitApplication()
        },
        title = "Осмотр ПС"
    ) {
        App()
    }
}

@Composable
fun OruInspectionScreen(oru: Oru, onBack: () -> Unit) {
    var inspectionData by remember { mutableStateOf<Map<String, String>>(emptyMap()) }
    val scrollState = rememberScrollState()
    var showSaveDialog by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Шапка
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            IconButton(onClick = onBack) {
                Icon(Icons.Filled.ArrowBack, "Назад")
            }
            Text(
                if (oru.voltage == "0") "Здания и сооружения" else "ОРУ-${oru.voltage} кВ",
                style = MaterialTheme.typography.h6,
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Основной контент
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(scrollState)
            ) {
                // Для специальных ОРУ используем особую структуру
                when (oru.voltage) {
                    "500/200/35" -> AtgReactorInspectionLayout(
                        oru = oru,
                        inspectionData = inspectionData,
                        onParamChange = { key, value ->
                            inspectionData = inspectionData + (key to value)
                        }
                    )
                    "500" -> Oru500InspectionLayout(
                        oru = oru,
                        inspectionData = inspectionData,
                        onParamChange = { key, value ->
                            inspectionData = inspectionData + (key to value)
                        }
                    )
                    "0" -> BuildingsInspectionLayout( // Новый экран для зданий
                        oru = oru,
                        inspectionData = inspectionData,
                        onParamChange = { key, value ->
                            inspectionData = inspectionData + (key to value)
                        }
                    )
                    else -> {
                        val groupedEquipment = remember { SubstationData.getEquipmentGrouped(oru) }
                        StandardOruInspectionLayout(
                            groupedEquipment = groupedEquipment,
                            inspectionData = inspectionData,
                            onParamChange = { key, value ->
                                inspectionData = inspectionData + (key to value)
                            }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
            }
        }

        Button(
            onClick = { showSaveDialog = true },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Завершить осмотр")
        }
    }

    if (showSaveDialog) {
        SaveInspectionDialog(
            onConfirm = {
                saveInspectionResults(oru, inspectionData)
                showSaveDialog = false
                onBack()
            },
            onDismiss = { showSaveDialog = false }
        )
    }
}

@Composable
fun SaveInspectionDialog(onConfirm: () -> Unit, onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Завершение осмотра") },
        text = { Text("Сохранить результаты осмотра?") },
        confirmButton = {
            Button(onClick = onConfirm) {
                Text("Сохранить")
            }
        },
        dismissButton = {
            Button(onClick = onDismiss) {
                Text("Отмена")
            }
        }
    )
}

private fun saveInspectionResults(oru: Oru, data: Map<String, String>) {
    val results = mutableListOf<InspectionResult>()

    oru.equipments.forEach { equipment ->
        val equipmentParams = mutableMapOf<String, String>()
        equipment.parameters.forEach { param ->
            val key = "${equipment.id}_${param.name}"
            equipmentParams[param.name] = data[key] ?: ""
        }
        results.add(InspectionResult(equipment, equipmentParams))
    }

    val session = InspectionSession(oru = oru, results = results, isCompleted = true)
    InspectionRepository.saveSession(session)
}

// Новый компактный компонент для зданий
@Composable
fun BuildingCompactCard(
    equipment: Equipment,
    inspectionData: Map<String, String>,
    onParamChange: (String, String) -> Unit
) {
    Card(
        elevation = 4.dp,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(8.dp)
        ) {
            Text(
                equipment.name,
                fontSize = 14.sp,
                maxLines = 2,
                modifier = Modifier.padding(bottom = 4.dp)
            )

            equipment.parameters.forEach { param ->
                OutlinedTextField(
                    value = inspectionData["${equipment.id}_${param.name}"] ?: "",
                    onValueChange = { onParamChange("${equipment.id}_${param.name}", it) },
                    label = { Text(param.name, fontSize = 10.sp) },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
            }
        }
    }
}

//Экран для зданий
@Composable
fun BuildingsInspectionLayout(
    oru: Oru,
    inspectionData: Map<String, String>,
    onParamChange: (String, String) -> Unit
) {
    Column {
        Text(
            text = "Здания и сооружения подстанции",
            style = MaterialTheme.typography.h6,
            modifier = Modifier.padding(vertical = 16.dp)
        )

        // Отображаем здания в сетке 2 колонки для компактности
        val rows = (oru.equipments.size + 1) / 2
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            repeat(rows) { rowIndex ->
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    for (colIndex in 0..1) {
                        val itemIndex = rowIndex * 2 + colIndex
                        if (itemIndex < oru.equipments.size) {
                            val equipment = oru.equipments[itemIndex]
                            Box(modifier = Modifier.weight(1f)) {
                                BuildingCompactCard(
                                    equipment = equipment,
                                    inspectionData = inspectionData,
                                    onParamChange = onParamChange
                                )
                            }
                        } else {
                            Spacer(modifier = Modifier.weight(1f))
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun Oru500InspectionLayout(
    oru: Oru,
    inspectionData: Map<String, String>,
    onParamChange: (String, String) -> Unit
) {
    // Создаем список пар: выключатель + соответствующий ТТ
    val breakerPairs = mutableListOf<Pair<Equipment?, Equipment?>>()

    // Создаем мапы для быстрого поиска
    val breakers = oru.equipments.filter { it.type == EquipmentType.CIRCUIT_BREAKER }
    val transformers = oru.equipments.filter { it.type == EquipmentType.CURRENT_TRANSFORMER }
    val otherEquipment = oru.equipments.filter {
        it.type != EquipmentType.CIRCUIT_BREAKER &&
                it.type != EquipmentType.CURRENT_TRANSFORMER
    }

    // Функция для поиска соответствующего ТТ
    fun findMatchingTT(breaker: Equipment): Equipment? {
        val breakerName = breaker.name
        return transformers.find { tt ->
            when {
                // Специальный случай: В-500 Р-500 2С не имеет ТТ
                breakerName == "В-500 Р-500 2С" -> false
                // Стандартные пары: "В-500 ВШТ-31" -> "ТТ-500 ВШТ-31"
                breakerName.startsWith("В-500 ") && tt.name == "ТТ-500 ${breakerName.removePrefix("В-500 ")}" -> true
                // Общий случай: если имя выключателя содержится в имени ТТ
                tt.name.contains(breakerName.removePrefix("В-500 ")) -> true
                else -> false
            }
        }
    }

    // Создаем пары выключатель-ТТ
    breakers.forEach { breaker ->
        if (breaker.name == "В-500 Р-500 2С") {
            // Для В-500 Р-500 2С оставляем место под ТТ пустым
            breakerPairs.add(Pair(breaker, null))
        } else {
            val matchingTT = findMatchingTT(breaker)
            breakerPairs.add(Pair(breaker, matchingTT))
        }
    }

    Column {
        // Заголовок для пар выключатель-ТТ
        Text(
            text = "Пары выключатель + ТТ",
            style = MaterialTheme.typography.h6,
            modifier = Modifier.padding(vertical = 16.dp)
        )

        // Отображаем пары выключатель-ТТ: слева выключатель, справа ТТ
        breakerPairs.forEach { (breaker, transformer) ->
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                // Выключатель (левая половина)
                Box(modifier = Modifier.weight(1f)) {
                    breaker?.let {
                        EquipmentCompactCard(
                            equipment = it,
                            inspectionData = inspectionData,
                            onParamChange = onParamChange
                        )
                    }
                }

                // Трансформатор тока (правая половина)
                Box(modifier = Modifier.weight(1f)) {
                    transformer?.let {
                        EquipmentCompactCard(
                            equipment = it,
                            inspectionData = inspectionData,
                            onParamChange = onParamChange
                        )
                    } ?: run {
                        // Пустая карточка для отсутствующего ТТ
                        Card(
                            elevation = 2.dp,
                            modifier = Modifier.fillMaxWidth(),
                            backgroundColor = MaterialTheme.colors.background.copy(alpha = 0.5f)
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(100.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    "ТТ отсутствует",
                                    color = MaterialTheme.colors.onSurface.copy(alpha = 0.5f)
                                )
                            }
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
        }

        // Остальное оборудование
        if (otherEquipment.isNotEmpty()) {
            Text(
                text = "Трансформаторы напряжения",
                style = MaterialTheme.typography.h6,
                modifier = Modifier.padding(vertical = 16.dp)
            )

            // Отображаем в стандартной сетке 3 колонки
            val rows = (otherEquipment.size + 2) / 3
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                repeat(rows) { rowIndex ->
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        for (colIndex in 0..2) {
                            val itemIndex = rowIndex * 3 + colIndex
                            if (itemIndex < otherEquipment.size) {
                                val equipment = otherEquipment[itemIndex]
                                Box(modifier = Modifier.weight(1f)) {
                                    EquipmentCompactCard(
                                        equipment = equipment,
                                        inspectionData = inspectionData,
                                        onParamChange = onParamChange
                                    )
                                }
                            } else {
                                Spacer(modifier = Modifier.weight(1f))
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun BreakerTTPairCard(
    breaker: Equipment?,
    transformer: Equipment?,
    inspectionData: Map<String, String>,
    onParamChange: (String, String) -> Unit
) {
    Card(
        elevation = 4.dp,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(8.dp)
        ) {
            // Выключатель
            breaker?.let { breakerEq ->
                Text(
                    breakerEq.name,
                    fontSize = 14.sp,
                    maxLines = 2,
                    modifier = Modifier.padding(bottom = 4.dp)
                )

                breakerEq.parameters.forEach { param ->
                    OutlinedTextField(
                        value = inspectionData["${breakerEq.id}_${param.name}"] ?: "",
                        onValueChange = { value -> onParamChange("${breakerEq.id}_${param.name}", value) },
                        label = { Text(param.name, fontSize = 10.sp) },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))
            }

            // Разделитель
            Divider(modifier = Modifier.padding(vertical = 4.dp))

            // Трансформатор тока
            transformer?.let { transformerEq ->
                Text(
                    transformerEq.name,
                    fontSize = 14.sp,
                    maxLines = 2,
                    modifier = Modifier.padding(bottom = 4.dp)
                )

                transformerEq.parameters.forEach { param ->
                    OutlinedTextField(
                        value = inspectionData["${transformerEq.id}_${param.name}"] ?: "",
                        onValueChange = { value -> onParamChange("${transformerEq.id}_${param.name}", value) },
                        label = { Text(param.name, fontSize = 10.sp) },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                }
            } ?: run {
                // Пустое место для отсутствующего ТТ
                Text(
                    "Трансформатор тока отсутствует",
                    fontSize = 12.sp,
                    color = MaterialTheme.colors.onSurface.copy(alpha = 0.5f),
                    modifier = Modifier.padding(vertical = 16.dp)
                )
            }
        }
    }
}

@Composable
fun StandardOruInspectionLayout(
    groupedEquipment: Map<EquipmentType, List<Equipment>>,
    inspectionData: Map<String, String>,
    onParamChange: (String, String) -> Unit
) {
    groupedEquipment.forEach { (type, equipments) ->
        // Заголовок группы
        Text(
            text = when (type) {
                EquipmentType.POWER_TRANSFORMER -> "Трансформаторы"
                EquipmentType.CIRCUIT_BREAKER -> "Выключатели"
                EquipmentType.CURRENT_TRANSFORMER -> "Трансформаторы тока"
                EquipmentType.VOLTAGE_TRANSFORMER -> "Трансформаторы напряжения"
                else -> type.toString()
            },
            style = MaterialTheme.typography.subtitle1,
            modifier = Modifier.padding(vertical = 8.dp)
        )

        // Стандартная сетка 3 колонки
        val rows = (equipments.size + 2) / 3
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            repeat(rows) { rowIndex ->
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    for (colIndex in 0..2) {
                        val itemIndex = rowIndex * 3 + colIndex
                        if (itemIndex < equipments.size) {
                            val equipment = equipments[itemIndex]
                            Box(modifier = Modifier.weight(1f)) {
                                EquipmentCompactCard(
                                    equipment = equipment,
                                    inspectionData = inspectionData,
                                    onParamChange = onParamChange
                                )
                            }
                        } else {
                            Spacer(modifier = Modifier.weight(1f))
                        }
                    }
                }
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
    }
}


// EquipmentCompactCard.kt
@Composable
fun EquipmentCompactCard(
    equipment: Equipment,
    inspectionData: Map<String, String>,
    onParamChange: (String, String) -> Unit
) {
    Card(
        elevation = 4.dp,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(8.dp)
        ) {
            Text(
                equipment.name,
                fontSize = 14.sp,
                maxLines = 2,
                modifier = Modifier.padding(bottom = 4.dp)
            )

            equipment.parameters.forEach { param ->
                OutlinedTextField(
                    value = inspectionData["${equipment.id}_${param.name}"] ?: "",
                    onValueChange = { onParamChange("${equipment.id}_${param.name}", it) },
                    label = { Text(param.name, fontSize = 10.sp) },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
            }
        }
    }
}
@Composable
fun AtgReactorInspectionLayout(
    oru: Oru,
    inspectionData: Map<String, String>,
    onParamChange: (String, String) -> Unit
) {
    // Создаем map для быстрого доступа к оборудованию по id
    val equipmentMap = oru.equipments.associateBy { it.id }

    // Жестко задаем порядок оборудования
    val orderedEquipment = listOf(
        "2АТГ ф.С", "2АТГ ф.В", "2АТГ ф.А",  // 2АТГ в ряд
        "АТГ-резерв",                         // АТГ-резерв одиночно
        "3АТГ ф.С", "3АТГ ф.В", "3АТГ ф.А",  // 3АТГ в ряд
        "Р-500 2С ф.С", "Р-500 2С ф.В", "Р-500 2С ф.А",  // Реактор в ряд
        "Р-500 резерв"                        // Резервная фаза реактора (только пломбы)
    ).mapNotNull { equipmentMap[it] }

    Column {
        // 2АТГ
        Text(
            text = "Трансформаторы 2АТГ",
            style = MaterialTheme.typography.h6,
            modifier = Modifier.padding(vertical = 8.dp)
        )

        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            orderedEquipment
                .filter { it.name.startsWith("2АТГ") }
                .forEach { equipment ->
                    Box(modifier = Modifier.weight(1f)) {
                        EquipmentCompactCard(
                            equipment = equipment,
                            inspectionData = inspectionData,
                            onParamChange = onParamChange
                        )
                    }
                }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // АТГ-резерв (компактный, как другие АТГ)
        Text(
            text = "Резервный трансформатор",
            style = MaterialTheme.typography.h6,
            modifier = Modifier.padding(vertical = 8.dp)
        )

        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            orderedEquipment
                .find { it.name == "АТГ-резерв" }
                ?.let { equipment ->
                    Box(modifier = Modifier.weight(1f)) {
                        EquipmentCompactCard(
                            equipment = equipment,
                            inspectionData = inspectionData,
                            onParamChange = onParamChange
                        )
                    }
                }
            Spacer(modifier = Modifier.weight(1f))
            Spacer(modifier = Modifier.weight(1f))
        }

        Spacer(modifier = Modifier.height(16.dp))

        // 3АТГ
        Text(
            text = "Трансформаторы 3АТГ",
            style = MaterialTheme.typography.h6,
            modifier = Modifier.padding(vertical = 8.dp)
        )

        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            orderedEquipment
                .filter { it.name.startsWith("3АТГ") }
                .forEach { equipment ->
                    Box(modifier = Modifier.weight(1f)) {
                        EquipmentCompactCard(
                            equipment = equipment,
                            inspectionData = inspectionData,
                            onParamChange = onParamChange
                        )
                    }
                }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Реактор Р-500 2С
        Text(
            text = "Реактор Р-500 2С",
            style = MaterialTheme.typography.h6,
            modifier = Modifier.padding(vertical = 8.dp)
        )

        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            orderedEquipment
                .filter { it.name.startsWith("Р-500 2С ф") }
                .forEach { equipment ->
                    Box(modifier = Modifier.weight(1f)) {
                        EquipmentCompactCard(
                            equipment = equipment,
                            inspectionData = inspectionData,
                            onParamChange = onParamChange
                        )
                    }
                }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Резервная фаза реактора (только пломбы)
        Text(
            text = "Резервная фаза реактора",
            style = MaterialTheme.typography.h6,
            modifier = Modifier.padding(vertical = 8.dp)
        )

        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            orderedEquipment
                .find { it.name == "Р-500 резерв" }
                ?.let { equipment ->
                    Box(modifier = Modifier.weight(1f)) {
                        EquipmentCompactCard(
                            equipment = equipment,
                            inspectionData = inspectionData,
                            onParamChange = onParamChange
                        )
                    }
                }
            Spacer(modifier = Modifier.weight(1f))
            Spacer(modifier = Modifier.weight(1f))
        }
    }
}

@Composable
fun AboutScreen(onBack: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
            .verticalScroll(rememberScrollState())
    ) {
        IconButton(onClick = onBack) {
            Icon(Icons.Filled.ArrowBack, "Назад")
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            "Осмотр ПС 500кВ Кустовая",
            style = MaterialTheme.typography.h4,
            modifier = Modifier.padding(vertical = 16.dp)
        )

        Text(
            "Программа для проведения осмотра подстанции",
            style = MaterialTheme.typography.body1,
            modifier = Modifier.padding(bottom = 24.dp)
        )

        Text(
            "\"Проще энергетику научиться программировать, \nчем программиста переучить на энергетика\" \n" +
                    "                                                Матвеев О.А.",
            fontStyle = FontStyle.Italic,
            modifier = Modifier.padding(vertical = 24.dp)
        )

        Text(
            "Версия 1.0",
            style = MaterialTheme.typography.caption,
            modifier = Modifier.padding(top = 32.dp)
        )
    }
}

@Composable
fun HistoryScreen(
    onBack: () -> Unit,
    onViewDetails: (InspectionSession) -> Unit  // Добавим параметр
) {
    val sessions by remember { mutableStateOf(InspectionRepository.getSessions()) }

    Column(Modifier.fillMaxSize().padding(16.dp)) {
        IconButton(onClick = onBack) {
            Icon(Icons.Filled.ArrowBack, "Назад")
        }

        Text("История осмотров", style = MaterialTheme.typography.h4)

        LazyColumn {
            items(sessions) { session ->
                HistoryItem(session, onViewDetails)
            }
        }
    }
}

@Composable
fun HistoryItem(session: InspectionSession, onViewDetails: (InspectionSession) -> Unit) {
    Card(
        elevation = 4.dp,
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable { onViewDetails(session) }
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                session.oru.name,
                style = MaterialTheme.typography.h6
            )
            Text(
                "Дата: ${session.dateTimeString}", // ИЗМЕНИЛИ ЗДЕСЬ
                style = MaterialTheme.typography.body2
            )
            Text(
                "Оборудование: ${session.results.size}",
                style = MaterialTheme.typography.body2
            )
            if (session.isCompleted) {
                Text(
                    "Завершено",
                    color = MaterialTheme.colors.primary,
                    style = MaterialTheme.typography.caption
                )
            }
        }
    }
}

@Composable
fun InspectionDetailsScreen(session: InspectionSession, onBack: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        // Шапка
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onBack) {
                Icon(Icons.Filled.ArrowBack, "Назад")
            }
            Text(
                "Детали осмотра: ${session.oru.name}",
                style = MaterialTheme.typography.h6
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Информация о сессии
        Text(
            "Дата и время: ${session.dateTimeString}", // ИЗМЕНИЛИ ЗДЕСЬ
            style = MaterialTheme.typography.body1
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Результаты осмотра
        Text("Результаты осмотра:", style = MaterialTheme.typography.h6)

        session.results.forEach { result ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                elevation = 4.dp
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        result.equipment.name,
                        style = MaterialTheme.typography.subtitle1
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    result.parameters.forEach { (paramName, value) ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(paramName, fontWeight = FontWeight.Bold)
                            Text(value)
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                    }
                }
            }
        }
    }
}

@Composable
fun ExportScreen(onBack: () -> Unit) {
    var exportPath by remember { mutableStateOf("") }

    Column(Modifier.fillMaxSize().padding(16.dp)) {
        // Шапка
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onBack) {
                Icon(Icons.Filled.ArrowBack, "Назад")
            }
            Text("Экспорт данных", style = MaterialTheme.typography.h4)
        }

        Spacer(Modifier.height(24.dp))

        // Статистика
        val sessions = InspectionRepository.getSessions()
        Text("Всего осмотров: ${sessions.size}")
        Text("Всего записей: ${sessions.sumOf { it.results.size }}")

        Spacer(Modifier.height(24.dp))

        // Кнопки экспорта
        Button(
            onClick = {
                exportPath = exportToCSV()
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(Icons.Default.FileDownload, "Экспорт в CSV")
            Spacer(Modifier.width(8.dp))
            Text("Экспорт в CSV")
        }

        Spacer(Modifier.height(16.dp))

        // Информация о экспорте
        if (exportPath.isNotBlank()) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                backgroundColor = MaterialTheme.colors.primary.copy(alpha = 0.1f)
            ) {
                Column(Modifier.padding(16.dp)) {
                    Text("Экспорт завершен!", color = MaterialTheme.colors.primary)
                    Text("Файл: $exportPath", fontSize = 12.sp)
                    Text("Откройте файл в Excel или другом редакторе", fontSize = 12.sp)
                }
            }
        }
    }
}

fun exportToCSV(fileName: String = "осмотры_подстанции_${System.currentTimeMillis()}.csv"): String {
    try {
        val file = File(fileName)
        // Явно указываем кодировку UTF-8 с BOM для правильного отображения в Excel
        val writer = file.bufferedWriter(Charsets.UTF_8)

        // Добавляем BOM (Byte Order Mark) для UTF-8
        writer.write("\uFEFF")

        // Заголовок CSV
        writer.write("Дата;Время;ОРУ;Напряжение;Оборудование;Тип оборудования;Параметр;Значение;Норма;Статус;Комментарии")
        writer.newLine()

        InspectionRepository.getSessions().forEach { session ->
            session.results.forEach { result ->
                result.parameters.forEach { (paramName, value) ->
                    val normalValue = result.equipment.parameters
                        .find { it.name == paramName }
                        ?.normalValue ?: ""

                    // Определяем статус (норма/не норма)
                    val status = if (normalValue.isNotBlank() && value.isNotBlank()) {
                        when {
                            value.matches(Regex("норма|исправно|установлены")) -> "✅ Норма"
                            normalValue.contains("-") -> { // Диапазон значений
                                val range = normalValue.split("-")
                                if (range.size == 2) {
                                    val min = range[0].trim().replace(",", ".").toDoubleOrNull()
                                    val max = range[1].trim().replace(",", ".").toDoubleOrNull()
                                    val numValue = value.replace(",", ".").toDoubleOrNull()

                                    if (min != null && max != null && numValue != null) {
                                        if (numValue in min..max) "✅ Норма" else "⚠️ Не норма"
                                    } else {
                                        "⚪ Не проверено"
                                    }
                                } else {
                                    "⚪ Не проверено"
                                }
                            }
                            else -> if (value == normalValue) "✅ Норма" else "⚠️ Не норма"
                        }
                    } else {
                        "⚪ Не проверено"
                    }

                    val dateTimeParts = session.dateTimeString.split(" ")
                    val date = dateTimeParts.getOrElse(0) { "" }
                    val time = dateTimeParts.getOrElse(1) { "" }

                    writer.write(
                        "$date;$time;${session.oru.name};${session.oru.voltage};" +
                                "${result.equipment.name};${result.equipment.type};" +
                                "$paramName;$value;$normalValue;$status;${result.comments}"
                    )
                    writer.newLine()
                }
            }
        }

        writer.close()
        println("Данные экспортированы в: ${file.absolutePath}")
        return file.absolutePath

    } catch (e: Exception) {
        println("Ошибка экспорта: ${e.message}")
        throw e
    }
}