package com.example.todoapp.presentation.ui.pagination

import android.util.Log
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.example.todoapp.data.model.TodoItem
import com.example.todoapp.domain.repository.ToDoRepository

class MainPagingSource(
    private val toDoRepository: ToDoRepository
) : PagingSource<Long, TodoItem>() {
    override suspend fun load(params: LoadParams<Long>): LoadResult<Long, TodoItem> {
        return try {
            Log.d("MainPagingSource", "load: $params")

            val currentId = params.key ?: 0L
            val items = toDoRepository.getAllTodos(currentId, params.loadSize)
            // Determine the next key
            val nextKey = if (items.isNotEmpty()) {
                items.last().id
            } else {
                null
            }

            LoadResult.Page(
                data = items,
                prevKey = null,
                nextKey = nextKey
            )
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }

    override fun getRefreshKey(state: PagingState<Long, TodoItem>): Long? {
        return state.anchorPosition?.let {
            state.closestPageToPosition(it)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(it)?.nextKey?.minus(1)
        }
    }
}