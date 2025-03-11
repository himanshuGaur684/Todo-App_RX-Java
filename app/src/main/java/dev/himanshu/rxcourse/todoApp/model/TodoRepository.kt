package dev.himanshu.rxcourse.todoApp.model

import android.content.Context
import dev.himanshu.rxcourse.todoApp.model.local.Todo
import dev.himanshu.rxcourse.todoApp.model.local.TodoDatabase

class TodoRepository(private val context: Context) {

    private val todoDao by lazy {
        TodoDatabase.getInstance(context).getTodoDao()
    }

    fun upsert(todo: Todo) = todoDao.upsert(todo)

    fun delete(todo: Todo) = todoDao.delete(todo)

    fun getTodos() = todoDao.getTodos()

}