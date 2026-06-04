package com.health.fitapp.viewmodel

import android.app.Application
import androidx.lifecycle.*
import com.health.fitapp.data.model.*
import com.health.fitapp.data.repository.FitRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class FitViewModel(app: Application) : AndroidViewModel(app) {

    val repo = FitRepository(app)

    // ── Weight ───────────────────────────────────────────────────────────────
    val last7Weights = repo.last7Weights.stateIn(viewModelScope, SharingStarted.Lazily, emptyList())
    val allWeights   = repo.allWeights.stateIn(viewModelScope, SharingStarted.Lazily, emptyList())
    val weightGoal   = repo.weightGoal.stateIn(viewModelScope, SharingStarted.Lazily, 70f)

    fun addWeight(kg: Float) = viewModelScope.launch { repo.addWeight(kg) }
    fun deleteWeight(e: WeightEntry) = viewModelScope.launch { repo.deleteWeight(e) }

    // ── Food ─────────────────────────────────────────────────────────────────
    val todayFood    = repo.getTodayFood().stateIn(viewModelScope, SharingStarted.Lazily, emptyList())
    val calorieGoal  = repo.calorieGoal.stateIn(viewModelScope, SharingStarted.Lazily, 2000)

    val todayCalories = todayFood.map { list -> list.sumOf { it.calories } }
        .stateIn(viewModelScope, SharingStarted.Lazily, 0)
    val todayProtein = todayFood.map { list -> list.sumOf { it.protein.toDouble() }.toFloat() }
        .stateIn(viewModelScope, SharingStarted.Lazily, 0f)
    val todayFat     = todayFood.map { list -> list.sumOf { it.fat.toDouble() }.toFloat() }
        .stateIn(viewModelScope, SharingStarted.Lazily, 0f)
    val todayCarbs   = todayFood.map { list -> list.sumOf { it.carbs.toDouble() }.toFloat() }
        .stateIn(viewModelScope, SharingStarted.Lazily, 0f)

    fun addFood(entry: FoodEntry) = viewModelScope.launch { repo.addFood(entry) }
    fun deleteFood(e: FoodEntry)  = viewModelScope.launch { repo.deleteFood(e) }

    // ── Water ─────────────────────────────────────────────────────────────────
    val todayWater = repo.getTodayWater().stateIn(viewModelScope, SharingStarted.Lazily, null)
    val waterGlasses = todayWater.map { it?.glasses ?: 0 }
        .stateIn(viewModelScope, SharingStarted.Lazily, 0)

    fun setWaterGlasses(n: Int) = viewModelScope.launch { repo.setWaterGlasses(n) }

    // ── Settings ─────────────────────────────────────────────────────────────
    val waterEnabled   = repo.waterEnabled.stateIn(viewModelScope, SharingStarted.Lazily, true)
    val waterInterval  = repo.waterInterval.stateIn(viewModelScope, SharingStarted.Lazily, 2)
    val workoutEnabled = repo.workoutEnabled.stateIn(viewModelScope, SharingStarted.Lazily, true)
    val workoutHour    = repo.workoutHour.stateIn(viewModelScope, SharingStarted.Lazily, 8)
    val workoutMinute  = repo.workoutMinute.stateIn(viewModelScope, SharingStarted.Lazily, 0)

    fun setWaterEnabled(v: Boolean)   = viewModelScope.launch { repo.setWaterEnabled(v) }
    fun setWorkoutEnabled(v: Boolean) = viewModelScope.launch { repo.setWorkoutEnabled(v) }
    fun setCalorieGoal(v: Int)        = viewModelScope.launch { repo.setCalorieGoal(v) }
    fun setWeightGoal(v: Float)       = viewModelScope.launch { repo.setWeightGoal(v) }

    // ── Static workout data ───────────────────────────────────────────────────
    val workoutPlans: List<WorkoutPlan> = listOf(
        WorkoutPlan("strength","Силовая тренировка","💪",45,0xFFFF6B35,0.6f, listOf(
            Exercise("Приседания","4 × 12 повт.","Спина прямая, колени не выходят за носки"),
            Exercise("Жим штанги лёжа","3 × 10 повт.","Лопатки сведены, гриф опускается к нижней части груди"),
            Exercise("Тяга верхнего блока","3 × 12 повт.","Тяни к поясу, локти ведут движение"),
            Exercise("Планка","3 × 45 сек","Тело — прямая линия, не опускай таз"),
            Exercise("Становая тяга","3 × 8 повт.","Спина прямая, толкай ногами — не тяни спиной")
        )),
        WorkoutPlan("cardio","Кардио","🏃",30,0xFFFF375F,0.25f, listOf(
            Exercise("Бег на месте","5 минут","Отталкивайся носками, руки согнуты под 90°"),
            Exercise("Прыжки со скакалкой","3 × 2 мин","Прыгай на носках, минимальная высота"),
            Exercise("Берпи","3 × 10 повт.","Полный диапазон: грудь касается пола"),
            Exercise("Велосипед лёжа","3 × 20 повт.","Тяни локоть к противоположному колену")
        )),
        WorkoutPlan("stretch","Растяжка","🧘",20,0xFFBF5AF2,1.0f, listOf(
            Exercise("Наклон к ногам","30 сек × 3","Колени прямые, тянемся плавно"),
            Exercise("Поза кошки-коровы","10 повторений","Синхронизируй с дыханием"),
            Exercise("Растяжка квадрицепса","30 сек на ногу","Держи равновесие, не сутулься"),
            Exercise("Плечи назад","20 круговых","Максимальная амплитуда"),
            Exercise("Растяжка шеи","30 сек × 4","Медленно, без рывков"),
            Exercise("Поза голубя","45 сек на сторону","Дыши ровно и расслабляйся")
        )),
        WorkoutPlan("hiit","HIIT","⚡",25,0xFF30D158,0.0f, listOf(
            Exercise("Джампинг-джекс","30 сек","Руки поднимаются выше головы"),
            Exercise("Берпи","30 сек","Полный темп, без пауз"),
            Exercise("Приседания с прыжком","30 сек","Мягкое приземление на носки"),
            Exercise("Mountain climbers","30 сек","Кор в постоянном напряжении"),
            Exercise("Высокое поднимание колен","30 сек","Бедро параллельно полу"),
            Exercise("Отжимания","30 сек","Локти под углом 45° к телу"),
            Exercise("Боковые прыжки","30 сек","Прыгай через воображаемую линию"),
            Exercise("Спринт на месте","30 сек","Максимальная скорость ног")
        ))
    )

    // ── Static breathing data ─────────────────────────────────────────────────
    val breathTechniques: List<BreathTechnique> = listOf(
        BreathTechnique("478","4-7-8","Снятие тревоги и улучшение сна","🌙",0xFF0A84FF, listOf(
            BreathPhase("Вдох",4,true),
            BreathPhase("Задержка",7,true),
            BreathPhase("Выдох",8,false)
        )),
        BreathTechnique("box","Квадратное","Концентрация и продуктивность","📦",0xFF30D158, listOf(
            BreathPhase("Вдох",4,true),
            BreathPhase("Задержка",4,true),
            BreathPhase("Выдох",4,false),
            BreathPhase("Пауза",4,false)
        )),
        BreathTechnique("wim","Вим Хоф","Энергия и иммунитет","🔥",0xFFFF6B35, listOf(
            BreathPhase("Вдох",2,true),
            BreathPhase("Выдох",2,false)
        ))
    )
}
