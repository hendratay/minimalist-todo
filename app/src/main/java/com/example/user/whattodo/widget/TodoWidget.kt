package com.example.user.whattodo.widget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import com.example.user.whattodo.activity.MainActivity
import com.example.user.whattodo.R
import com.example.user.whattodo.App
import com.example.user.whattodo.db.TodoDatabase
import com.example.user.whattodo.db.TodoEntity
import io.reactivex.Single
import javax.inject.Inject

class TodoWidget : AppWidgetProvider() {

    @Inject lateinit var database : TodoDatabase

    override fun onUpdate(context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray) {
        for (appWidgetId in appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId)
        }
    }

    override fun onEnabled(context: Context) {
    }

    override fun onDisabled(context: Context) {
    }

    override fun onReceive(context: Context?, intent: Intent?) {
        App.component.inject(this)
        if (intent?.action == UPDATE_ACTION) {
            val viewIndex = intent.getIntExtra(EXTRA_ITEM, 0)
            val todo = TodoRemoteViewsFactory.widgetList[viewIndex]
            val entity = TodoEntity(todo.todoId, todo.todoText, true, todo.type, todo.date)
            Single.fromCallable { database.todoDao().updateTodo(entity) }.subscribe()
            TodoRemoteViewsFactory.widgetList.removeAt(viewIndex)

            // notify widget adapter
            val appWidgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID,
                    AppWidgetManager.INVALID_APPWIDGET_ID)
            AppWidgetManager.getInstance(context).notifyAppWidgetViewDataChanged(appWidgetId, R.id.appwidget_list_view);
        }
        super.onReceive(context, intent)
    }

    override fun onDeleted(context: Context?, appWidgetIds: IntArray?) {
    }

    companion object {

        const val EXTRA_ITEM = "com.example.user.whattodo.widget.EXTRA_ITEM"
        const val UPDATE_ACTION = "com.example.user.whattodo.widget.UPDATE_ACTION"

        internal fun updateAppWidget(context: Context, appWidgetManager: AppWidgetManager,
                                     appWidgetId: Int) {

            val views = RemoteViews(context.packageName, R.layout.widget_todo)

            val taskIntent = Intent(context, MainActivity::class.java)
            val pendingIntent = PendingIntent.getActivity(context, 0, taskIntent, PendingIntent.FLAG_CANCEL_CURRENT)
            views.setOnClickPendingIntent(R.id.appwidget_button, pendingIntent)

            val intent = Intent(context, TodoRemoteViewsService::class.java)
            views.setRemoteAdapter(R.id.appwidget_list_view, intent)

            val updateIntent = Intent(context, TodoWidget::class.java)
            updateIntent.action = TodoWidget.UPDATE_ACTION
            updateIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId)
            val toastPendingIntent = PendingIntent.getBroadcast(context, 0, updateIntent,
                    PendingIntent.FLAG_UPDATE_CURRENT)
            views.setPendingIntentTemplate(R.id.appwidget_list_view, toastPendingIntent)

            appWidgetManager.updateAppWidget(appWidgetId, views)

        }

    }

}

