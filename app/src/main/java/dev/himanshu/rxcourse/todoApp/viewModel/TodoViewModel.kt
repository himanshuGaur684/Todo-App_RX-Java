package dev.himanshu.rxcourse.todoApp.viewModel

import android.content.Context
import androidx.lifecycle.ViewModel
import dev.himanshu.rxcourse.todoApp.model.TodoRepository
import dev.himanshu.rxcourse.todoApp.model.local.Todo
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.schedulers.Schedulers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class TodoViewModel(context: Context) : ViewModel() {

    private val repository: TodoRepository by lazy { TodoRepository(context) }

    private val _todos = MutableStateFlow(emptyList<Todo>())
    val todo = _todos.asStateFlow()

    private val compositeDisposable = CompositeDisposable()

    init {
        val dispose = repository
            .getTodos()
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe { todos -> _todos.update { todos } }

        compositeDisposable.add(dispose)
    }

    fun upsert(todo: Todo) {
        repository.upsert(todo)
            .subscribeOn(Schedulers.io())
            .subscribe()
    }

    fun delete(todo: Todo) {
        repository.delete(todo)
            .subscribeOn(Schedulers.io())
            .subscribe()
    }

    override fun onCleared() {
        super.onCleared()
        compositeDisposable.dispose()
    }

}