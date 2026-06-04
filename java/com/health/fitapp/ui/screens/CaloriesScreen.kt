package com.health.fitapp.ui.screens

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.*
import com.health.fitapp.data.model.FoodEntry
import com.health.fitapp.ui.components.*
import com.health.fitapp.ui.theme.*
import com.health.fitapp.viewmodel.FitViewModel
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CaloriesScreen(vm: FitViewModel) {
    val todayFood     by vm.todayFood.collectAsState()
    val todayCalories by vm.todayCalories.collectAsState()
    val todayProtein  by vm.todayProtein.collectAsState()
    val todayFat      by vm.todayFat.collectAsState()
    val todayCarbs    by vm.todayCarbs.collectAsState()
    val calorieGoal   by vm.calorieGoal.collectAsState()

    var foodName   by remember { mutableStateOf("") }
    var calories   by remember { mutableStateOf("") }
    var protein    by remember { mutableStateOf("") }
    var fat        by remember { mutableStateOf("") }
    var carbs      by remember { mutableStateOf("") }
    var mealType   by remember { mutableStateOf("Завтрак") }

    val mealTypes = listOf("🌅 Завтрак","☀️ Обед","🌙 Ужин","🍎 Перекус")
    val progress = (todayCalories / calorieGoal.toFloat()).coerceIn(0f, 1f)
    val remaining = maxOf(0, calorieGoal - todayCalories)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
            .padding(bottom = 16.dp)
    ) {
        Column(modifier = Modifier.padding(start = 20.dp, end = 20.dp, top = 56.dp, bottom = 8.dp)) {
            Text("Калории", style = MaterialTheme.typography.displayLarge)
            Text("Отслеживание питания", style = MaterialTheme.typography.bodyMedium, color = TextSecondary)
        }

        // Summary card
        Column(modifier = Modifier.padding(horizontal = 16.dp)) {
            FitCard {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                "$todayCalories",
                                style = MaterialTheme.typography.displayMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Text("из $calorieGoal ккал",
                                style = MaterialTheme.typography.bodyMedium, color = TextSecondary)
                        }
                        Column(horizontalAlignment = Alignment.End) {
                            Text(
                                "$remaining",
                                style = MaterialTheme.typography.headlineLarge,
                                fontWeight = FontWeight.Bold,
                                color = if (remaining > 0) GreenAccent else PinkAccent
                            )
                            Text("осталось", style = MaterialTheme.typography.bodyMedium, color = TextSecondary)
                        }
                    }

                    Spacer(Modifier.height(12.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(10.dp)
                            .clip(RoundedCornerShape(5.dp))
                            .background(MaterialTheme.colorScheme.outline.copy(.3f))
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth(progress)
                                .fillMaxHeight()
                                .clip(RoundedCornerShape(5.dp))
                                .background(
                                    if (progress < 0.8f) GreenAccent
                                    else if (progress < 1f) OrangeAccent
                                    else PinkAccent
                                )
                        )
                    }

                    Spacer(Modifier.height(14.dp))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        MacroChip("Белки","${"%.0f".format(todayProtein)} г", OrangeAccent, Modifier.weight(1f))
                        MacroChip("Жиры","${"%.0f".format(todayFat)} г", BlueAccent, Modifier.weight(1f))
                        MacroChip("Углев.","${"%.0f".format(todayCarbs)} г", GreenAccent, Modifier.weight(1f))
                    }
                }
            }
        }

        Spacer(Modifier.height(12.dp))

        // Add food
        Column(modifier = Modifier.padding(horizontal = 16.dp)) {
            SectionLabel("Добавить приём пищи")
            FitCard {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    // Meal type selector
                    Row(modifier = Modifier.horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        mealTypes.forEach { mt ->
                            val clean = mt.replace(Regex("^.\\s"), "")
                            val sel = mealType == clean
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(20.dp))
                                    .background(if (sel) BlueAccent.copy(.12f) else Color.Transparent)
                                    .border(1.5.dp, if (sel) BlueAccent else MaterialTheme.colorScheme.outline,
                                        RoundedCornerShape(20.dp))
                                    .clickable { mealType = clean }
                                    .padding(horizontal = 14.dp, vertical = 8.dp)
                            ) {
                                Text(mt, style = MaterialTheme.typography.bodyMedium,
                                    color = if (sel) BlueAccent else TextPrimary,
                                    fontWeight = if (sel) FontWeight.SemiBold else FontWeight.Normal)
                            }
                        }
                    }

                    OutlinedTextField(
                        value = foodName,
                        onValueChange = { foodName = it },
                        label = { Text("Название продукта") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        singleLine = true
                    )

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = calories, onValueChange = { calories = it },
                            label = { Text("ккал") },
                            modifier = Modifier.weight(1f), shape = RoundedCornerShape(12.dp),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            singleLine = true
                        )
                        OutlinedTextField(
                            value = protein, onValueChange = { protein = it },
                            label = { Text("Белки г") },
                            modifier = Modifier.weight(1f), shape = RoundedCornerShape(12.dp),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                            singleLine = true
                        )
                    }

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = fat, onValueChange = { fat = it },
                            label = { Text("Жиры г") },
                            modifier = Modifier.weight(1f), shape = RoundedCornerShape(12.dp),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                            singleLine = true
                        )
                        OutlinedTextField(
                            value = carbs, onValueChange = { carbs = it },
                            label = { Text("Углев. г") },
                            modifier = Modifier.weight(1f), shape = RoundedCornerShape(12.dp),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                            singleLine = true
                        )
                    }

                    Button(
                        onClick = {
                            if (foodName.isNotBlank() && calories.isNotBlank()) {
                                vm.addFood(FoodEntry(
                                    name = foodName,
                                    mealType = mealType,
                                    calories = calories.toIntOrNull() ?: 0,
                                    protein = protein.toFloatOrNull() ?: 0f,
                                    fat = fat.toFloatOrNull() ?: 0f,
                                    carbs = carbs.toFloatOrNull() ?: 0f
                                ))
                                foodName = ""; calories = ""; protein = ""; fat = ""; carbs = ""
                            }
                        },
                        modifier = Modifier.fillMaxWidth().height(50.dp),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = GreenAccent)
                    ) {
                        Text("Добавить", style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold, color = Color.White)
                    }
                }
            }
        }

        Spacer(Modifier.height(12.dp))

        // Today's log
        if (todayFood.isNotEmpty()) {
            Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                SectionLabel("Сегодня")
                FitCard {
                    todayFood.forEachIndexed { i, entry ->
                        if (i > 0) FitDivider()
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(RoundedCornerShape(9.dp))
                                    .background(GreenAccent.copy(.15f)),
                                contentAlignment = Alignment.Center
                            ) { Text("🍽️", fontSize = 18.sp) }
                            Spacer(Modifier.width(14.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(entry.name, style = MaterialTheme.typography.titleMedium)
                                Text(entry.mealType, style = MaterialTheme.typography.bodyMedium,
                                    color = TextSecondary)
                            }
                            Column(horizontalAlignment = Alignment.End) {
                                Text("${entry.calories} ккал",
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.SemiBold)
                                Text("Б${entry.protein.toInt()}·Ж${entry.fat.toInt()}·У${entry.carbs.toInt()}",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = TextTertiary)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun MacroChip(label: String, value: String, color: Color, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(10.dp))
            .background(MaterialTheme.colorScheme.background)
            .padding(10.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(value, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = color)
        Text(label, style = MaterialTheme.typography.labelSmall, color = TextSecondary)
    }
}
