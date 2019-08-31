package com.minimalist.todo.widget

import android.content.Context
import android.content.Intent
import android.graphics.Paint
import android.view.View
import android.widget.RemoteViews
import android.widget.RemoteViewsService
import com.minimalist.todo.R
import com.minimalist.todo.db.TodoDatabase
import com.minimalist.todo.db.TodoEntity
import com.minimalist.todo.model.Todo

class TodoRemoteViewsFactory(private val context: Context, val intent: Intent?) : RemoteViewsService.RemoteViewsFactory {

    private var database: TodoDatabase? = null
    private var widgetList: MutableList<Todo> = ArrayList()

    override fun onCreate() {
        database = TodoDatabase.getDatabase(context)
    }

    override fun getLoadingView(): RemoteViews? {
        return null
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun onDataSetChanged() {
        widgetList.clear()
        if (database != null) {
            val todo = database!!.todoDao().getTodo().blockingFirst() as MutableList<TodoEntity>
            todo.forEach { widgetList.add(Todo(it.id, it.todo, it.done)) }
        }
    }

    override fun hasStableIds(): Boolean {
        return true
    }

    override fun getViewAt(position: Int): RemoteViews {
        val remoteViews = RemoteViews(context.packageName, R.layout.widget_item_todo)

        // update
        val updateIntent = Intent()
        updateIntent.putExtra(TodoWidget.EXTRA_ITEM, widgetList[position].todoId)
        updateIntent.putExtra(TodoWidget.ACTION, TodoWidget.UPDATE_ACTION)
        remoteViews.setOnClickFillInIntent(R.id.appwidget_list_item_check_box, updateIntent)
        remoteViews.setOnClickFillInIntent(R.id.appwidget_list_item_text, updateIntent)
        // delete
        val deleteIntent = Intent()
        deleteIntent.putExtra(TodoWidget.EXTRA_ITEM, widgetList[position].todoId)
        deleteIntent.putExtra(TodoWidget.ACTION, TodoWidget.DELETE_ACTION)
        remoteViews.setOnClickFillInIntent(R.id.appwidget_list_item_delete, deleteIntent)
        // ui
        remoteViews.setTextViewText(R.id.appwidget_list_item_text, widgetList[position].todoText)
        if (widgetList[position].done) {
            remoteViews.setImageViewResource(R.id.appwidget_list_item_check_box, R.drawable.ic_check_box_green_24dp)
            remoteViews.setInt(R.id.appwidget_list_item_text, "setPaintFlags", Paint.STRIKE_THRU_TEXT_FLAG)
            remoteViews.setViewVisibility(R.id.appwidget_list_item_delete, View.VISIBLE)
        } else {
            remoteViews.setImageViewResource(R.id.appwidget_list_item_check_box, R.drawable.ic_check_box_outline_blank_green_24dp)
            remoteViews.setInt(R.id.appwidget_list_item_text, "setPaintFlags", 0)
            remoteViews.setViewVisibility(R.id.appwidget_list_item_delete, View.GONE)
        }

        return remoteViews
    }

    override fun getCount(): Int {
        return widgetList.size
    }

    override fun getViewTypeCount(): Int {
        return 1
    }

    override fun onDestroy() {
        widgetList.clear()
    }

}