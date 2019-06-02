package com.minimalist.todo.di

import com.minimalist.todo.App
import com.minimalist.todo.activity.MainActivity
import com.minimalist.todo.widget.TodoRemoteViewsFactory
import com.minimalist.todo.widget.TodoWidget
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = arrayOf(AppModule::class))
interface AppComponent {
    fun inject(application: App)
    fun inject(mainActivity: MainActivity)
    fun inject(todoRemoteViewsFactory: TodoRemoteViewsFactory)
    fun inject(todoWidget: TodoWidget)
}
