package com.example.todoapp.domain.di

import android.content.Context
import com.example.todoapp.data.datasource.helper.DatabaseHelper
import com.example.todoapp.data.repository.ToDoRepositoryImpl
import com.example.todoapp.domain.repository.ToDoRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
class DBModule {

    @Provides
    fun provideDBHelper(@ApplicationContext context: Context): DatabaseHelper {
        return DatabaseHelper(context)
    }

    @Provides
    fun provideUserRepository(
        databaseHelper: DatabaseHelper
    ): ToDoRepository {
        return ToDoRepositoryImpl(databaseHelper)
    }
}