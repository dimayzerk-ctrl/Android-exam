package com.health.fitapp.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.*
import com.health.fitapp.data.model.BreathTechnique
import com.health.fitapp.ui.components.*
import com.health.fitapp.ui.theme.*
import com.health.fitapp.viewmodel.FitViewModel
import kotlinx.coroutines.delay

@Composable
fun BreathScreen(vm: FitViewModel) {
    var selectedTechnique by remember { mutableStateOf(vm.breathTechniques.first()) }
    var running by remember { mutableStateOf(false) }
    var phaseIndex by remember { mutableIntStateOf(0) }
    var countdown by remember { mutableIntStateOf(0) }
    var sessionCount by remember { mutableIntStateOf(14) }
    var totalMinutes by remember { mutableIntStateOf(87) }

    val currentPhase = selectedTechnique.phases[phaseIndex % selectedTechnique.phases.size]

    // Animation
    val scale by animateFloatAsState(
        targetValue = if (running && currentPhase.expanding) 1.25f else if (running) 0.85f else 1f,
        animationSpec = tween(currentPhase.seconds * 900, easing = EaseInOutQuad),
        label = "breathScale"
    )

    // Timer coroutine
    LaunchedEffect(running, phaseIndex, selectedTechnique) {
        if (!running) return@LaunchedEffect
        val phase = selectedTechnique.phases[phaseIndex % selectedTechnique.phases.size]
        countdown = phase.seconds
        while (countdown > 0) {
            delay(1000)
            countdown--
        }
        phaseIndex++
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
            .padding(bottom = 16.dp)
    ) {
        Column(modifier = Modifier.padding(start = 20.dp, end = 20.dp, top = 56.dp, bottom = 8.dp)) {
            Text("Дыхание", style = MaterialTheme.typography.displayLarge)
            Text("Успокойтесь и сосредоточьтесь",
                style = MaterialTheme.typography.bodyMedium, color = TextSecondary)
        }

        // Main breath circle
        Column(modifier = Modifier.padding(horizontal = 16.dp)) {
            FitCard {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .size(200.dp)
                            .scale(scale)
                            .clip(CircleShape)
                            .background(BlueAccent.copy(.15f))
                            .border(2.dp, BlueAccent.copy(.4f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                if (running) "$countdown" else selectedTechnique.phases[0].seconds.toString(),
                                style = MaterialTheme.typography.displayLarge,
                                fontWeight = FontWeight.Light,
                                color = BlueAccent
                            )
                            Text(
                                if (running) currentPhase.label else "Готов",
                                style = MaterialTheme.typography.bodyMedium,
                                color = TextSecondary
                            )
                        }
                    }

                    Spacer(Modifier.height(20.dp))

                    Button(
                        onClick = {
                            if (running) {
                                running = false
                                phaseIndex = 0
                                countdown = selectedTechnique.phases[0].seconds
                            } else {
                                phaseIndex = 0
                                running = true
                            }
                        },
                        modifier = Modifier.fillMaxWidth().height(50.dp),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (running) PinkAccent else BlueAccent
                        )
                    ) {
                        Text(
                            if (running) "Остановить" else "Начать",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = Color.White
                        )
                    }

                    Spacer(Modifier.height(12.dp))
                    Text(
                        "${selectedTechnique.phases.map { it.label }.joinToString(" · ")}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextSecondary,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }

        Spacer(Modifier.height(12.dp))

        // Techniques
        Column(modifier = Modifier.padding(horizontal = 16.dp)) {
            SectionLabel("Техники")
            FitCard {
                vm.breathTechniques.forEachIndexed { i, tech ->
                    if (i > 0) FitDivider()
                    val isSelected = tech.id == selectedTechnique.id
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                selectedTechnique = tech
                                running = false
                                phaseIndex = 0
                                countdown = tech.phases[0].seconds
                            }
                            .padding(horizontal = 16.dp, vertical = 13.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(RoundedCornerShape(9.dp))
                                .background(Color(tech.colorHex).copy(.15f)),
                            contentAlignment = Alignment.Center
                        ) { Text(tech.emoji, fontSize = 18.sp) }
                        Column(modifier = Modifier.weight(1f)) {
                            Text(tech.name, style = MaterialTheme.typography.titleMedium)
                            Text(tech.description, style = MaterialTheme.typography.bodyMedium,
                                color = TextSecondary)
                        }
                        if (isSelected) FitBadge("Активна", BlueAccent)
                    }
                }
            }
        }

        Spacer(Modifier.height(12.dp))

        // Stats
        Column(modifier = Modifier.padding(horizontal = 16.dp)) {
            SectionLabel("Статистика")
            FitCard {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(12.dp))
                            .background(MaterialTheme.colorScheme.background)
                            .padding(12.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text("$sessionCount", style = MaterialTheme.typography.headlineLarge,
                            fontWeight = FontWeight.Bold, color = BlueAccent)
                        Text("сессий", style = MaterialTheme.typography.bodyMedium, color = TextSecondary)
                    }
                    Spacer(Modifier.width(12.dp))
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(12.dp))
                            .background(MaterialTheme.colorScheme.background)
                            .padding(12.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text("$totalMinutes", style = MaterialTheme.typography.headlineLarge,
                            fontWeight = FontWeight.Bold, color = PurpleAccent)
                        Text("минут", style = MaterialTheme.typography.bodyMedium, color = TextSecondary)
                    }
                }
            }
        }
    }
}

private val EaseInOutQuad = Easing { t ->
    if (t < 0.5f) 2 * t * t else 1 - (-2 * t + 2f).let { it * it } / 2
}
