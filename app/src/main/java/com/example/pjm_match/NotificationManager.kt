package com.example.pjm_match

import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.os.Build
import androidx.core.app.NotificationCompat
import java.util.Calendar

object SettingsManager {
    private const val PREFS_NAME = "pjm_match_settings_prefs"
    const val KEY_REMINDER_ENABLED = "key_reminder_enabled"
    const val KEY_REMINDER_HOUR = "key_reminder_hour"
    const val KEY_REMINDER_MINUTE = "key_reminder_minute"
    const val KEY_SOUND_ENABLED = "key_sound_enabled"
    const val KEY_VIBRATION_ENABLED = "key_vibration_enabled"
    const val KEY_MESSAGES_ENABLED = "key_messages_enabled"

    fun isReminderEnabled(context: Context): Boolean = getPrefs(context).getBoolean(KEY_REMINDER_ENABLED, true)
    fun getReminderHour(context: Context): Int = getPrefs(context).getInt(KEY_REMINDER_HOUR, 20)
    fun getReminderMinute(context: Context): Int = getPrefs(context).getInt(KEY_REMINDER_MINUTE, 0)
    fun isSoundEnabled(context: Context): Boolean = getPrefs(context).getBoolean(KEY_SOUND_ENABLED, true)
    fun isVibrationEnabled(context: Context): Boolean = getPrefs(context).getBoolean(KEY_VIBRATION_ENABLED, true)
    fun isMessagesEnabled(context: Context): Boolean = getPrefs(context).getBoolean(KEY_MESSAGES_ENABLED, true)

    fun setReminderEnabled(context: Context, enabled: Boolean) = getPrefs(context).edit().putBoolean(KEY_REMINDER_ENABLED, enabled).apply()
    fun setReminderTime(context: Context, hour: Int, minute: Int) {
        getPrefs(context).edit()
            .putInt(KEY_REMINDER_HOUR, hour)
            .putInt(KEY_REMINDER_MINUTE, minute)
            .apply()
    }
    fun setSoundEnabled(context: Context, enabled: Boolean) = getPrefs(context).edit().putBoolean(KEY_SOUND_ENABLED, enabled).apply()
    fun setVibrationEnabled(context: Context, enabled: Boolean) = getPrefs(context).edit().putBoolean(KEY_VIBRATION_ENABLED, enabled).apply()
    fun setMessagesEnabled(context: Context, enabled: Boolean) = getPrefs(context).edit().putBoolean(KEY_MESSAGES_ENABLED, enabled).apply()

    private fun getPrefs(context: Context) = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    fun scheduleDailyReminder(context: Context) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, ReminderReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        if (!isReminderEnabled(context)) {
            alarmManager.cancel(pendingIntent)
            return
        }

        val calendar = Calendar.getInstance().apply {
            timeInMillis = System.currentTimeMillis()
            set(Calendar.HOUR_OF_DAY, getReminderHour(context))
            set(Calendar.MINUTE, getReminderMinute(context))
            set(Calendar.SECOND, 0)
            if (timeInMillis <= System.currentTimeMillis()) {
                add(Calendar.DAY_OF_YEAR, 1)
            }
        }

        try {
            alarmManager.setRepeating(
                AlarmManager.RTC_WAKEUP,
                calendar.timeInMillis,
                AlarmManager.INTERVAL_DAY,
                pendingIntent
            )
        } catch (e: SecurityException) {
            e.printStackTrace()
        }
    }
}

class ReminderReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as android.app.NotificationManager
        val channelId = "pjm_match_daily_reminder"

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Przypomnienia o nauce",
                android.app.NotificationManager.IMPORTANCE_DEFAULT
            )
            notificationManager.createNotificationChannel(channel)
        }

        val launchIntent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent = PendingIntent.getActivity(
            context,
            0,
            launchIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        val soundEnabled = SettingsManager.isSoundEnabled(context)
        val vibrationEnabled = SettingsManager.isVibrationEnabled(context)

        val builder = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(android.R.drawable.ic_dialog_info) // TODO: use real icon
            .setContentTitle("Czas na naukę!")
            .setContentText("Kontynuuj swoją serię i osiągnij dzisiejszy cel w PJM-Match.")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)

        if (soundEnabled) {
            builder.setSound(soundUri)
        }
        if (vibrationEnabled) {
            builder.setVibrate(longArrayOf(0, 250, 250, 250))
        }

        notificationManager.notify(1001, builder.build())
    }
}