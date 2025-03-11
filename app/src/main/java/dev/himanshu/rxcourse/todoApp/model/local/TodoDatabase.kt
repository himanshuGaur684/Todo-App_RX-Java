package dev.himanshu.rxcourse.todoApp.model.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [Todo::class], version = 1, exportSchema = false)
abstract class TodoDatabase : RoomDatabase() {
    companion object {
        fun getInstance(context: Context): TodoDatabase {
            return Room.databaseBuilder(context, TodoDatabase::class.java, "todo_db")
                .fallbackToDestructiveMigration()
                .build()
        }
    }

    abstract fun getTodoDao(): TodoDao
}