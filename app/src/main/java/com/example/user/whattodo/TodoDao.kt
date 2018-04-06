package com.example.user.whattodo

import android.arch.persistence.room.*

@Dao
interface TodoDao {

    @Query("select * from todo")
    fun getAllTodo() : List<TodoEntity>

    @Insert
    fun insertTodo(todo : TodoEntity)

    @Update
    fun updateTodo(todo : TodoEntity)

    @Delete
    fun deleteTodo(todo : TodoEntity)
}