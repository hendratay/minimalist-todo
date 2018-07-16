package com.example.user.whattodo.model

class GeneralItem(val todo: Todo): ListItem() {

    override fun getType(): Int {
        return TYPE_GENERAL
    }

}
