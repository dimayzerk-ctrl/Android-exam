package com.health.fitapp.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.health.fitapp.ui.components.ActivityRings
import com.health.fitapp.ui.components.CardRow
import com.health.fitapp.ui.components.FitCard
import com.health.fitapp.ui.components.FitDivider
import com.health.fitapp.ui.components.FitProgressBar
import com.health.fitapp.ui.components.SectionLabel
import com.health.fitapp.ui.components.StatChip
import com.health.fitapp.ui.theme.BlueAccent
import com.health.fitapp.ui.theme.GreenAccent
import com.health.fitapp.ui.theme.OrangeAccent
import com.health.fitapp.ui.theme.PinkAccent
import com.health.fitapp.ui.theme.PurpleAccent
import com.health.fitapp.ui.theme.TextSecondary
import com.health.fitapp.viewmodel.FitViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun HomeScreen(
    vm: FitViewModel,
    onNavigate: (String) -> Unit
) {
    val waterGlasses by vm.waterGlasses.collectAsState()
    val todayCalories by vm.todayCalories.collectAsState()
    val calorieGoal by vm.calorieGoal.collectAsState()
    val weights by vm.last7Weights.collectAsState()

    val currentWeight = weights.firstOrNull()?.weightKg
    val prevWeight = weights.drop(1).firstOrNull()?.weightKg

    val weightDiff = if (currentWeight != null && prevWeight != null) {
        currentWeight - prevWeight
    } else {
        null
    }

    val dateStr = SimpleDateFormat(
        "EEE, d MMMM",
        Locale("ru")
    ).format(Date())

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
            .padding(bottom = 16.dp)
    ) {

        // Header
        Column(
            modifier = Modifier.padding(
                start = 20.dp,
                end = 20.dp,
                top = 56.dp,
                bottom = 8.dp
            )
        ) {
            Text(
                text = "Сегодня",
                style = MaterialTheme.typography.displayLarge
            )

            Text(
                text = dateStr,
                style = MaterialTheme.typography.bodyMedium,
                color = TextSecondary
            )
        }

        // Activity
        Column(modifier = Modifier.padding(horizontal = 16.dp)) {
            SectionLabel("Активность")

            FitCard {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {

                    Box(
                        modifier = Modifier.size(160.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        ActivityRings(
                            moveProgress = (todayCalories / calorieGoal.toFloat())
                                .coerceIn(0f, 1f),
                            exerciseProgress = 0.65f,
                            standProgress = 0.70f
                        )

                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "$todayCalories",
                                style = MaterialTheme.typography.displayMedium,
                                fontWeight = FontWeight.Bold
                            )

                            Text(
                                text = "ккал",
                                style = MaterialTheme.typography.bodyMedium,
                                color = TextSecondary
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        StatChip("$todayCalories", "🔥 ккал", PinkAccent)
                        StatChip("32", "⚡ мин", GreenAccent)
                        StatChip("7 842", "👟 шаги", BlueAccent)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Water
        Column(modifier = Modifier.padding(horizontal = 16.dp)) {
            SectionLabel("Вода")

            FitCard {
                Column(modifier = Modifier.padding(16.dp)) {

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {

                        Column {
                            Row(verticalAlignment = Alignment.Bottom) {
                                Text(
                                    text = "$waterGlasses",
                                    style = MaterialTheme.typography.displayMedium,
                                    color = BlueAccent,
                                    fontWeight = FontWeight.Bold
                                )

                                Text(
                                    text = " / 8 стак.",
                                    style = MaterialTheme.typography.bodyLarge,
                                    color = TextSecondary,
                                    modifier = Modifier.padding(bottom = 4.dp)
                                )
                            }

                            Text(
                                text = "Нажми на стакан чтобы отметить",
                                style = MaterialTheme.typography.bodyMedium,
                                color = TextSecondary
                            )
                        }

                        Text(
                            text = "💧",
                            fontSize = 38.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        (1..8).forEach { i ->

                            val filled = i <= waterGlasses

                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .aspectRatio(1f)
                                    .clip(CircleShape)
                                    .background(
                                        if (filled) BlueAccent else Color.Transparent
                                    )
                                    .border(
                                        width = 2.dp,
                                        color = BlueAccent,
                                        shape = CircleShape
                                    )
                                    .clickable {
                                        vm.setWaterGlasses(
                                            if (i <= waterGlasses) i - 1 else i
                                        )
                                    },
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "💧",
                                    fontSize = if (filled) 14.sp else 12.sp
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    FitProgressBar(
                        progress = waterGlasses / 8f,
                        color = BlueAccent
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Quick actions
        Column(modifier = Modifier.padding(horizontal = 16.dp)) {
            SectionLabel("Быстрые действия")

            FitCard {
                CardRow(
                    icon = "🏋️",
                    iconBg = OrangeAccent.copy(alpha = 0.15f),
                    title = "Тренировки",
                    subtitle = "3 плана активны",
                    onClick = { onNavigate("workouts") }
                )

                FitDivider()

                CardRow(
                    icon = "🫁",
                    iconBg = BlueAccent.copy(alpha = 0.15f),
                    title = "Дыхательная гимнастика",
                    subtitle = "Снижение стресса",
                    onClick = { onNavigate("breath") }
                )

                FitDivider()

                CardRow(
                    icon = "🥗",
                    iconBg = GreenAccent.copy(alpha = 0.15f),
                    title = "Калькулятор калорий",
                    subtitle = "$todayCalories / $calorieGoal ккал",
                    onClick = { onNavigate("calories") }
                )

                FitDivider()

                val weightSubtitle = if (currentWeight != null) {
                    val diffStr = weightDiff?.let {
                        val sign = if (it > 0) "+" else ""
                        " ($sign${"%.1f".format(it)} кг)"
                    } ?: ""

                    "${"%.1f".format(currentWeight)} кг$diffStr"
                } else {
                    "Нет данных"
                }

                CardRow(
                    icon = "⚖️",
                    iconBg = PurpleAccent.copy(alpha = 0.15f),
                    title = "Вес",
                    subtitle = weightSubtitle,
                    onClick = { onNavigate("weight") }
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Reminders
        Column(modifier = Modifier.padding(horizontal = 16.dp)) {
            SectionLabel("Напоминания")

            FitCard {
                CardRow(
                    icon = "🔔",
                    iconBg = OrangeAccent.copy(alpha = 0.15f),
                    title = "Напоминания",
                    subtitle = "Вода каждые 2 ч · Тренировка в 08:00",
                    onClick = { onNavigate("reminders") }
                )
            }
        }
    }
}

