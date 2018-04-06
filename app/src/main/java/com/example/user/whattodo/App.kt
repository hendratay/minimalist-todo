package com.example.user.whattodo

import android.app.Application
import com.example.user.whattodo.db.TodoDatabase
import com.example.user.whattodo.di.AppComponent
import com.example.user.whattodo.di.AppModule
import javax.inject.Inject

class App : Application() {

    companion object {
        @JvmStatic lateinit var component: AppComponent
    }

    @Inject
    lateinit var database : TodoDatabase

    override fun onCreate() {
        super.onCreate()
        component = DaggerAppComponent.builder().appModule(AppModule(this)).build()
        component.inject(this)
    }
}