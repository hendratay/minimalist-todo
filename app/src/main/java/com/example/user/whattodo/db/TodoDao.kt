package com.example.user.whattodo.db

import android.arch.persistence.room.*
import io.reactivex.Flowable

@Dao
interface TodoDao {

    @Query("select * from todo where done = 0")
    fun getTodo() : Flowable<List<TodoEntity>>

    @Query("select * from todo where done = 1")
    fun getDoneTodo() : Flowable<List<TodoEntity>>

    @Insert
    fun insertTodo(todo : TodoEntity)

    @Update(onConflict = OnConflictStrategy.REPLACE)
    fun updateTodo(todo : TodoEntity)

    @Delete
    fun deleteTodo(todo : TodoEntity)
}