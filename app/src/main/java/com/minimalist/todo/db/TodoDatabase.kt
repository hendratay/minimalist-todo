package com.minimalist.todo.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(entities = arrayOf(TodoEntity::class), version = 1, exportSchema = false)
@TypeConverters(DateConverter::class)
abstract class TodoDatabase : RoomDatabase() {

    abstract fun todoDao() : TodoDao

}
