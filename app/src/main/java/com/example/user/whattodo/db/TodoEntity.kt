package com.example.user.whattodo.db

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Entity
import android.arch.persistence.room.Ignore
import android.arch.persistence.room.PrimaryKey

@Entity(tableName = "todo")
data class TodoEntity(@ColumnInfo(name = "id") @PrimaryKey(autoGenerate = true) val id : Long = 0,
                      @ColumnInfo(name = "todo_description") var todo : String,
                      @ColumnInfo(name = "done") var done : Boolean = false) {
    @Ignore
    constructor(todo : String, done : Boolean = false) : this(0, todo, done)
}
