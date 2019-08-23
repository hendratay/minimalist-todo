package com.minimalist.todo.db

import androidx.room.*
import io.reactivex.Flowable
import io.reactivex.Single

@Dao
interface TodoDao {

    @Query("SELECT * FROM todo")
    fun getTodo() : Flowable<List<TodoEntity>>

    @Query("SELECT * FROM todo WHERE id = :todoId")
    fun getTodo(todoId: Long) : Single<TodoEntity>

    @Insert
    fun insertTodo(todo : TodoEntity)

    @Update(onConflict = OnConflictStrategy.REPLACE)
    fun updateTodo(todo : TodoEntity)

    @Delete
    fun deleteTodo(todo : TodoEntity)

}