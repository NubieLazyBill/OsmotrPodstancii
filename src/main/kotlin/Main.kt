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

    MaterialTheme {
        when (currentScreen) {
            AppScreen.ORU_SELECTION -> OruSelectionScreen(
                oruList = SubstationData.allOru,
                onOruSelected = {
                    selectedOru = it
                    currentScreen = AppScreen.EQUIPMENT_SELECTION
                }
            )
            AppScreen.EQUIPMENT_SELECTION -> EquipmentSelectionScreen(
                oru = selectedOru!!,
                onEquipmentSelected = {
                    selectedEquipment = it
                    currentScreen = AppScreen.INSPECTION_ENTRY
                },
                onBack = { currentScreen = AppScreen.ORU_SELECTION }
            )
            AppScreen.INSPECTION_ENTRY -> SimpleButtonScreen(
                onBack = { currentScreen = AppScreen.EQUIPMENT_SELECTION }
            )
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
fun EquipmentSelectionScreen(oru: Oru, onEquipmentSelected: (Equipment) -> Unit, onBack: () -> Unit) {
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
fun SimpleButtonScreen(onBack: () -> Unit) {
    Column(Modifier.fillMaxSize(), verticalArrangement = Arrangement.Center) {
        Button(
            onClick = onBack,
            modifier = Modifier.padding(16.dp).align(Alignment.CenterHorizontally)
        ) {
            Text("Назад к выбору оборудования")
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