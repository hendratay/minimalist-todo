package com.example.user.whattodo.receiver

import android.app.NotificationChannel
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.support.v4.app.NotificationCompat
import android.support.v4.app.NotificationManagerCompat
import com.example.user.whattodo.MainActivity
import com.example.user.whattodo.R

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