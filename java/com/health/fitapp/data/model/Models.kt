package com.health.fitapp.data.model

import androidx.room.*
import java.util.Date

// ── Weight entry ──────────────────────────────────────────────────────────────
@Entity(tableName = "weight_entries")
data class WeightEntry(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val weightKg: Float,
    val date: Long = System.currentTimeMillis()
)

// ── Food / calorie log ────────────────────────────────────────────────────────
@Entity(tableName = "food_entries")
data class FoodEntry(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val mealType: String,   // Breakfast / Lunch / Dinner / Snack
    val calories: Int,
    val protein: Float = 0f,
    val fat: Float     = 0f,
    val carbs: Float   = 0f,
    val date: Long = System.currentTimeMillis()
)

// ── Water log ─────────────────────────────────────────────────────────────────
@Entity(tableName = "water_entries")
data class WaterEntry(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val glasses: Int,
    val date: Long = System.currentTimeMillis()
)

// ── Workout plan ──────────────────────────────────────────────────────────────
data class Exercise(
    val name: String,
    val sets: String,
    val tip: String
)

data class WorkoutPlan(
    val id: String,
    val name: String,
    val emoji: String,
    val durationMin: Int,
    val colorHex: Long,
    val progress: Float,
    val exercises: List<Exercise>
)

// ── Breathing technique ───────────────────────────────────────────────────────
data class BreathPhase(val label: String, val seconds: Int, val expanding: Boolean)

data class BreathTechnique(
    val id: String,
    val name: String,
    val description: String,
    val emoji: String,
    val colorHex: Long,
    val phases: List<BreathPhase>
)

// ── Reminder settings (stored in DataStore, not Room) ─────────────────────────
data class ReminderSettings(
    val waterEnabled: Boolean = true,
    val waterIntervalHours: Int = 2,
    val workoutEnabled: Boolean = true,
    val workoutHour: Int = 8,
    val workoutMinute: Int = 0
)
