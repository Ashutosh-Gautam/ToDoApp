package com.example.todoapp.presentation.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import com.example.todoapp.data.model.TodoItem
import com.example.todoapp.domain.repository.ToDoRepository
import com.example.todoapp.presentation.ui.TransactionState
import com.example.todoapp.presentation.ui.UiState
import com.example.todoapp.presentation.ui.pagination.MainPagingSource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ToDoViewModel @Inject constructor(private val toDoRepository: ToDoRepository): ViewModel() {

    private val _result = MutableLiveData<UiState>()
    val result = _result
    private val _deletedItem = MutableLiveData<Pair<Int, Long>>()
    val deletedItem = _deletedItem
    private val _toDoItems = MutableLiveData<MutableList<TodoItem>>()
    val toDoItems = _toDoItems

    val getTodos =
        Pager(PagingConfig(pageSize = 20, enablePlaceholders = false, initialLoadSize = 20)) {
            MainPagingSource(toDoRepository)
        }.flow

    fun getAllTodos(id: Long) {
        viewModelScope.launch {
            _toDoItems.value = toDoRepository.getRestOfItems(id).toMutableList()
        }
    }

    fun insertTodoItem(item: TodoItem) {
        _result.value = UiState.Loading
        viewModelScope.launch {
            toDoRepository.insertTodo(item)
            _result.value = UiState.Success(TransactionState.INSERT)
        }
    }

    fun deleteTodoItem(item: TodoItem, position: Int) {
        _result.value = UiState.Loading
        viewModelScope.launch {
            item.id?.let {
                if (toDoRepository.deleteTodoItem(it) > 0) {
                    _deletedItem.value = Pair(position, it)
                }
                _result.value = UiState.Success(TransactionState.DELETE)
            }
        }
    }

    fun insertItems() {
        _result.value = UiState.Loading
        viewModelScope.launch {
            toDoRepository.insertTodoInBulk()
            _result.value = UiState.Success(TransactionState.BULK_INSERT)
        }
    }
}