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
    }

    override fun onDestroy() {
        super.onDestroy()
        compositeDisposable.dispose()
    }

}