package com.minimalist.todo.widget

import android.content.Intent
import android.widget.RemoteViewsService

class TodoRemoteViewsService: RemoteViewsService() {

    override fun onGetViewFactory(intent: Intent?): RemoteViewsFactory {
        return TodoRemoteViewsFactory(applicationContext, intent ?: null)
    }

}