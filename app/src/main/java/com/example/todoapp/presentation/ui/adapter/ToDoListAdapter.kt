package com.example.todoapp.presentation.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.todoapp.data.model.TodoItem
import com.example.todoapp.databinding.ToDoItemBinding
import com.example.todoapp.utils.toDateTime

class ToDoListAdapter(private val onItemLongClicked: (TodoItem, Int) -> Unit) :
    PagingDataAdapter<TodoItem, ToDoListAdapter.MainViewHolder>(DIFF_CALLBACK) {

    companion object {
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<TodoItem>() {
            override fun areItemsTheSame(oldItem: TodoItem, newItem: TodoItem): Boolean =
                oldItem.id == newItem.id

            override fun areContentsTheSame(oldItem: TodoItem, newItem: TodoItem): Boolean =
                oldItem == newItem
        }
    }

    inner class MainViewHolder(val binding: ToDoItemBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MainViewHolder {
        return MainViewHolder(
            ToDoItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }

    override fun onBindViewHolder(holder: MainViewHolder, position: Int) {
        val item = getItem(position)
        holder.binding.apply {
            textView.text = item?.title
            date.text = item?.createdAt?.toDateTime()
        }
        holder.binding.root.setOnLongClickListener {
            item?.let { onItemLongClicked(item, position) }
            true
        }
    }

    fun deleteItem(pos: Int) {
        this.notifyItemRemoved(pos)
    }
}
