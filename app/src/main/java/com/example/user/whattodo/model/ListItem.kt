package com.example.user.whattodo.model

abstract class ListItem {

    companion object {
        const val TYPE_HEADER = 0
        const val TYPE_GENERAL = 1
        const val TYPE_FOOTER = 2
    }

    abstract fun getType(): Int

}