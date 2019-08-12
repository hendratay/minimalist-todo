package com.minimalist.todo.widget

import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import android.widget.RemoteViewsService
import com.minimalist.todo.App
import com.minimalist.todo.R
import com.minimalist.todo.db.TodoDatabase
import com.minimalist.todo.model.Todo
import javax.inject.Inject

class TodoRemoteViewsFactory(val context: Context, val intent: Intent?): RemoteViewsService.RemoteViewsFactory {

    @Inject lateinit var database : TodoDatabase

    companion object {
        var widgetList: MutableList<Todo> = ArrayList()
    }

    override fun onCreate() {
        App.component.inject(this)
        updateWidgetListView()
    }

    override fun getLoadingView(): RemoteViews? {
        return null
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun onDataSetChanged() {
        updateWidgetListView()
    }

    override fun hasStableIds(): Boolean {
        return true
    }

    override fun getViewAt(position: Int): RemoteViews {
        val remoteViews = RemoteViews(context.packageName, R.layout.widget_item_todo)

        val fillIntent = Intent()
        fillIntent.putExtra(TodoWidget.EXTRA_ITEM, position)
        remoteViews.setOnClickFillInIntent(R.id.appwidget_list_item, fillIntent)
        remoteViews.setTextViewText(R.id.appwidget_list_item_text, widgetList[position].todoText.capitalize())

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

    private fun updateWidgetListView() {
/*
        database.todoDao().getTodo()
                .subscribe {
                    widgetList.clear()
                    it.forEach { widgetList.add(Todo(it.id, it.todo, it.done)) }
                }
*/
    }

}