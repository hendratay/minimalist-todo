package com.example.user.whattodo

import android.app.Application
import com.example.user.whattodo.db.TodoDatabase
import com.example.user.whattodo.di.AppComponent
import com.example.user.whattodo.di.AppModule
import com.example.user.whattodo.di.DaggerAppComponent
import javax.inject.Inject

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