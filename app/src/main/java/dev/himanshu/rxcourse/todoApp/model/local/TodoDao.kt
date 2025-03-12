package dev.himanshu.rxcourse.todoApp.model.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Upsert
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Observable

@Dao
interface TodoDao {

    @Upsert
    fun upsert(todo: Todo): Completable

    @Query("Select * from Todo")
    fun getAllTodos(): Observable<List<Todo>>

    @Delete
    fun delete(todo: Todo): Completable


}