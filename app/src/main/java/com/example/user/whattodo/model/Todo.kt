package com.example.user.whattodo.model

import java.util.*

data class Todo(val todoId: Long, val todoText : String, val done : Boolean, val type: String, val date: Date?)
