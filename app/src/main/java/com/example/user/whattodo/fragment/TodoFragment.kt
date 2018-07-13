package com.example.user.whattodo.fragment

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.user.whattodo.MainActivity
import com.example.user.whattodo.R
import com.example.user.whattodo.db.TodoEntity
import com.example.user.whattodo.model.Todo
import io.reactivex.Flowable
import io.reactivex.Observable
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

    fun getTodoType(): Flowable<List<String>> {
        return (activity as MainActivity).database.todoDao().getTodoType()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
    }

    fun getTodoCount(type: String): Flowable<Int> {
        return (activity as MainActivity).database.todoDao().getTodoCount(type)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
    }

    fun getTodoDoneCount(type: String): Flowable<Int> {
        return (activity as MainActivity).database.todoDao().getDoneTodoCount(type)
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