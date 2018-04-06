package com.example.user.whattodo

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey

@Entity(tableName = "todo")
data class TodoEntity(@ColumnInfo(name = "id") @PrimaryKey(autoGenerate = true) val id : Long,
                      @ColumnInfo(name = "todo_description") var todo : String,
                      @ColumnInfo(name = "done") var done : Boolean = false) {
}
