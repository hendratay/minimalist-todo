package com.example.user.whattodo.db

import android.arch.persistence.room.Database
import android.arch.persistence.room.RoomDatabase
import android.arch.persistence.room.TypeConverter
import android.arch.persistence.room.TypeConverters

@Database(entities = arrayOf(TodoEntity::class), version = 1, exportSchema = false)
@TypeConverters(DateConverter::class)
abstract class TodoDatabase : RoomDatabase() {

    abstract fun todoDao() : TodoDao

}
