package com.minimalist.todo.di

import com.minimalist.todo.App
import com.minimalist.todo.activity.MainActivity
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = [AppModule::class])
interface AppComponent {
    fun inject(application: App)
    fun inject(mainActivity: MainActivity)
}
