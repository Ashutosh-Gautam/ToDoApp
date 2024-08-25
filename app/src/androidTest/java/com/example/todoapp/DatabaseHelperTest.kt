package com.example.todoapp

import android.content.ContentValues
import android.database.sqlite.SQLiteDatabase
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.example.todoapp.data.datasource.helper.DatabaseHelper
import com.example.todoapp.data.model.TodoItem
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class DatabaseHelperTest {

    private lateinit var dbHelper: DatabaseHelper
    private lateinit var db: SQLiteDatabase

    @Before
    fun setUp() {
        dbHelper = DatabaseHelper(InstrumentationRegistry.getInstrumentation().targetContext)
        db = dbHelper.writableDatabase
    }

    @After
    fun tearDown() {
        db.close()
        dbHelper.close()
    }

    @Test
    fun test_insert_and_retrieve_data() {
        val todo = dbHelper.insertItem(TodoItem(null, "ToDo Item", null))
        assert(todo.createdAt != null)
        assert("ToDo Item" == todo.title)
        assert(todo.id != null)

        val items = dbHelper.getAllItems()
        assert(items.isNotEmpty())
    }

    @Test
    fun test_insert_and_delete_data() {
        val todo = dbHelper.insertItem(TodoItem(null, "ToDo Item", null))
        assert(todo.createdAt != null)
        assert("ToDo Item" == todo.title)
        assert(todo.id != null)

        val items = dbHelper.getAllItems()
        assert(items.isNotEmpty())

        todo.id?.let {
            assert(dbHelper.deleteItem(it) > 0)
        }
    }
}