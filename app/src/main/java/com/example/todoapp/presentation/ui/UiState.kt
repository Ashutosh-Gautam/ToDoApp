package com.example.todoapp.presentation.ui

sealed class UiState {
    object Loading: UiState()
    data class Success(val state: TransactionState) : UiState()
    data class Error(val message: String): UiState()
}

enum class TransactionState(val value: String) {
    INSERT("Todo Inserted"),
    BULK_INSERT("Todos Inserted"),
    DELETE("Todo Deleted")
}