package org.example

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application

@Composable
@Preview
fun App() {
    var currentScreen by remember { mutableStateOf(AppScreen.ORU_SELECTION) }
    var selectedOru by remember { mutableStateOf<Oru?>(null) }
    var selectedEquipment by remember { mutableStateOf<Equipment?>(null) }
    var inspectionResults by remember { mutableStateOf<List<InspectionResult>>(emptyList()) }

    MaterialTheme {
        when (currentScreen) {
            AppScreen.ORU_SELECTION -> OruSelectionScreen(
                oruList = SubstationData.allOru,
                onOruSelected = {
                    selectedOru = it
                    currentScreen = AppScreen.EQUIPMENT_SELECTION
                }
            )

            AppScreen.EQUIPMENT_SELECTION -> selectedOru?.let { oru ->
                EquipmentSelectionScreen(
                    oru = oru,
                    onEquipmentSelected = {
                        selectedEquipment = it
                        currentScreen = AppScreen.INSPECTION_ENTRY
                    },
                    onBack = { currentScreen = AppScreen.ORU_SELECTION }
                )
            } ?: run {
                currentScreen = AppScreen.ORU_SELECTION
                Box {}
            }

            AppScreen.INSPECTION_ENTRY -> selectedEquipment?.let { equipment ->
                InspectionEntryScreen(
                    equipment = equipment,
                    onSave = { parameters, comments ->
                        inspectionResults = inspectionResults + InspectionResult(
                            oru = selectedOru!!,
                            equipment = equipment,
                            parameters = parameters,
                            comments = comments
                        )
                        currentScreen = AppScreen.EQUIPMENT_SELECTION
                    },
                    onBack = { currentScreen = AppScreen.EQUIPMENT_SELECTION }
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