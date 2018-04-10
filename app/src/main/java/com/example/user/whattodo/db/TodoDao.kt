package com.example.user.whattodo.db

import android.arch.persistence.room.*
import io.reactivex.Flowable

@Dao
interface TodoDao {

    @Query("select * from todo")
    fun getAllTodo() : Flowable<List<TodoEntity>>

    @Insert
    fun insertTodo(todo : TodoEntity)

    @Update
    fun updateTodo(todo : TodoEntity)

    @Delete
    fun deleteTodo(todo : TodoEntity)
}