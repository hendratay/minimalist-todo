package com.minimalist.todo.di

import android.app.Application
import android.arch.persistence.room.Room
import android.content.Context
import com.minimalist.todo.db.TodoDatabase
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class AppModule(private val application : Application) {

    @Provides
    @Singleton
    fun providesAppContext(): Context = application

    @Provides
    @Singleton
    fun providesAppDatabase(context : Context) : TodoDatabase =
            Room.databaseBuilder(context, TodoDatabase::class.java, "todo.db").allowMainThreadQueries().build()

}