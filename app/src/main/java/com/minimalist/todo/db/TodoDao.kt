package com.minimalist.todo.db

import androidx.room.*
import io.reactivex.Flowable

@Dao
interface TodoDao {

    @Query("SELECT * FROM todo")
    fun getTodo() : Flowable<List<TodoEntity>>

    @Insert
    fun insertTodo(todo : TodoEntity)

    @Update(onConflict = OnConflictStrategy.REPLACE)
    fun updateTodo(todo : TodoEntity)

    @Delete
    fun deleteTodo(todo : TodoEntity)

}