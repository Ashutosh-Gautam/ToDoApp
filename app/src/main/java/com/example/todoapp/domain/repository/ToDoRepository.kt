package com.example.todoapp.domain.repository

import com.example.todoapp.data.model.TodoItem

interface ToDoRepository {
    suspend fun getAllTodos(currentId: Long, loadSize: Int): List<TodoItem>
    suspend fun insertTodo(item: TodoItem) : TodoItem
    suspend fun deleteTodoItem(id: Long) : Int
    suspend fun insertTodoInBulk()
    suspend fun getRestOfItems(id: Long): List<TodoItem>
}