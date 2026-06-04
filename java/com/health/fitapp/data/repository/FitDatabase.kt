package com.health.fitapp.data.repository

import android.content.Context
import androidx.room.*
import com.health.fitapp.data.model.FoodEntry
import com.health.fitapp.data.model.WaterEntry
import com.health.fitapp.data.model.WeightEntry
import kotlinx.coroutines.flow.Flow

// ── DAOs ──────────────────────────────────────────────────────────────────────

@Dao
interface WeightDao {
    @Query("SELECT * FROM weight_entries ORDER BY date DESC")
    fun getAll(): Flow<List<WeightEntry>>

    @Query("SELECT * FROM weight_entries ORDER BY date DESC LIMIT 7")
    fun getLast7(): Flow<List<WeightEntry>>

    @Insert
    suspend fun insert(entry: WeightEntry)

    @Delete
    suspend fun delete(entry: WeightEntry)
}

@Dao
interface FoodDao {
    @Query("SELECT * FROM food_entries WHERE date >= :startOfDay ORDER BY date DESC")
    fun getToday(startOfDay: Long): Flow<List<FoodEntry>>

    @Query("SELECT * FROM food_entries ORDER BY date DESC")
    fun getAll(): Flow<List<FoodEntry>>

    @Insert
    suspend fun insert(entry: FoodEntry)

    @Delete
    suspend fun delete(entry: FoodEntry)
}

@Dao
interface WaterDao {
    @Query("SELECT * FROM water_entries WHERE date >= :startOfDay ORDER BY date DESC LIMIT 1")
    fun getToday(startOfDay: Long): Flow<WaterEntry?>

    @Insert
    suspend fun insert(entry: WaterEntry)

    @Update
    suspend fun update(entry: WaterEntry)
}

// ── Database ──────────────────────────────────────────────────────────────────

@Database(
    entities = [WeightEntry::class, FoodEntry::class, WaterEntry::class],
    version = 1,
    exportSchema = false
)
abstract class FitDatabase : RoomDatabase() {
    abstract fun weightDao(): WeightDao
    abstract fun foodDao(): FoodDao
    abstract fun waterDao(): WaterDao

    companion object {
        @Volatile private var INSTANCE: FitDatabase? = null

        fun getInstance(context: Context): FitDatabase =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    FitDatabase::class.java,
                    "fit_database"
                ).build().also { INSTANCE = it }
            }
    }
}
