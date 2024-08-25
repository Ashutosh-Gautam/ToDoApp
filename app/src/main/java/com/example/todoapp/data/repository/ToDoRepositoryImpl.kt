package com.example.todoapp.data.repository

import com.example.todoapp.data.datasource.helper.DatabaseHelper
import com.example.todoapp.data.model.TodoItem
import com.example.todoapp.domain.repository.ToDoRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class ToDoRepositoryImpl @Inject constructor(private val databaseHelper: DatabaseHelper): ToDoRepository {

    override suspend fun getRestOfItems(id: Long): List<TodoItem> {
        return withContext(Dispatchers.IO) {
            databaseHelper.getRestOfItems(id)
        }
    }

    override suspend fun getAllTodos(currentId: Long, loadSize: Int): List<TodoItem> {
        return withContext(Dispatchers.IO) {
            databaseHelper.getAllItems(currentId, loadSize)
        }
    }

    override suspend fun insertTodo(item: TodoItem): TodoItem {
        return withContext(Dispatchers.IO) {
            databaseHelper.insertItem(item)
        }
    }

    override suspend fun deleteTodoItem(id: Long): Int {
        return withContext(Dispatchers.IO) {
            databaseHelper.deleteItem(id)
        }
    }

    override suspend fun insertTodoInBulk() {
        return withContext(Dispatchers.IO) {
            databaseHelper.insertTodoInBulk()
        }
    }
}