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

    private val repo: TodoRepository by lazy {
        TodoRepository(context)
    }

    private val _uiState = MutableStateFlow(UiState())
    val uiState = _uiState.asStateFlow()

    private val compositeDisposable = CompositeDisposable()

    init {
        val disposable = repo.getAllTodos()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe { list ->
                _uiState.update { UiState(list = list) }
            }

        compositeDisposable.add(disposable)
    }

    fun upsert(todo: Todo) {
        repo.upsert(todo)
            .subscribeOn(Schedulers.io())
            .subscribe()
    }

    fun delete(todo: Todo) {
        repo.delete(todo)
            .subscribeOn(Schedulers.io())
            .subscribe()
    }

    override fun onCleared() {
        super.onCleared()
        compositeDisposable.dispose()
    }

}

data class UiState(
    val isLoading: Boolean = false,
    val list: List<Todo>? = null,
    val error: String = ""
)