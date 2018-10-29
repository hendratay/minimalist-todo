package com.example.user.whattodo.db

import android.arch.persistence.room.*
import io.reactivex.Flowable

@Dao
interface TodoDao {

    @Query("select * from todo where done == 0")
    fun getAllUndoneTodo() : Flowable<List<TodoEntity>>

    @Query("select * from todo where type == :type ")
    fun getTodo(type: String) : Flowable<List<TodoEntity>>

    @Insert
    fun insertTodo(todo : TodoEntity)

    @Update(onConflict = OnConflictStrategy.REPLACE)
    fun updateTodo(todo : TodoEntity)

    @Delete
    fun deleteTodo(todo : TodoEntity)

    @Query("select DISTINCT type from todo")
    fun getTodoType() : Flowable<List<String>>

    @Query("select COUNT(type) from todo where type == :type")
    fun getTodoCount(type: String) : Flowable<Int>

    @Query("select COUNT(type) from todo where type == :type AND done == 1")
    fun getDoneTodoCount(type: String) : Flowable<Int>

}