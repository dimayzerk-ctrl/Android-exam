package com.health.fitapp.data.repository

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import com.health.fitapp.data.model.FoodEntry
import com.health.fitapp.data.model.WaterEntry
import com.health.fitapp.data.model.WeightEntry
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.Calendar

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore("fit_prefs")

class FitRepository(context: Context) {

    private val db   = FitDatabase.getInstance(context)
    private val prefs = context.dataStore

    // ── Keys ────────────────────────────────────────────────────────────────
    private object Keys {
        val WATER_ENABLED   = booleanPreferencesKey("water_enabled")
        val WATER_INTERVAL  = intPreferencesKey("water_interval_h")
        val WORKOUT_ENABLED = booleanPreferencesKey("workout_enabled")
        val WORKOUT_HOUR    = intPreferencesKey("workout_hour")
        val WORKOUT_MINUTE  = intPreferencesKey("workout_minute")
        val CALORIE_GOAL    = intPreferencesKey("calorie_goal")
        val WEIGHT_GOAL     = floatPreferencesKey("weight_goal")
    }

    // ── Weight ───────────────────────────────────────────────────────────────
    val allWeights: Flow<List<WeightEntry>>  = db.weightDao().getAll()
    val last7Weights: Flow<List<WeightEntry>> = db.weightDao().getLast7()

    suspend fun addWeight(kg: Float) = db.weightDao().insert(WeightEntry(weightKg = kg))
    suspend fun deleteWeight(e: WeightEntry) = db.weightDao().delete(e)

    // ── Food ─────────────────────────────────────────────────────────────────
    fun getTodayFood(): Flow<List<FoodEntry>> {
        val start = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 0); set(Calendar.MINUTE, 0); set(Calendar.SECOND, 0)
        }.timeInMillis
        return db.foodDao().getToday(start)
    }

    suspend fun addFood(entry: FoodEntry) = db.foodDao().insert(entry)
    suspend fun deleteFood(e: FoodEntry)  = db.foodDao().delete(e)

    // ── Water ────────────────────────────────────────────────────────────────
    fun getTodayWater(): Flow<WaterEntry?> {
        val start = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 0); set(Calendar.MINUTE, 0); set(Calendar.SECOND, 0)
        }.timeInMillis
        return db.waterDao().getToday(start)
    }

    suspend fun setWaterGlasses(glasses: Int) {
        val start = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 0); set(Calendar.MINUTE, 0); set(Calendar.SECOND, 0)
        }.timeInMillis
        val existing = db.waterDao().getToday(start)
        db.waterDao().insert(WaterEntry(glasses = glasses))
    }

    // ── Preferences ──────────────────────────────────────────────────────────
    val calorieGoal: Flow<Int>   = prefs.data.map { it[Keys.CALORIE_GOAL] ?: 2000 }
    val weightGoal: Flow<Float>  = prefs.data.map { it[Keys.WEIGHT_GOAL]  ?: 70f }
    val waterEnabled: Flow<Boolean>   = prefs.data.map { it[Keys.WATER_ENABLED]   ?: true }
    val waterInterval: Flow<Int>      = prefs.data.map { it[Keys.WATER_INTERVAL]  ?: 2 }
    val workoutEnabled: Flow<Boolean> = prefs.data.map { it[Keys.WORKOUT_ENABLED] ?: true }
    val workoutHour: Flow<Int>        = prefs.data.map { it[Keys.WORKOUT_HOUR]    ?: 8 }
    val workoutMinute: Flow<Int>      = prefs.data.map { it[Keys.WORKOUT_MINUTE]  ?: 0 }

    suspend fun setCalorieGoal(v: Int)    = prefs.edit { it[Keys.CALORIE_GOAL]    = v }
    suspend fun setWeightGoal(v: Float)   = prefs.edit { it[Keys.WEIGHT_GOAL]     = v }
    suspend fun setWaterEnabled(v: Boolean)   = prefs.edit { it[Keys.WATER_ENABLED]   = v }
    suspend fun setWaterInterval(v: Int)      = prefs.edit { it[Keys.WATER_INTERVAL]  = v }
    suspend fun setWorkoutEnabled(v: Boolean) = prefs.edit { it[Keys.WORKOUT_ENABLED] = v }
    suspend fun setWorkoutTime(h: Int, m: Int) {
        prefs.edit { it[Keys.WORKOUT_HOUR] = h; it[Keys.WORKOUT_MINUTE] = m }
    }
}
