package com.minimalist.todo

import android.app.Application
import com.minimalist.todo.di.AppComponent
import com.minimalist.todo.di.AppModule
import com.minimalist.todo.di.DaggerAppComponent

class App : Application() {

    companion object {
        lateinit var component: AppComponent
    }

    override fun onCreate() {
        super.onCreate()
        component = DaggerAppComponent.builder().appModule(AppModule(this)).build()
        component.inject(this)
    }
}