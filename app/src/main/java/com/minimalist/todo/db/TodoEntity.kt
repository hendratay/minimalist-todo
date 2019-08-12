package com.minimalist.todo.db

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey

@Entity(tableName = "todo")
data class TodoEntity(@ColumnInfo(name = "id") @PrimaryKey(autoGenerate = true) val id : Long = 0,
                      @ColumnInfo(name = "todo_description") var todo : String,
                      @ColumnInfo(name = "done") var done : Boolean = false) {
    @Ignore
    constructor(todo : String, done : Boolean = false) : this(0, todo, done)

}
