package dev.himanshu.rxcourse.todoApp.model

import android.content.Context
import dev.himanshu.rxcourse.todoApp.model.local.AppDatabase
import dev.himanshu.rxcourse.todoApp.model.local.Todo
import dev.himanshu.rxcourse.todoApp.model.local.TodoDao
import io.reactivex.rxjava3.core.Observable

class TodoRepository(context: Context) {

    private val dao: TodoDao by lazy {
        AppDatabase.getInstance(context).getTodoDao()
    }

    fun upsert(todo: Todo) = dao.upsert(todo)

    fun delete(todo: Todo) = dao.delete(todo)

    fun getAllTodos(): Observable<List<Todo>> = dao.getAllTodos()


}