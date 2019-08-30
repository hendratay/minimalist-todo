package com.minimalist.todo.widget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import androidx.room.Room
import com.minimalist.todo.R
import com.minimalist.todo.activity.MainActivity
import com.minimalist.todo.db.TodoDatabase
import com.minimalist.todo.db.TodoEntity
import io.reactivex.Single

class TodoWidget : AppWidgetProvider() {

    lateinit var database: TodoDatabase

    override fun onUpdate(context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray) {
        for (appWidgetId in appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId)
        }
        super.onUpdate(context, appWidgetManager, appWidgetIds)
    }

    override fun onReceive(context: Context?, intent: Intent?) {
        if (intent?.action == UPDATE_ACTION && context != null) {
            val action = intent.getStringExtra(ACTION)
            val todoId = intent.getLongExtra(EXTRA_ITEM, 0)

            database = Room.databaseBuilder(context, TodoDatabase::class.java, "todo.db").allowMainThreadQueries().build()
            if (action == TodoWidget.UPDATE_ACTION) {
                val todo = database.todoDao().getTodo(todoId).blockingGet()
                val entity = TodoEntity(todo.id, todo.todo, !todo.done)
                Single.fromCallable { database.todoDao().updateTodo(entity) }.subscribe()
            } else if (action == TodoWidget.DELETE_ACTION) {
                val todo = database.todoDao().getTodo(todoId).blockingGet()
                val entity = TodoEntity(todo.id, todo.todo, todo.done)
                Single.fromCallable { database.todoDao().deleteTodo(entity) }.subscribe()
            }

            val appWidgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID,
                    AppWidgetManager.INVALID_APPWIDGET_ID)
            AppWidgetManager.getInstance(context).notifyAppWidgetViewDataChanged(appWidgetId, R.id.appwidget_list_view)
/*
            // use this part if upper block doesn't work
            val appWidgetManager = AppWidgetManager.getInstance(context)
            val appWidgetIds = appWidgetManager.getAppWidgetIds(ComponentName(context,
                    TodoWidget::class.java))
            appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetIds, R.id.appwidget_list_view)
*/
        }
        super.onReceive(context, intent)
    }

    companion object {

        const val EXTRA_ITEM = "com.minimalist.todo.widget.EXTRA_ITEM"
        const val ACTION = "com.minimalist.todo.widget.ACTION"
        const val UPDATE_ACTION = "com.minimalist.todo.widget.UPDATE_ACTION"
        const val DELETE_ACTION = "com.minimalist.todo.widget.DELETE_ACTION"

        internal fun updateAppWidget(context: Context, appWidgetManager: AppWidgetManager,
                                     appWidgetId: Int) {

            val views = RemoteViews(context.packageName, R.layout.widget_todo)

            // ui empty view
            views.setEmptyView(R.id.appwidget_list_view, R.id.appwidget_empty_view)

            // listview adapter
            val intent = Intent(context, TodoRemoteViewsService::class.java)
            views.setRemoteAdapter(R.id.appwidget_list_view, intent)

            // listview item click
            val updateIntent = Intent(context, TodoWidget::class.java).apply {
                action = UPDATE_ACTION
                putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId)
            }
            val updatePendingIntent = PendingIntent.getBroadcast(context, 0, updateIntent,
                    PendingIntent.FLAG_UPDATE_CURRENT)
            views.setPendingIntentTemplate(R.id.appwidget_list_view, updatePendingIntent)

            // input edit text
            val inputIntent = Intent(context, MainActivity::class.java)
            val pendingIntent = PendingIntent.getActivity(context, 0, inputIntent, PendingIntent.FLAG_CANCEL_CURRENT)
            views.setOnClickPendingIntent(R.id.appwidget_edit_text, pendingIntent)

            appWidgetManager.updateAppWidget(appWidgetId, views)
        }
    }

}

