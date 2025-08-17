package org.example

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
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

@Composable
fun EquipmentSelectionScreen(
    oru: Oru,
    onEquipmentSelected: (Equipment) -> Unit,
    onBack: () -> Unit
) {
    Column(Modifier.fillMaxSize().padding(16.dp)) {
        Text("ОРУ: ${oru.name}", style = MaterialTheme.typography.h5)
        Spacer(Modifier.height(8.dp))
        oru.equipments.forEach { equipment ->
            Button(
                onClick = { onEquipmentSelected(equipment) },
                modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)
            ) {
                Text(equipment.name)
            }
        }
        Spacer(Modifier.weight(1f))
        Button(onClick = onBack, modifier = Modifier.fillMaxWidth()) {
            Text("Назад")
        }
    }
}

@Composable
fun InspectionEntryScreen(
    equipment: Equipment,
    onSave: (Map<String, String>, String) -> Unit,
    onBack: () -> Unit
) {
    var parameters by remember { mutableStateOf<Map<String, String>>(emptyMap()) }
    var comments by remember { mutableStateOf("") }

    Column(Modifier.fillMaxSize().padding(16.dp)) {
        Text("Оборудование: ${equipment.name}", style = MaterialTheme.typography.h5)
        Spacer(Modifier.height(16.dp))

        equipment.parameters.forEach { param ->
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(vertical = 4.dp)
            ) {
                Text("${param.name} (${param.normalValue}):", modifier = Modifier.weight(1f))
                OutlinedTextField(
                    value = parameters[param.name] ?: "",
                    onValueChange = { parameters = parameters + (param.name to it) },
                    modifier = Modifier.weight(1f)
                )
            }
        }

        OutlinedTextField(
            value = comments,
            onValueChange = { comments = it },
            label = { Text("Комментарии") },
            modifier = Modifier.fillMaxWidth()
        )

        Row(
            Modifier.fillMaxWidth().padding(top = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Button(onClick = onBack) { Text("Назад") }
            Button(onClick = { onSave(parameters, comments) }) { Text("Сохранить") }
        }
    }
}

@Composable
fun EmptyScreen() {
    Box(Modifier.fillMaxSize())
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
fun OruInspectionScreen(
    oru: Oru,
    onBack: () -> Unit
) {
    var inspectionData by remember { mutableStateOf<Map<String, String>>(emptyMap()) }
    var comments by remember { mutableStateOf("") }

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        // Шапка (остаётся всегда видимой)
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Назад")
            }
            Text("ОРУ-${oru.voltage} кВ", style = MaterialTheme.typography.h5)
        }

        // Основной контент с прокруткой
        Box(
            modifier = Modifier
                .weight(1f)  // Занимает всё доступное пространство
                .verticalScroll(rememberScrollState())  // Включаем прокрутку
        ) {
            Column(
                modifier = Modifier.padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                oru.equipments.forEach { equipment ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        elevation = 4.dp
                    ) {
                        Column(modifier = Modifier.padding(8.dp)) {
                            Text(equipment.name, style = MaterialTheme.typography.h6)

                            equipment.parameters.forEach { param ->
                                OutlinedTextField(
                                    value = inspectionData["${equipment.id}_${param.name}"] ?: "",
                                    onValueChange = {
                                        inspectionData = inspectionData + ("${equipment.id}_${param.name}" to it)
                                    },
                                    label = { Text(param.name) },
                                    placeholder = { Text("Норма: ${param.normalValue}") },
                                    modifier = Modifier.fillMaxWidth()
                                )
                            }
                        }
                    }
                }

                // Комментарии
                OutlinedTextField(
                    value = comments,
                    onValueChange = { comments = it },
                    label = { Text("Общие комментарии") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }

        // Кнопка сохранения (остаётся внизу)
        Button(
            onClick = { /* Сохранить */ },
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text("Завершить осмотр")
        }
    }
}