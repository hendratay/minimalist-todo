package com.example.user.whattodo.fragment

import android.support.v7.widget.LinearLayoutManager
import com.example.user.whattodo.adapter.HomeAdapter
import com.example.user.whattodo.model.*
import kotlinx.android.synthetic.main.fragment_todo.*

class HomeFragment: TodoFragment() {

    private lateinit var adapter: HomeAdapter
    private var consolidatedList: MutableList<ListItem> = ArrayList()

    override fun onStart() {
        super.onStart()
        getTodos()
    }

    override fun setupRecyclerView() {
        recycler_view.layoutManager = LinearLayoutManager(activity)
        adapter = HomeAdapter(consolidatedList)
        recycler_view.adapter = adapter
    }

    private fun getTodos() {
        getAllTodo()
                .subscribe {
                    consolidatedList.clear()
                    val todoList: MutableList<Todo> = ArrayList()
                    it.forEach { todoList.add(Todo(it.id, it.todo, it.done, it.type, it.dateTime)) }
                    val groupedHashMap: Map<String, MutableList<Todo>> = groupDataIntoHashMap(todoList)
                    for(type: String in groupedHashMap.keys) {
                        val typeItem = HeaderItem(type)
                        consolidatedList.add(typeItem)
                        for (todo: Todo in groupedHashMap[type]!!) {
                            val generalItem = GeneralItem(todo)
                            consolidatedList.add(generalItem)
                        }
                        consolidatedList.add(FooterItem())
                    }
                    adapter.notifyDataSetChanged()
                }
    }

    private fun groupDataIntoHashMap(todoList: MutableList<Todo>): Map<String, MutableList<Todo>> {
        val groupedHashMap: HashMap<String, MutableList<Todo>> = HashMap()
        for(todo: Todo in todoList) {
            val type = todo.type
            if(groupedHashMap.containsKey(type)) {
                groupedHashMap[type]?.add(todo)
            } else {
                val list: MutableList<Todo> = ArrayList()
                list.add(todo)
                groupedHashMap[type] = list
            }
        }
        return groupedHashMap.toList().sortedBy { (key, _) -> key }.toMap()
    }

}