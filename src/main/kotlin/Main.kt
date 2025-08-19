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
                }
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
        }
    }
}

@Composable
fun OruSelectionScreen(oruList: List<Oru>, onOruSelected: (Oru) -> Unit) {
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
    val groupedEquipment = remember { SubstationData.getEquipmentGrouped(oru) }
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
                "ОРУ-${oru.voltage} кВ",
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

                    // Обычная сетка вместо LazyVerticalGrid
                    val rows = (equipments.size + 2) / 3 // Вычисляем количество строк
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
                                                onParamChange = { key, value ->
                                                    inspectionData = inspectionData + (key to value)
                                                }
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
        }

        Button(
            onClick = { /* Сохранить */ },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Завершить осмотр")
        }
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