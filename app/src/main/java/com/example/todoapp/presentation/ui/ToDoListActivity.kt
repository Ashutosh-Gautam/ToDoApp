package com.example.todoapp.presentation.ui

import android.os.Bundle
import android.view.View
import androidx.activity.ComponentActivity
import androidx.appcompat.app.AlertDialog
import androidx.core.view.postDelayed
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.paging.LoadState
import androidx.paging.PagingData
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.todoapp.R
import com.example.todoapp.data.model.TodoItem
import com.example.todoapp.databinding.ToDoListBinding
import com.example.todoapp.presentation.ui.adapter.ToDoListAdapter
import com.example.todoapp.presentation.ui.pagination.MainLoadStateAdapter
import com.example.todoapp.presentation.viewmodel.ToDoViewModel
import com.example.todoapp.utils.showAlertDialog
import com.example.todoapp.utils.showInputDialog
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ToDoListActivity : ComponentActivity() {

    private lateinit var binding: ToDoListBinding
    private lateinit var adapter: ToDoListAdapter
    private var progressDialog : AlertDialog? = null
    private lateinit var viewModel: ToDoViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ToDoListBinding.inflate(layoutInflater)

        setViews()
        setObservers()
        setListeners()
    }

    private fun setViews() {
        setContentView(binding.root)
        viewModel = ViewModelProvider(this)[ToDoViewModel::class.java]
        adapter = ToDoListAdapter { item, pos -> showDeleteAlertDialog(item, pos) }
        binding.recyclerView.adapter = adapter.withLoadStateFooter(MainLoadStateAdapter())
        val dividerItemDecoration = DividerItemDecoration(this, LinearLayoutManager.VERTICAL)
        binding.recyclerView.addItemDecoration(dividerItemDecoration)
    }

    private fun setObservers() {
        collectTodos()
        viewModel.deletedItem.observe(this) {
            val data = it
            var currentList = adapter.snapshot().items
            val deletedItem = currentList.find { todo -> todo.id == data.second }
            deletedItem?.let {
                currentList = currentList.filter { deletedItem.id != it.id }
            }
            if (currentList.isEmpty()) binding.button.visibility = View.VISIBLE
            val newPagingData = PagingData.from(currentList)
            adapter.submitData(lifecycle, newPagingData)
        }
        viewModel.result.observe(this) { response ->
            response?.let { handleResponse(response) }
        }
        viewModel.toDoItems.observe(this) {
            val currentList = adapter.snapshot().items.toMutableList()
            currentList.addAll(it.toList())
            val newPagingData = PagingData.from(currentList)
            adapter.submitData(lifecycle, newPagingData)
            binding.recyclerView.postDelayed(500) {
                binding.recyclerView.scrollToPosition(adapter.snapshot().items.size - 1)
            }
        }
    }

    private fun setListeners() {
        adapter.addLoadStateListener { loadStates ->
            if (loadStates.refresh is LoadState.NotLoading) {
                if (adapter.itemCount > 0) {
                    if (binding.button.visibility == View.VISIBLE) {
                        binding.button.visibility = View.GONE
                    }
                } else {
                    binding.button.visibility = View.VISIBLE
                }
            }
        }
        binding.button.setOnClickListener { viewModel.insertItems() }
        binding.fab.setOnClickListener { showInputAlertDialog() }
    }

    private fun collectTodos() {
        lifecycleScope.launch {
            viewModel.getTodos.collectLatest {
                adapter.submitData(it)
            }
        }
    }

    private fun handleResponse(response: UiState) {
        when (response) {
            is UiState.Error -> {
                progressDialog?.dismiss()
                showSnackBar(response.message)
            }
            is UiState.Success -> {
                progressDialog?.dismiss()
                showSnackBar(response.state.value)
                if (response.state == TransactionState.BULK_INSERT) {
                    collectTodos()
                } else if (response.state == TransactionState.INSERT) {
                    val currentList = adapter.snapshot().items
                    if (currentList.isNotEmpty()) {
                        currentList.last().id?.let { viewModel.getAllTodos(it) }
                    } else {
                        collectTodos()
                    }
                }
            }
            UiState.Loading -> showProgressDialog()
        }
    }

    private fun showSnackBar(message: String?) {
        progressDialog?.dismiss()
        binding.let {
            val snackBar: Snackbar = Snackbar.make(it.root,
                message ?: getString(R.string.unknown_error), BaseTransientBottomBar.LENGTH_LONG
            )
            snackBar.show()
        }
    }

    private fun showProgressDialog() {
        if (progressDialog == null) {
            val builder = AlertDialog.Builder(this)
            val customLayout: View = layoutInflater.inflate(R.layout.progress_dialog, null)
            builder.setView(customLayout)
            progressDialog = builder.create()
            progressDialog?.setCanceledOnTouchOutside(true)
        }
        progressDialog?.show()
    }

    private fun showDeleteAlertDialog(item: TodoItem, pos: Int) {
        showAlertDialog(this) { viewModel.deleteTodoItem(item, pos) }
    }

    private fun showInputAlertDialog() {
        showInputDialog(this) { title ->
            viewModel.insertTodoItem(TodoItem(null, title, null))
        }
    }
}