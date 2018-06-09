package com.example.user.whattodo.adapter

import android.content.Context
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import com.example.user.whattodo.fragment.GroceryFragment
import com.example.user.whattodo.fragment.ReminderFragment
import com.example.user.whattodo.fragment.TaskFragment

class PagerAdapter(fm: FragmentManager, context: Context): FragmentPagerAdapter(fm) {

    override fun getCount(): Int {
        return 3
    }

    override fun getItem(position: Int): Fragment {
        lateinit var fragment: Fragment
        when(position) {
            0 -> fragment = TaskFragment()
            1 -> fragment = ReminderFragment()
            2 -> fragment = GroceryFragment()
        }
        return fragment
    }

}