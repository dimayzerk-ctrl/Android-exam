package com.health.fitapp.ui.screens

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.*
import com.health.fitapp.ui.components.*
import com.health.fitapp.ui.theme.*
import com.health.fitapp.viewmodel.FitViewModel
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun WeightScreen(vm: FitViewModel) {
    val weights    by vm.last7Weights.collectAsState()
    val allWeights by vm.allWeights.collectAsState()
    val weightGoal by vm.weightGoal.collectAsState()

    var inputKg by remember { mutableStateOf("") }

    val current  = weights.firstOrNull()?.weightKg
    val previous = weights.drop(1).firstOrNull()?.weightKg
    val monthAgo = allWeights.lastOrNull()?.weightKg
    val diff30   = if (current != null && monthAgo != null) current - monthAgo else null
    val bmi      = if (current != null) current / (1.75f * 1.75f) else null

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
            .padding(bottom = 16.dp)
    ) {
        Column(modifier = Modifier.padding(start = 20.dp, end = 20.dp, top = 56.dp, bottom = 8.dp)) {
            Text("Вес", style = MaterialTheme.typography.displayLarge)
            Text("Измерения и прогресс", style = MaterialTheme.typography.bodyMedium, color = TextSecondary)
        }

        // Summary
        Column(modifier = Modifier.padding(horizontal = 16.dp)) {
            FitCard {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    WeightStatBox("${"%.1f".format(current ?: 0f)}", "Текущий кг",
                        MaterialTheme.colorScheme.background, TextPrimary, Modifier.weight(1f))
                    WeightStatBox(
                        diff30?.let { "${"%.1f".format(it)} кг" } ?: "—",
                        "За месяц",
                        if ((diff30 ?: 0f) <= 0f) GreenAccent.copy(.12f) else PinkAccent.copy(.12f),
                        if ((diff30 ?: 0f) <= 0f) GreenAccent else PinkAccent,
                        Modifier.weight(1f)
                    )
                    WeightStatBox("${"%.1f".format(weightGoal)}", "Цель кг",
                        MaterialTheme.colorScheme.background, TextPrimary, Modifier.weight(1f))
                }

                // BMI
                if (bmi != null) {
                    HorizontalDivider(thickness = 0.5.dp, color = MaterialTheme.colorScheme.outline)
                    Row(
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("ИМТ:", style = MaterialTheme.typography.bodyMedium, color = TextSecondary)
                        Text("${"%.1f".format(bmi)}", style = MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.SemiBold)
                        val (bmiLabel, bmiColor) = when {
                            bmi < 18.5f -> "Недовес" to BlueAccent
                            bmi < 25f   -> "Норма ✓" to GreenAccent
                            bmi < 30f   -> "Избыток" to OrangeAccent
                            else        -> "Ожирение" to PinkAccent
                        }
                        FitBadge(bmiLabel, bmiColor)
                    }
                }
            }
        }

        Spacer(Modifier.height(12.dp))

        // Graph
        if (weights.size >= 2) {
            Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                SectionLabel("График (последние 7)")
                FitCard {
                    WeightChart(weights = weights.reversed())
                }
            }
            Spacer(Modifier.height(12.dp))
        }

        // Add measurement
        Column(modifier = Modifier.padding(horizontal = 16.dp)) {
            SectionLabel("Добавить измерение")
            FitCard {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp),
                        verticalAlignment = Alignment.CenterVertically) {
                        OutlinedTextField(
                            value = inputKg,
                            onValueChange = { inputKg = it },
                            label = { Text("Вес в кг") },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                            singleLine = true
                        )
                        Button(
                            onClick = {
                                inputKg.toFloatOrNull()?.let { vm.addWeight(it); inputKg = "" }
                            },
                            modifier = Modifier.height(56.dp),
                            shape = RoundedCornerShape(14.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = PurpleAccent)
                        ) {
                            Text("Добавить", color = Color.White, fontWeight = FontWeight.SemiBold)
                        }
                    }
                }
            }
        }

        Spacer(Modifier.height(12.dp))

        // History
        if (allWeights.isNotEmpty()) {
            Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                SectionLabel("История")
                FitCard {
                    val dateFormat = SimpleDateFormat("d MMM", Locale("ru"))
                    allWeights.take(10).forEachIndexed { i, entry ->
                        if (i > 0) FitDivider()
                        val prev = allWeights.getOrNull(i + 1)?.weightKg
                        val delta = if (prev != null) entry.weightKg - prev else null
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text("${"%.1f".format(entry.weightKg)} кг",
                                    style = MaterialTheme.typography.titleMedium)
                                Text(
                                    when (i) { 0 -> "Сегодня"; 1 -> "Вчера"
                                               else -> dateFormat.format(Date(entry.date)) },
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = TextSecondary
                                )
                            }
                            if (delta != null) {
                                FitBadge(
                                    "${if (delta > 0) "+" else ""}${"%.1f".format(delta)}",
                                    if (delta <= 0) GreenAccent else PinkAccent
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun WeightStatBox(value: String, label: String, bgColor: Color, textColor: Color, modifier: Modifier) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(bgColor)
            .padding(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(value, style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold, color = textColor)
        Text(label, style = MaterialTheme.typography.labelSmall, color = TextSecondary)
    }
}

@Composable
fun WeightChart(weights: List<com.health.fitapp.data.model.WeightEntry>) {
    if (weights.isEmpty()) return
    val values = weights.map { it.weightKg }
    val minV = (values.minOrNull() ?: 0f) - 1f
    val maxV = (values.maxOrNull() ?: 1f) + 1f

    Canvas(
        modifier = Modifier
            .fillMaxWidth()
            .height(140.dp)
            .padding(horizontal = 16.dp, vertical = 16.dp)
    ) {
        val w = size.width
        val h = size.height
        val n = values.size
        if (n < 2) return@Canvas

        fun xOf(i: Int) = i * w / (n - 1)
        fun yOf(v: Float) = h - (v - minV) / (maxV - minV) * h

        // Line
        val path = Path()
        values.forEachIndexed { i, v ->
            val x = xOf(i); val y = yOf(v)
            if (i == 0) path.moveTo(x, y) else path.lineTo(x, y)
        }
        drawPath(path, PurpleAccent, style = Stroke(width = 2.5.dp.toPx(),
            cap = StrokeCap.Round, join = StrokeJoin.Round))

        // Fill under line
        val fillPath = Path().apply { addPath(path) }
        fillPath.lineTo(xOf(n - 1), h)
        fillPath.lineTo(0f, h)
        fillPath.close()
        drawPath(fillPath, Brush.verticalGradient(
            listOf(PurpleAccent.copy(.2f), Color.Transparent), 0f, h))

        // Dots
        values.forEachIndexed { i, v ->
            drawCircle(PurpleAccent, 5f, Offset(xOf(i), yOf(v)))
            drawCircle(Color.White, 3f, Offset(xOf(i), yOf(v)))
        }
    }
}
