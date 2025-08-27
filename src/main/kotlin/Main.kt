package org.example

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.clickable
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.FileDownload
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.SwapHoriz
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
import androidx.compose.ui.graphics.Color
import org.example.exportSingleSessionToCSV
import org.example.exportToCSV
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.material.icons.filled.SwapHoriz


// Модель для уведомлений
data class Notification(
    val id: String = UUID.randomUUID().toString(),
    val message: String,
    val type: NotificationType = NotificationType.SUCCESS
)

enum class NotificationType {
    SUCCESS, ERROR, INFO
}

// Глобальное состояние для уведомлений
object NotificationManager {
    val notifications = mutableStateListOf<Notification>()

    fun showNotification(message: String, type: NotificationType = NotificationType.SUCCESS) {
        notifications.add(Notification(message = message, type = type))

        // Автоматическое скрытие через 5 секунд
        Thread {
            Thread.sleep(5000)
            notifications.removeAll { it.id == notifications.lastOrNull()?.id }
        }.start()
    }

    fun removeNotification(id: String) {
        notifications.removeAll { it.id == id }
    }
}

// Компонент для отображения уведомлений
@Composable
fun NotificationToast() {
    val notifications = NotificationManager.notifications

    if (notifications.isNotEmpty()) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            contentAlignment = Alignment.TopCenter
        ) {
            Column {
                notifications.forEach { notification ->
                    val backgroundColor = when (notification.type) {
                        NotificationType.SUCCESS -> Color.Green.copy(alpha = 0.9f)
                        NotificationType.ERROR -> Color.Red.copy(alpha = 0.9f)
                        NotificationType.INFO -> Color.Blue.copy(alpha = 0.9f)
                    }

                    Card(
                        backgroundColor = backgroundColor,
                        elevation = 8.dp,
                        modifier = Modifier
                            .fillMaxWidth(0.8f)
                            .padding(vertical = 4.dp)
                            .clickable {
                                NotificationManager.removeNotification(notification.id)
                            }
                    ) {
                        Text(
                            text = notification.message,
                            color = Color.White,
                            modifier = Modifier.padding(16.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun App() {
    var currentScreen by remember { mutableStateOf(AppScreen.ORU_SELECTION) }
    var selectedOru by remember { mutableStateOf<Oru?>(null) }
    var selectedSession by remember { mutableStateOf<InspectionSession?>(null) }
    var showInspectorSelection by remember { mutableStateOf(false) }

    // Отслеживаем текущего дежурного
    val currentInspector by produceState<Inspector?>(initialValue = null) {
        value = InspectorManager.getCurrentInspector()
    }

    MaterialTheme {
        Box(modifier = Modifier.fillMaxSize()) {
            when (currentScreen) {
                AppScreen.ORU_SELECTION -> OruSelectionScreen(
                    oruList = SubstationData.allOru,
                    onOruSelected = {
                        selectedOru = it
                        // Проверяем, выбран ли дежурный
                        if (InspectorManager.getCurrentInspector() == null) {
                            showInspectorSelection = true
                        } else {
                            currentScreen = AppScreen.ORU_INSPECTION
                        }
                    },
                    onAbout = { currentScreen = AppScreen.ABOUT },
                    onHistory = { currentScreen = AppScreen.HISTORY }
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
                AppScreen.INSPECTOR_SELECTION -> { /* Можно оставить пустым или добавить заглушку */ }
            }

            // Отображаем дежурного в верхнем правом углу (кроме экрана выбора дежурного)
            if (currentScreen != AppScreen.INSPECTOR_SELECTION && !showInspectorSelection) {
                Box(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(16.dp)
                ) {
                    InspectorBadge(
                        inspector = currentInspector,
                        onChangeClick = { showInspectorSelection = true }
                    )
                }
            }



            // Экран выбора дежурного (поверх основного контента)
            if (showInspectorSelection) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background.copy(alpha = 0.9f)
                ) {
                    InspectorSelectionScreen(
                        onInspectorSelected = { inspector ->
                            InspectorManager.setCurrentInspector(inspector)
                            showInspectorSelection = false
                            currentScreen = AppScreen.ORU_INSPECTION
                            // Здесь автоматически обновится currentInspector через produceState
                        },
                        onBack = {
                            showInspectorSelection = false
                            currentScreen = AppScreen.ORU_SELECTION
                        }
                    )
                }
            }

            NotificationToast()
        }
    }
}

@Composable
fun InspectorBadge(
    inspector: Inspector?,
    onChangeClick: () -> Unit
) {
    Card(
        elevation = 4.dp,
        backgroundColor = MaterialTheme.colors.primary.copy(alpha = 0.1f)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(8.dp)
        ) {
            if (inspector != null) {
                Column(
                    modifier = Modifier.padding(end = 8.dp)
                ) {
                    Text(
                        text = inspector.name,
                        style = MaterialTheme.typography.body2,
                        fontWeight = FontWeight.Bold
                    )
                    if (inspector.position.isNotBlank()) {
                        Text(
                            text = inspector.position,
                            style = MaterialTheme.typography.caption,
                            color = MaterialTheme.colors.onSurface.copy(alpha = 0.7f)
                        )
                    }
                }
            } else {
                Text(
                    text = "Дежурный не выбран",
                    style = MaterialTheme.typography.body2,
                    color = MaterialTheme.colors.error,
                    modifier = Modifier.padding(end = 8.dp)
                )
            }

            IconButton(
                onClick = onChangeClick,
                modifier = Modifier.size(24.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.SwapHoriz, // Или другую иконку для смены
                    contentDescription = "Сменить дежурного",
                    tint = MaterialTheme.colors.primary
                )
            }
        }
    }
}


@Composable
fun OruSelectionScreen(
    oruList: List<Oru>,
    onOruSelected: (Oru) -> Unit,
    onAbout: () -> Unit,
    onHistory: () -> Unit
    // Убираем onExport
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
    var showInspectorDialog by remember { mutableStateOf(false) }

    // Загрузка существующего черновика при открытии экрана
    LaunchedEffect(oru) {
        val drafts = InspectionRepository.getSessions()
            .filter { it.oru.voltage == oru.voltage && it.isDraft }
            .sortedByDescending { it.dateTime } // Сортируем по дате (новейший первый)

        if (drafts.isNotEmpty()) {
            val draft = drafts.first() // Берем самый свежий черновик
            val loadedData = mutableMapOf<String, String>()

            draft.results.forEach { result ->
                result.parameters.forEach { (paramName, value) ->
                    val key = "${result.equipment.id}_$paramName"
                    loadedData[key] = value
                }
            }

            inspectionData = loadedData
            NotificationManager.showNotification("Загружен черновик", NotificationType.INFO)
        }
    }

    // Автосохранение черновика при изменении данных
    LaunchedEffect(inspectionData) {
        if (inspectionData.isNotEmpty() && inspectionData.values.any { it.isNotBlank() }) {
            saveDraftAutoSave(oru, inspectionData)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Шапка с кнопкой назад
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            IconButton(onClick = {
                // При выходе сохраняем черновик, если есть данные
                if (inspectionData.isNotEmpty() && inspectionData.values.any { it.isNotBlank() }) {
                    saveDraftAutoSave(oru, inspectionData)
                    NotificationManager.showNotification("Черновик сохранен", NotificationType.INFO)
                }
                onBack()
            }) {
                Icon(Icons.Filled.ArrowBack, "Назад")
            }
            Text(
                if (oru.voltage == "0") "Здания и сооружения" else "ОРУ-${oru.voltage} кВ",
                style = MaterialTheme.typography.h6,
                modifier = Modifier.weight(1f)
            )

            // Индикатор черновика
            if (inspectionData.isNotEmpty() && inspectionData.values.any { it.isNotBlank() }) {
                Text(
                    "Черновик",
                    color = MaterialTheme.colors.secondary,
                    style = MaterialTheme.typography.caption,
                    modifier = Modifier.padding(horizontal = 8.dp)
                )
            }
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
                    "0" -> BuildingsInspectionLayout(
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
                showSaveDialog = false
                showInspectorDialog = true
            },
            onDismiss = { showSaveDialog = false }
        )
    }

    if (showSaveDialog) {
        AlertDialog(
            onDismissRequest = { showSaveDialog = false },
            title = { Text("Завершение осмотра") },
            text = { Text("Сохранить результаты осмотра?") },
            confirmButton = {
                Button(onClick = {
                    showSaveDialog = false
                    // Сохраняем результаты сразу, используя текущего инспектора
                    val currentInspector = InspectorManager.getCurrentInspector()
                    if (currentInspector != null) {
                        saveInspectionResults(oru, inspectionData, false)
                        onBack()
                        NotificationManager.showNotification("✅ Осмотр сохранен")
                    } else {
                        NotificationManager.showNotification("❌ Не выбран дежурный", NotificationType.ERROR)
                    }
                }) {
                    Text("Сохранить")
                }
            },
            dismissButton = {
                Button(onClick = { showSaveDialog = false }) {
                    Text("Отмена")
                }
            }
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

private fun saveInspectionResults(oru: Oru, data: Map<String, String>, isDraft: Boolean = false) {
    val results = mutableListOf<InspectionResult>()

    oru.equipments.forEach { equipment ->
        val equipmentParams = mutableMapOf<String, String>()
        equipment.parameters.forEach { param ->
            val key = "${equipment.id}_${param.name}"
            equipmentParams[param.name] = data[key] ?: ""
        }
        results.add(InspectionResult(equipment, equipmentParams))
    }

    val currentInspector = InspectorManager.getCurrentInspector()

    if (!isDraft && currentInspector == null) {
        NotificationManager.showNotification("Не выбран дежурный", NotificationType.ERROR)
        return
    }

    // Если это завершенный осмотр, удаляем черновики для этого ОРУ
    if (!isDraft) {
        val existingDrafts = InspectionRepository.getSessions()
            .filter { it.oru.voltage == oru.voltage && it.isDraft }

        existingDrafts.forEach { draft ->
            InspectionRepository.deleteSession(draft.id)
        }
    }

    val session = InspectionSession(
        oru = oru,
        results = results,
        isCompleted = !isDraft,
        inspectorId = if (isDraft) "" else currentInspector?.id ?: "", // Для черновика оставляем пустым
        isDraft = isDraft
    )
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
            Icon(Icons.AutoMirrored.Filled.ArrowBack, "Назад")
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
    onViewDetails: (InspectionSession) -> Unit
) {
    val sessions = remember { mutableStateOf(InspectionRepository.getSessions().sortedByDescending { it.dateTime }) }
    var showDeleteDialog by remember { mutableStateOf<String?>(null) }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(Modifier.fillMaxSize().padding(16.dp)) {
            IconButton(onClick = onBack) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, "Назад")
            }

            Text("История осмотров", style = MaterialTheme.typography.h4)

            // Кнопка экспорта всех данных
            Button(
                onClick = {
                    try {
                        val path = exportToCSV()
                        NotificationManager.showNotification("✅ Все данные экспортированы в: ${File(path).name}")
                    } catch (e: Exception) {
                        NotificationManager.showNotification("❌ Ошибка экспорта: ${e.message}", NotificationType.ERROR)
                    }
                },
                modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                colors = ButtonDefaults.buttonColors(backgroundColor = MaterialTheme.colors.primaryVariant)
            ) {
                Icon(Icons.Default.FileDownload, "Экспорт всех данных")
                Spacer(Modifier.width(8.dp))
                Text("Экспортировать все данные в CSV")
            }

            if (sessions.value.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Нет сохраненных осмотров", style = MaterialTheme.typography.h6)
                }
            } else {
                LazyColumn(modifier = Modifier.weight(1f)) {
                    items(sessions.value) { session ->
                        HistoryItem(
                            session = session,
                            onViewDetails = onViewDetails,
                            onDelete = { showDeleteDialog = session.id }
                        )
                    }
                }
            }
        }

        NotificationToast()
    }

    // Диалог подтверждения удаления
    showDeleteDialog?.let { sessionId ->
        AlertDialog(
            onDismissRequest = { showDeleteDialog = null },
            title = { Text("Подтверждение удаления") },
            text = { Text("Вы уверены, что хотите удалить этот осмотр?") },
            confirmButton = {
                Button(
                    onClick = {
                        InspectionRepository.deleteSession(sessionId)
                        sessions.value = InspectionRepository.getSessions().sortedByDescending { it.dateTime }
                        showDeleteDialog = null
                        NotificationManager.showNotification("🗑️ Осмотр удален")
                    }
                ) {
                    Text("Удалить")
                }
            },
            dismissButton = {
                Button(
                    onClick = { showDeleteDialog = null }
                ) {
                    Text("Отмена")
                }
            }
        )
    }
}

@Composable
fun HistoryItem(
    session: InspectionSession,
    onViewDetails: (InspectionSession) -> Unit,
    onDelete: () -> Unit
) {
    var showContextMenu by remember { mutableStateOf(false) }

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
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        session.oru.name,
                        style = MaterialTheme.typography.h6
                    )
                    Text(
                        "Дата: ${session.dateTimeString}",
                        style = MaterialTheme.typography.body2
                    )
                    Text(
                        "Дежурный: ${session.inspectorName}",
                        style = MaterialTheme.typography.body2,
                        color = MaterialTheme.colors.secondary
                    )
                    Text(
                        "Оборудование: ${session.results.size}",
                        style = MaterialTheme.typography.body2
                    )
                    // Отображаем статус вместо простого "Завершено"
                    Text(
                        session.status,
                        color = when {
                            session.isDraft -> MaterialTheme.colors.secondary
                            session.isCompleted -> MaterialTheme.colors.primary
                            else -> MaterialTheme.colors.onSurface.copy(alpha = 0.7f)
                        },
                        style = MaterialTheme.typography.caption
                    )
                }

                IconButton(
                    onClick = { showContextMenu = true }
                ) {
                    Icon(Icons.Default.MoreVert, "Действия")
                }
            }
        }
    }

    // Контекстное меню
    if (showContextMenu) {
        DropdownMenu(
            expanded = showContextMenu,
            onDismissRequest = { showContextMenu = false }
        ) {
            DropdownMenuItem(
                onClick = {
                    showContextMenu = false
                    onDelete()
                }
            ) {
                Text("Удалить осмотр")
            }
            DropdownMenuItem(
                onClick = {
                    showContextMenu = false
                    try {
                        val path = exportSingleSessionToCSV(session)
                        NotificationManager.showNotification("📊 Осмотр экспортирован в: ${File(path).name}")
                    } catch (e: Exception) {
                        NotificationManager.showNotification("❌ Ошибка экспорта: ${e.message}", NotificationType.ERROR)
                    }
                }
            ) {
                Text("Экспортировать в CSV")
            }

            // Дополнительное действие для черновиков
            if (session.isDraft) {
                DropdownMenuItem(
                    onClick = {
                        showContextMenu = false
                        // Здесь можно добавить функционал продолжения редактирования черновика
                    }
                ) {
                    Text("Продолжить редактирование")
                }
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
                Icon(Icons.AutoMirrored.Filled.ArrowBack, "Назад")
            }
            Text(
                "Детали осмотра: ${session.oru.name}",
                style = MaterialTheme.typography.h6
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Информация о сессии
        Text(
            "Дата и время: ${session.dateTimeString}",
            style = MaterialTheme.typography.body1
        )
        Text(
            "Дежурный: ${session.inspectorName}",
            style = MaterialTheme.typography.body1,
            color = MaterialTheme.colors.secondary
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
                Icon(Icons.AutoMirrored.Filled.ArrowBack, "Назад")
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

        // Информация об экспорте
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

        // Заголовок CSV с добавлением поля "Дежурный"
        writer.write("Дата;Время;ОРУ;Напряжение;Дежурный;Оборудование;Тип оборудования;Параметр;Значение;Норма;Статус;Комментарии")
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

                    // ВСТАВИТЬ ЗДЕСЬ - это заменяет старую строку writer.write
                    writer.write(
                        "$date;$time;${session.oru.name};${session.oru.voltage};${session.inspectorName};" +
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

// Добавьте в Main.kt новый экран
@Composable
fun InspectorSelectionScreen(
    onInspectorSelected: (Inspector) -> Unit,
    onBack: () -> Unit
) {
    var inspectors by remember { mutableStateOf(InspectorManager.getInspectors()) }
    var showAddDialog by remember { mutableStateOf(false) }
    var newInspectorName by remember { mutableStateOf("") }
    var newInspectorPosition by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Шапка
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onBack) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, "Назад")
            }
            Text("Выбор дежурного", style = MaterialTheme.typography.h4)
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (inspectors.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("Нет созданных дежурных", style = MaterialTheme.typography.h6)
                    Text("Добавьте первого дежурного", style = MaterialTheme.typography.body2)
                }
            }
        } else {
            LazyColumn(modifier = Modifier.weight(1f)) {
                items(inspectors) { inspector ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                            .clickable { onInspectorSelected(inspector) },
                        elevation = 2.dp
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(inspector.name, style = MaterialTheme.typography.h6)
                            if (inspector.position.isNotBlank()) {
                                Text(inspector.position, style = MaterialTheme.typography.body2)
                            }
                        }
                    }
                }
            }
        }

        Button(
            onClick = { showAddDialog = true },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Добавить нового дежурного")
        }
    }

    // Диалог добавления нового дежурного
    if (showAddDialog) {
        AlertDialog(
            onDismissRequest = { showAddDialog = false },
            title = { Text("Добавить дежурного") },
            text = {
                Column {
                    OutlinedTextField(
                        value = newInspectorName,
                        onValueChange = { newInspectorName = it },
                        label = { Text("Фамилия и инициалы") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = newInspectorPosition,
                        onValueChange = { newInspectorPosition = it },
                        label = { Text("Должность (необязательно)") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (newInspectorName.isNotBlank()) {
                            val inspector = Inspector(
                                name = newInspectorName,
                                position = newInspectorPosition
                            )
                            if (InspectorManager.addInspector(inspector)) {
                                inspectors = InspectorManager.getInspectors()
                                showAddDialog = false
                                newInspectorName = ""
                                newInspectorPosition = ""
                            } else {
                                NotificationManager.showNotification("Дежурный с таким именем уже существует", NotificationType.ERROR)
                            }
                        }
                    },
                    enabled = newInspectorName.isNotBlank()
                ) {
                    Text("Добавить")
                }
            },
            dismissButton = {
                Button(onClick = { showAddDialog = false }) {
                    Text("Отмена")
                }
            }
        )
    }
}

private fun saveDraftAutoSave(oru: Oru, data: Map<String, String>) {
    val results = mutableListOf<InspectionResult>()

    oru.equipments.forEach { equipment ->
        val equipmentParams = mutableMapOf<String, String>()
        equipment.parameters.forEach { param ->
            val key = "${equipment.id}_${param.name}"
            equipmentParams[param.name] = data[key] ?: ""
        }
        results.add(InspectionResult(equipment, equipmentParams))
    }

    // НЕ удаляем существующие черновики при автосохранении
    val session = InspectionSession(
        oru = oru,
        results = results,
        isCompleted = false,
        inspectorId = "", // Для черновика оставляем пустым
        isDraft = true
    )
    InspectionRepository.saveSession(session)
}


