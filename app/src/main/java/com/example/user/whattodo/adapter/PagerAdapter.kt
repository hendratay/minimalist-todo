package com.example.user.whattodo.adapter

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import com.example.user.whattodo.fragment.GroceryFragment
import com.example.user.whattodo.fragment.HomeFragment
import com.example.user.whattodo.fragment.ReminderFragment
import com.example.user.whattodo.fragment.TaskFragment

class PagerAdapter(fm: FragmentManager): FragmentPagerAdapter(fm) {

    override fun getCount(): Int {
        return 4
    }

    override fun getItem(position: Int): Fragment {
        lateinit var fragment: Fragment
        when(position) {
            0 -> fragment = HomeFragment()
            1 -> fragment = TaskFragment()
            2 -> fragment = ReminderFragment()
            3 -> fragment = GroceryFragment()
        }
        return fragment
    }

    override fun getPageTitle(position: Int): CharSequence? {
        return when(position) {
            0 -> "Home"
            1 -> "Task"
            2 -> "Reminder"
            3 -> "Grocery"
            else -> ""
        }
    }

}