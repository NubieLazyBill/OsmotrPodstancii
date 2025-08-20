package org.example

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application

@Composable
fun App() {
    var currentScreen by remember { mutableStateOf(AppScreen.ORU_SELECTION) }
    var selectedOru by remember { mutableStateOf<Oru?>(null) }

    MaterialTheme {
        when (currentScreen) {
            AppScreen.ORU_SELECTION -> OruSelectionScreen(
                oruList = SubstationData.allOru,
                onOruSelected = {
                    selectedOru = it
                    currentScreen = AppScreen.ORU_INSPECTION
                },
                onAbout = { currentScreen = AppScreen.ABOUT }
            )

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
        }
    }
}


@Composable
fun OruSelectionScreen(oruList: List<Oru>, onOruSelected: (Oru) -> Unit, onAbout: () -> Unit) {
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

        // Кнопка "О программе"
        Spacer(modifier = Modifier.height(32.dp))
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
        onCloseRequest = ::exitApplication,
        title = "Осмотр ПС"
    ) {
        App()
    }
}

@Composable
fun OruInspectionScreen(oru: Oru, onBack: () -> Unit) {
    var inspectionData by remember { mutableStateOf<Map<String, String>>(emptyMap()) }
    val scrollState = rememberScrollState()

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
            onClick = { /* Сохранить */ },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Завершить осмотр")
        }
    }
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

// Новый экран для зданий
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
            "ОсмотрПС",
            style = MaterialTheme.typography.h4,
            modifier = Modifier.padding(vertical = 16.dp)
        )

        Text(
            "Программа для проведения осмотра подстанции",
            style = MaterialTheme.typography.body1,
            modifier = Modifier.padding(bottom = 24.dp)
        )

        Text(
            "Посвящается",
            style = MaterialTheme.typography.h6,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        Text(
            "Матвееву Олегу Александровичу\n" +
                    "Мастеру-масленщику МЭС Западной Сибири\n" +
                    "Человеку, стоявшему у истоков подстанции\n" +
                    "1948-2020",
            style = MaterialTheme.typography.body2,
            modifier = Modifier.padding(vertical = 16.dp)
        )

        Text(
            "\"Проще энергетика отучить на программиста, \nчем программисту изучить всю энергетику\" \n" +
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