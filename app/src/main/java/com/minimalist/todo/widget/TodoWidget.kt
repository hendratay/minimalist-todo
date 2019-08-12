package com.minimalist.todo.widget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import com.minimalist.todo.activity.MainActivity
import com.minimalist.todo.R
import com.minimalist.todo.App
import com.minimalist.todo.db.TodoDatabase
import com.minimalist.todo.db.TodoEntity
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
            val entity = TodoEntity(todo.todoId, todo.todoText, true)
            Single.fromCallable { database.todoDao().updateTodo(entity) }.subscribe()
            TodoRemoteViewsFactory.widgetList.removeAt(viewIndex)

            // notify widget adapter
            val appWidgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID,
                    AppWidgetManager.INVALID_APPWIDGET_ID)
            AppWidgetManager.getInstance(context).notifyAppWidgetViewDataChanged(appWidgetId, R.id.appwidget_list_view)
        }
        super.onReceive(context, intent)
    }

    override fun onDeleted(context: Context?, appWidgetIds: IntArray?) {
    }

    companion object {

        const val EXTRA_ITEM = "com.minimalist.todo.widget.EXTRA_ITEM"
        const val UPDATE_ACTION = "com.minimalist.todo.widget.UPDATE_ACTION"

        internal fun updateAppWidget(context: Context, appWidgetManager: AppWidgetManager,
                                     appWidgetId: Int) {

            val views = RemoteViews(context.packageName, R.layout.widget_todo)

            val taskIntent = Intent(context, MainActivity::class.java)
            val pendingIntent = PendingIntent.getActivity(context, 0, taskIntent, PendingIntent.FLAG_CANCEL_CURRENT)
            views.setOnClickPendingIntent(R.id.appwidget_button, pendingIntent)

            val intent = Intent(context, TodoRemoteViewsService::class.java)
            views.setRemoteAdapter(R.id.appwidget_list_view, intent)

            val updateIntent = Intent(context, TodoWidget::class.java)
            updateIntent.action = UPDATE_ACTION
            updateIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId)
            val toastPendingIntent = PendingIntent.getBroadcast(context, 0, updateIntent,
                    PendingIntent.FLAG_UPDATE_CURRENT)
            views.setPendingIntentTemplate(R.id.appwidget_list_view, toastPendingIntent)

            appWidgetManager.updateAppWidget(appWidgetId, views)

        }

    }

}

