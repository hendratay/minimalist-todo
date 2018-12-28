package com.example.user.whattodo.fragment

import android.support.v7.widget.LinearLayoutManager
import com.example.user.whattodo.adapter.HomeAdapter
import kotlinx.android.synthetic.main.fragment_todo.*
import io.reactivex.disposables.CompositeDisposable

class HomeFragment: TodoFragment() {

    private lateinit var adapter: HomeAdapter
    private val todoType: MutableList<String> = ArrayList()
    private val todoCount: MutableList<Int> = ArrayList()
    private var todoDoneCount: MutableList<Int> = ArrayList()

    var compositeDisposable = CompositeDisposable()

    override fun setupRecyclerView() {
        adapter = HomeAdapter(todoType, todoCount, todoDoneCount)
        recycler_view.layoutManager = LinearLayoutManager(requireContext())
        recycler_view.adapter = adapter
    }

    override fun onStart() {
        super.onStart()
        getType()
    }

    override fun onDestroy() {
        super.onDestroy()
        compositeDisposable.dispose()
    }

    private fun getType() {
        val typeDisposable = getTodoType()
                .subscribe { todo ->
                    todoType.clear()
                    todo.forEach { todoType.add(it) }
                    adapter.notifyDataSetChanged()
                    todoCount()
                    doneTodoCount()
                }
        compositeDisposable.add(typeDisposable)
    }

    private fun todoCount() {
        for(allType in 0..(todoType.size - 1)) {
            val countDisposable = getTodoCount(todoType[allType])
                    .subscribe {
                        todoCount.add(allType, it)
                        adapter.notifyDataSetChanged()
                    }
            compositeDisposable.add(countDisposable)
        }
    }

    private fun doneTodoCount() {
        for(allType in 0..(todoType.size - 1)) {
            val doneCountDisposable = getTodoDoneCount(todoType[allType])
                    .subscribe {
                        todoDoneCount.add(allType, it)
                        adapter.notifyDataSetChanged()
                    }
            compositeDisposable.addAll(doneCountDisposable)
        }
    }

}