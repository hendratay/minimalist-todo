package com.example.user.whattodo.di

import android.app.Application
import android.arch.persistence.room.Room
import android.content.Context
import com.example.user.whattodo.db.TodoDatabase
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class AppModule(private val application : Application) {

    @Provides
    @Singleton
    fun providesAppContext() = application

    @Provides
    @Singleton
    fun providesAppDatabase(context : Context) : TodoDatabase =
            Room.databaseBuilder(context, TodoDatabase::class.java, "todo.db").build()

    @Provides
    fun providesTodoDao(database : TodoDatabase) = database.todoDao()

}