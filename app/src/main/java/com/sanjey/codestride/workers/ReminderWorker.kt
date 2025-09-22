package com.sanjey.codestride.workers

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.annotation.RequiresPermission
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.sanjey.codestride.R

class ReminderWorker(
    context: Context,
    params: WorkerParameters
) : Worker(context, params) {

    @RequiresPermission(Manifest.permission.POST_NOTIFICATIONS)
    override fun doWork(): Result {
        showNotification()
        return Result.success()
    }

    @RequiresPermission(Manifest.permission.POST_NOTIFICATIONS)
    private fun showNotification() {
        val messages = listOf(
            "📚 Still not learned today? Your streak is crying.",
            "🧠 Pro tip: Learning beats scrolling!",
            "👀 Hey genius, your modules miss you!",
            "🚨 Procrastination alert! Tap now!",
            "🎯 You’re one tap away from glory.",
            "🛑 Stop. Learn. Dominate.",
            "😴 You sleep, your dreams code. Wake up!",
            "🔥 Your streak wants you back.",
            "🤖 Even CodeBot is waiting.",
            "📅 Today’s best plan: Learn a module.",
            "🏆 Earn that badge!",
            "🎮 Learning = XP gain. Go!",
            "⚙️ Compile your brain, not just your code.",
            "🌞 Learn before the sun sets!",
            "📈 No progress today? Let’s fix that.",
            "🎓 Greatness isn’t paused. Neither should you be.",
            "🕒 Reminder: 10 minutes of code = progress.",
            "🎶 Learning sounds better than guilt.",
            "🔁 This notification will haunt you daily 😈",
            "🚀 Even rockets start with small sparks. Tap!"
        )

        val message = messages.random()
        val channelId = "reminder_channel"

        val channel = NotificationChannel(
            channelId,
            "Daily Reminder",
            NotificationManager.IMPORTANCE_DEFAULT
        )
        val manager = applicationContext.getSystemService(NotificationManager::class.java)
        manager?.createNotificationChannel(channel)

        val intent = applicationContext.packageManager
            .getLaunchIntentForPackage(applicationContext.packageName)
        intent?.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)

        val pendingIntent = PendingIntent.getActivity(
            applicationContext,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(applicationContext, channelId)
            .setContentTitle("Time to Learn!")
            .setContentText(message)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .build()

        NotificationManagerCompat.from(applicationContext).notify(1, notification)

    }


}
