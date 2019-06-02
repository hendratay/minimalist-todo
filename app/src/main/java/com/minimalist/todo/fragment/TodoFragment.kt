package com.minimalist.todo.fragment

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.minimalist.todo.activity.MainActivity
import com.minimalist.todo.R
import com.minimalist.todo.db.TodoEntity
import com.minimalist.todo.model.Todo
import io.reactivex.Flowable
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import java.util.*

open class TodoFragment: Fragment() {

    private val backup: MutableList<TodoEntity> = ArrayList()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_todo,container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        setupRecyclerView()
    }

    open fun setupRecyclerView() {
    }

    fun getTodo(type: String): Flowable<List<TodoEntity>> {
        return (activity as MainActivity).database.todoDao().getTodo(type)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
    }

    fun insertTodo(todoEntity: TodoEntity)  {
        (activity as MainActivity).database.todoDao().insertTodo(todoEntity)
    }

    fun updateTodo(todo: Todo, done: Boolean) {
        val entity = TodoEntity(todo.todoId, todo.todoText, done, todo.type, todo.date)
        Single.fromCallable { (activity as MainActivity).database.todoDao().updateTodo(entity) }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe()
    }

    fun deleteTodo(selected: List<Int>, todoList: List<Todo>) {
        backup.clear()
        selected.forEach {
            val entity = TodoEntity(todoList[it].todoId, todoList[it].todoText, todoList[it].done, todoList[it].type, todoList[it].date)
            backup.add(entity)
            Single.fromCallable { (activity as MainActivity).database.todoDao().deleteTodo(entity) }
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe()
        }
    }

    fun undoDeleteTodo() {
        backup.forEach {
            (activity as MainActivity).database.todoDao().insertTodo(it)
        }
    }

    open fun destroyActionCallback() {
    }

}