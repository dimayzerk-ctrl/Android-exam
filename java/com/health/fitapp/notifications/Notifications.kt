package com.health.fitapp.notifications

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import com.health.fitapp.MainActivity

class WaterReminderReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        showWaterNotification(context)
    }
}

class BootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED) {
            // Re-schedule notifications after reboot if needed
        }
    }
}

fun createNotificationChannels(context: Context) {
    val nm = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    nm.createNotificationChannel(NotificationChannel(
        "water", "Напоминание о воде", NotificationManager.IMPORTANCE_DEFAULT
    ).apply { description = "Напоминает выпить воду" })

    nm.createNotificationChannel(NotificationChannel(
        "workout", "Тренировка", NotificationManager.IMPORTANCE_DEFAULT
    ).apply { description = "Напоминает о тренировке" })

    nm.createNotificationChannel(NotificationChannel(
        "tips", "Советы по упражнениям", NotificationManager.IMPORTANCE_LOW
    ).apply { description = "Советы по правильной технике" })
}

fun showWaterNotification(context: Context) {
    val nm = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    val intent = Intent(context, MainActivity::class.java).apply { flags = Intent.FLAG_ACTIVITY_NEW_TASK }
    val pi = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_IMMUTABLE)

    val messages = listOf(
        "💧 Время пить воду! Ваше тело скажет спасибо.",
        "💧 Вы уже выпили достаточно воды сегодня?",
        "💧 Не забудьте о воде — это важно для здоровья!",
        "💧 Стакан воды прямо сейчас?"
    )

    nm.notify(1001, NotificationCompat.Builder(context, "water")
        .setSmallIcon(android.R.drawable.ic_dialog_info)
        .setContentTitle("FitApp")
        .setContentText(messages.random())
        .setAutoCancel(true)
        .setContentIntent(pi)
        .build())
}

fun showWorkoutNotification(context: Context, workoutName: String = "Тренировка") {
    val nm = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    val intent = Intent(context, MainActivity::class.java)
    val pi = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_IMMUTABLE)

    nm.notify(1002, NotificationCompat.Builder(context, "workout")
        .setSmallIcon(android.R.drawable.ic_dialog_info)
        .setContentTitle("Время тренироваться! 💪")
        .setContentText("Сегодня: $workoutName. Ты готов?")
        .setAutoCancel(true)
        .setContentIntent(pi)
        .build())
}
