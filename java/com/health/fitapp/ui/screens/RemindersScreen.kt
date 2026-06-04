package com.health.fitapp.ui.screens

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.*
import com.health.fitapp.ui.components.*
import com.health.fitapp.ui.theme.*
import com.health.fitapp.viewmodel.FitViewModel

@Composable
fun RemindersScreen(vm: FitViewModel) {
    val waterEnabled   by vm.waterEnabled.collectAsState()
    val waterInterval  by vm.waterInterval.collectAsState()
    val workoutEnabled by vm.workoutEnabled.collectAsState()
    val workoutHour    by vm.workoutHour.collectAsState()
    val workoutMinute  by vm.workoutMinute.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
            .padding(bottom = 16.dp)
    ) {
        Column(modifier = Modifier.padding(start = 20.dp, end = 20.dp, top = 56.dp, bottom = 8.dp)) {
            Text("Напоминания", style = MaterialTheme.typography.displayLarge)
            Text("Настройте уведомления", style = MaterialTheme.typography.bodyMedium, color = TextSecondary)
        }

        // Water reminders
        Column(modifier = Modifier.padding(horizontal = 16.dp)) {
            SectionLabel("Вода")
            FitCard {
                // Toggle
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp), verticalAlignment = Alignment.CenterVertically) {
                        Box(modifier = Modifier.size(36.dp).background(BlueAccent.copy(.15f),
                            RoundedCornerShape(9.dp)), contentAlignment = Alignment.Center) {
                            Text("💧", fontSize = 18.sp)
                        }
                        Column {
                            Text("Напоминание о воде", style = MaterialTheme.typography.titleMedium)
                            Text("Каждые $waterInterval ч", style = MaterialTheme.typography.bodyMedium, color = TextSecondary)
                        }
                    }
                    Switch(
                        checked = waterEnabled,
                        onCheckedChange = { vm.setWaterEnabled(it) },
                        colors = SwitchDefaults.colors(checkedThumbColor = Color.White, checkedTrackColor = GreenAccent)
                    )
                }

                if (waterEnabled) {
                    FitDivider()
                    Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)) {
                        Text("Интервал", style = MaterialTheme.typography.bodyMedium, color = TextSecondary,
                            modifier = Modifier.padding(bottom = 8.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            listOf(1, 2, 3, 4).forEach { h ->
                                FilterChip(
                                    selected = waterInterval == h,
                                    onClick = { /* vm.setWaterInterval(h) */ },
                                    label = { Text("$h ч") },
                                    colors = FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = BlueAccent.copy(.15f),
                                        selectedLabelColor = BlueAccent
                                    )
                                )
                            }
                        }
                    }
                }
            }
        }

        Spacer(Modifier.height(12.dp))

        // Workout reminders
        Column(modifier = Modifier.padding(horizontal = 16.dp)) {
            SectionLabel("Тренировки")
            FitCard {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp), verticalAlignment = Alignment.CenterVertically) {
                        Box(modifier = Modifier.size(36.dp).background(OrangeAccent.copy(.15f),
                            RoundedCornerShape(9.dp)), contentAlignment = Alignment.Center) {
                            Text("🏋️", fontSize = 18.sp)
                        }
                        Column {
                            Text("Тренировка", style = MaterialTheme.typography.titleMedium)
                            Text("%02d:%02d".format(workoutHour, workoutMinute),
                                style = MaterialTheme.typography.bodyMedium, color = TextSecondary)
                        }
                    }
                    Switch(
                        checked = workoutEnabled,
                        onCheckedChange = { vm.setWorkoutEnabled(it) },
                        colors = SwitchDefaults.colors(checkedThumbColor = Color.White, checkedTrackColor = GreenAccent)
                    )
                }
            }
        }

        Spacer(Modifier.height(12.dp))

        // Tips reminders
        Column(modifier = Modifier.padding(horizontal = 16.dp)) {
            SectionLabel("Советы по упражнениям")
            FitCard {
                val tips = listOf(
                    Triple("💪","Техника приседа","Раз в 3 дня"),
                    Triple("🏃","Правильный бег","Раз в неделю"),
                    Triple("🧘","Растяжка","Ежедневно после тренировки"),
                )
                tips.forEachIndexed { i, (icon, name, freq) ->
                    if (i > 0) FitDivider()
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 13.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(horizontalArrangement = Arrangement.spacedBy(12.dp), verticalAlignment = Alignment.CenterVertically) {
                            Box(modifier = Modifier.size(36.dp).background(BlueAccent.copy(.1f),
                                RoundedCornerShape(9.dp)), contentAlignment = Alignment.Center) {
                                Text(icon, fontSize = 18.sp)
                            }
                            Column {
                                Text(name, style = MaterialTheme.typography.titleMedium)
                                Text(freq, style = MaterialTheme.typography.bodyMedium, color = TextSecondary)
                            }
                        }
                        Switch(
                            checked = true,
                            onCheckedChange = { },
                            colors = SwitchDefaults.colors(checkedThumbColor = Color.White, checkedTrackColor = GreenAccent)
                        )
                    }
                }
            }
        }

        Spacer(Modifier.height(16.dp))

        // Info
        Column(modifier = Modifier.padding(horizontal = 16.dp)) {
            FitCard {
                Row(
                    modifier = Modifier.padding(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text("ℹ️", fontSize = 20.sp)
                    Text(
                        "Разрешите отправку уведомлений в настройках телефона, чтобы напоминания работали в фоне.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextSecondary
                    )
                }
            }
        }
    }
}
