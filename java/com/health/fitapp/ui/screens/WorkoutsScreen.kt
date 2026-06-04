package com.health.fitapp.ui.screens

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.*
import com.health.fitapp.data.model.WorkoutPlan
import com.health.fitapp.ui.components.*
import com.health.fitapp.ui.theme.*
import com.health.fitapp.viewmodel.FitViewModel

@Composable
fun WorkoutsScreen(vm: FitViewModel) {
    var selectedPlan by remember { mutableStateOf<WorkoutPlan?>(null) }

    if (selectedPlan != null) {
        WorkoutDetailScreen(plan = selectedPlan!!, onBack = { selectedPlan = null })
        return
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
            .padding(bottom = 16.dp)
    ) {
        Column(modifier = Modifier.padding(start = 20.dp, end = 20.dp, top = 56.dp, bottom = 8.dp)) {
            Text("Тренировки", style = MaterialTheme.typography.displayLarge)
            Text("Выберите программу", style = MaterialTheme.typography.bodyMedium, color = TextSecondary)
        }

        Column(modifier = Modifier.padding(horizontal = 16.dp)) {
            SectionLabel("Программы")
            vm.workoutPlans.forEachIndexed { index, plan ->
                Spacer(if (index == 0) Modifier.height(0.dp) else Modifier.height(10.dp))
                WorkoutPlanCard(plan) { selectedPlan = plan }
            }
        }

        Spacer(Modifier.height(16.dp))

        Column(modifier = Modifier.padding(horizontal = 16.dp)) {
            SectionLabel("Советы по технике")
            FitCard {
                listOf(
                    Triple("🏋️","Присед","Спина прямая, колени не выходят за носки"),
                    Triple("💪","Жим лёжа","Лопатки сведены, гриф опускается к груди"),
                    Triple("🧘","Планка","Тело — прямая линия, не опускать таз"),
                    Triple("🏃","Бег","Приземляйся на среднюю часть стопы"),
                    Triple("⚡","Берпи","Контролируй опускание, не падай на пол"),
                ).forEachIndexed { i, (icon, name, tip) ->
                    if (i > 0) FitDivider()
                    CardRow(icon, BlueAccent.copy(.12f), name, tip)
                }
            }
        }
    }
}

@Composable
fun WorkoutPlanCard(plan: WorkoutPlan, onClick: () -> Unit) {
    val color = Color(plan.colorHex)
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(0.5.dp, MaterialTheme.colorScheme.outline)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(52.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(color.copy(.15f)),
                contentAlignment = Alignment.Center
            ) { Text(plan.emoji, fontSize = 24.sp) }

            Column(modifier = Modifier.weight(1f)) {
                Text(plan.name, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.SemiBold)
                Text(
                    "${plan.exercises.size} упражнений · ${plan.durationMin} мин",
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextSecondary
                )
                Spacer(Modifier.height(8.dp))
                FitProgressBar(plan.progress, color)
            }

            FitBadge(
                text = if (plan.progress >= 1f) "✓" else "${(plan.progress * 100).toInt()}%",
                color = color
            )
        }
    }
}

@Composable
fun WorkoutDetailScreen(plan: WorkoutPlan, onBack: () -> Unit) {
    val color = Color(plan.colorHex)
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
            .padding(bottom = 32.dp)
    ) {
        // Back button
        Row(
            modifier = Modifier
                .padding(start = 16.dp, top = 56.dp)
                .clickable { onBack() },
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("‹", fontSize = 22.sp, color = BlueAccent, fontWeight = FontWeight.Light)
            Text(" Назад", color = BlueAccent, style = MaterialTheme.typography.bodyLarge)
        }

        // Hero card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = color)
        ) {
            Column(modifier = Modifier.padding(24.dp)) {
                Text(plan.emoji, fontSize = 40.sp)
                Spacer(Modifier.height(8.dp))
                Text(plan.name, style = MaterialTheme.typography.headlineLarge,
                    fontWeight = FontWeight.Bold, color = Color.White)
                Text(
                    "${plan.exercises.size} упражнений · ${plan.durationMin} мин · ${(plan.progress*100).toInt()}% выполнено",
                    color = Color.White.copy(.8f),
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }

        // Exercises
        Column(modifier = Modifier.padding(horizontal = 16.dp)) {
            SectionLabel("Упражнения")
            FitCard {
                plan.exercises.forEachIndexed { i, ex ->
                    if (i > 0) FitDivider()
                    Row(
                        modifier = Modifier.padding(16.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalAlignment = Alignment.Top
                    ) {
                        Box(
                            modifier = Modifier
                                .size(28.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.background),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                "${i + 1}",
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.SemiBold,
                                color = TextSecondary
                            )
                        }
                        Column(modifier = Modifier.weight(1f)) {
                            Text(ex.name, style = MaterialTheme.typography.titleMedium)
                            Text(ex.sets, style = MaterialTheme.typography.bodyMedium, color = color)
                            Text(
                                "ℹ️ ${ex.tip}",
                                style = MaterialTheme.typography.bodyMedium,
                                color = TextSecondary,
                                modifier = Modifier.padding(top = 2.dp)
                            )
                        }
                    }
                }
            }
        }

        Spacer(Modifier.height(16.dp))

        // Start button
        Button(
            onClick = { },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .height(54.dp),
            shape = RoundedCornerShape(14.dp),
            colors = ButtonDefaults.buttonColors(containerColor = color)
        ) {
            Text("Начать тренировку", style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold, color = Color.White)
        }
    }
}
