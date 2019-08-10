package com.minimalist.todo.receiver

import android.app.NotificationChannel
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.minimalist.todo.activity.MainActivity
import com.minimalist.todo.R

class AlarmReceiver: BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        NotificationManagerCompat.from(context).notify(
                intent.getIntExtra("notificationId", 0),
                NotificationCompat.Builder(context, NotificationChannel.DEFAULT_CHANNEL_ID).apply {
                    priority = NotificationCompat.PRIORITY_DEFAULT
                    setSmallIcon(R.drawable.ic_add_black_24dp)
                    setContentTitle("Reminder")
                    setContentText(intent.getStringExtra("reminder"))
                    setDefaults(NotificationCompat.DEFAULT_ALL)
                    setContentIntent(PendingIntent.getActivity(context,
                            0,
                            Intent(context, MainActivity::class.java).putExtra("fragment", "reminderFragment"),
                            PendingIntent.FLAG_UPDATE_CURRENT))
                    setAutoCancel(true)
                }.build()
        )
    }

}