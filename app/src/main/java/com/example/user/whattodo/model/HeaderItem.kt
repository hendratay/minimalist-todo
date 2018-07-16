package com.example.user.whattodo.model

class HeaderItem(val type: String): ListItem() {

    override fun getType(): Int {
        return TYPE_HEADER
    }

}