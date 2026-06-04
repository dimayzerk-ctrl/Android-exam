package com.health.fitapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.*
import androidx.lifecycle.viewmodel.compose.viewModel
import com.health.fitapp.notifications.createNotificationChannels
import com.health.fitapp.ui.screens.*
import com.health.fitapp.ui.theme.*
import com.health.fitapp.viewmodel.FitViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        createNotificationChannels(this)
        setContent {
            FitAppTheme {
                FitApp()
            }
        }
    }
}

data class NavItem(val route: String, val label: String, val emoji: String)

@Composable
fun FitApp() {
    val vm: FitViewModel = viewModel()
    var currentRoute by remember { mutableStateOf("home") }

    val navItems = listOf(
        NavItem("home",      "Главная",   "🏠"),
        NavItem("workouts",  "Тренировки","🏋️"),
        NavItem("breath",    "Дыхание",   "🫁"),
        NavItem("calories",  "Калории",   "🥗"),
        NavItem("weight",    "Вес",       "⚖️"),
    )

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        bottomBar = {
            NavigationBar(
                containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.95f),
                tonalElevation = 0.dp,
                modifier = Modifier.height(80.dp)
            ) {
                navItems.forEach { item ->
                    val selected = currentRoute == item.route ||
                        (currentRoute == "reminders" && item.route == "home")
                    NavigationBarItem(
                        selected = selected,
                        onClick = { currentRoute = item.route },
                        icon = {
                            Text(item.emoji, fontSize = 22.sp)
                        },
                        label = {
                            Text(
                                item.label,
                                fontSize = 10.sp,
                                fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal
                            )
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedTextColor     = BlueAccent,
                            unselectedTextColor   = TextTertiary,
                            indicatorColor        = Color.Transparent
                        )
                    )
                }
            }
        }
    ) { padding ->
        Box(modifier = Modifier.padding(bottom = padding.calculateBottomPadding())) {
            when (currentRoute) {
                "home"      -> HomeScreen(vm, onNavigate = { currentRoute = it })
                "workouts"  -> WorkoutsScreen(vm)
                "breath"    -> BreathScreen(vm)
                "calories"  -> CaloriesScreen(vm)
                "weight"    -> WeightScreen(vm)
                "reminders" -> RemindersScreen(vm)
            }
        }
    }
}
