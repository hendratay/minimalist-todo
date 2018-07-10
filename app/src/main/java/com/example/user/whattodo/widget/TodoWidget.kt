package com.example.user.whattodo.widget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import com.example.user.whattodo.MainActivity
import com.example.user.whattodo.R

class TodoWidget : AppWidgetProvider() {

    override fun onUpdate(context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray) {
        for (appWidgetId in appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId)
        }
    }

    override fun onEnabled(context: Context) {
    }

    override fun onDisabled(context: Context) {
    }

    companion object {
        internal fun updateAppWidget(context: Context, appWidgetManager: AppWidgetManager,
                                     appWidgetId: Int) {

            val views = RemoteViews(context.packageName, R.layout.todo_widget)

            val taskIntent = Intent(context, MainActivity::class.java)
            val pendingIntent = PendingIntent.getActivity(context, 0, taskIntent, PendingIntent.FLAG_CANCEL_CURRENT)
            views.setOnClickPendingIntent(R.id.appwidget_button, pendingIntent);

            val intent = Intent(context, TodoRemoteViewsService::class.java)
            views.setRemoteAdapter(R.id.appwidget_list_view, intent)

            appWidgetManager.updateAppWidget(appWidgetId, views)

        }
    }

}

