package com.minimalist.todo.widget

import android.content.Context
import android.content.Intent
import android.graphics.Paint
import android.view.View
import android.widget.RemoteViews
import android.widget.RemoteViewsService
import androidx.room.Room
import com.minimalist.todo.R
import com.minimalist.todo.db.TodoDatabase
import com.minimalist.todo.db.TodoEntity
import com.minimalist.todo.model.Todo

class TodoRemoteViewsFactory(private val context: Context, val intent: Intent?) : RemoteViewsService.RemoteViewsFactory {

    lateinit var database: TodoDatabase

    companion object {
        var widgetList: MutableList<Todo> = ArrayList()
    }

    override fun onCreate() {
        database = Room.databaseBuilder(context, TodoDatabase::class.java, "todo.db").allowMainThreadQueries().build()
    }

    override fun getLoadingView(): RemoteViews? {
        return null
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun onDataSetChanged() {
        val todo = database.todoDao().getTodo().blockingFirst() as MutableList<TodoEntity>
        widgetList.clear()
        todo.forEach{ widgetList.add(Todo(it.id, it.todo, it.done))}
    }

    override fun hasStableIds(): Boolean {
        return true
    }

    override fun getViewAt(position: Int): RemoteViews {
        val remoteViews = RemoteViews(context.packageName, R.layout.widget_item_todo)

        // update
        val updateIntent = Intent()
        updateIntent.putExtra(TodoWidget.EXTRA_ITEM, position)
        updateIntent.putExtra(TodoWidget.ACTION, TodoWidget.UPDATE_ACTION)
        remoteViews.setOnClickFillInIntent(R.id.appwidget_list_item_check_box, updateIntent)
        remoteViews.setOnClickFillInIntent(R.id.appwidget_list_item_text, updateIntent)
        // delete
        val deleteIntent = Intent()
        deleteIntent.putExtra(TodoWidget.EXTRA_ITEM, position)
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
            remoteViews.setViewVisibility(R.id.appwidget_list_item_delete, View.INVISIBLE)
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