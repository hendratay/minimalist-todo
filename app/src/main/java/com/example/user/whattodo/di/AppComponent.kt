package com.example.user.whattodo.di

import com.example.user.whattodo.App
import com.example.user.whattodo.MainActivity
import com.example.user.whattodo.widget.TodoRemoteViewsFactory
import com.example.user.whattodo.widget.TodoWidget
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
