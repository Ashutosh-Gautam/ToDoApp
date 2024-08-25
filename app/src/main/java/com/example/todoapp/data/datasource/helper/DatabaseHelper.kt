package com.example.todoapp.data.datasource.helper

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.example.todoapp.data.model.TodoItem
import com.example.todoapp.utils.getCurrentUTCTimestamp
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

class DatabaseHelper(context: Context): SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "todos_db"
        private const val DATABASE_VERSION = 1
        private const val TABLE_NAME = "todoTable"
        private const val COLUMN_ID = "id"
        private const val COLUMN_TITLE = "title"
        private const val COLUMN_CREATED_DATE = "created_date"
    }

    // Create table SQL query
    private val CREATE_TABLE = (
            "CREATE TABLE " + TABLE_NAME + "("
                    + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                    + COLUMN_TITLE + " TEXT,"
                    + COLUMN_CREATED_DATE + " DATETIME DEFAULT CURRENT_TIMESTAMP"
                    + ")")

    // Create to-do table
    override fun onCreate(db: SQLiteDatabase?) {
        db?.execSQL(CREATE_TABLE)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db?.execSQL("DROP TABLE IF EXISTS $TABLE_NAME") // Drop older table if existed
        onCreate(db)  // Create tables again
    }

    // Insert a new item
    fun insertItem(todo: TodoItem): TodoItem {
        val db = this.writableDatabase

        val timestamp = getCurrentUTCTimestamp()
        val values = ContentValues()
        values.put(COLUMN_TITLE, todo.title)
        values.put(COLUMN_CREATED_DATE, timestamp)

        val id = db.insert(TABLE_NAME, null, values)
        db.close()

        todo.id = id
        todo.createdAt = timestamp

        return todo
    }

    // Retrieve items with pagination
    fun getAllItems(currentId: Long, loadSize: Int): List<TodoItem> {
        val items = mutableListOf<TodoItem>()
        val db = this.readableDatabase

        // Convert the parameters to a string array
        val selectionArgs = arrayOf(currentId.toString(), loadSize.toString())

        val cursor: Cursor = db.rawQuery(
            "SELECT * FROM $TABLE_NAME WHERE id > ? ORDER BY id ASC LIMIT ?",
            selectionArgs
        )
        if (cursor.moveToFirst()) {
            do {
                val id = cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_ID))
                val name = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TITLE))
                val timestamp = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CREATED_DATE))
                items.add(TodoItem(id, name, timestamp))
            } while (cursor.moveToNext())
        }
        cursor.close()
        return items
    }

    // Retrieve all items
    fun getAllItems(): List<TodoItem> {
        val items = mutableListOf<TodoItem>()
        val db = this.readableDatabase

        val cursor: Cursor = db.rawQuery("SELECT * FROM $TABLE_NAME", null)
        if (cursor.moveToFirst()) {
            do {
                val id = cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_ID))
                val name = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TITLE))
                val timestamp = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CREATED_DATE))
                items.add(TodoItem(id, name, timestamp))
            } while (cursor.moveToNext())
        }
        cursor.close()
        return items
    }

    // Retrieve items with pagination
    fun getRestOfItems(currentId: Long): List<TodoItem> {
        val items = mutableListOf<TodoItem>()
        val db = this.readableDatabase

        // Convert the parameters to a string array
        val selectionArgs = arrayOf(currentId.toString())

        val cursor: Cursor = db.rawQuery(
            "SELECT * FROM $TABLE_NAME WHERE id > ? ORDER BY id ASC",
            selectionArgs
        )
        if (cursor.moveToFirst()) {
            do {
                val id = cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_ID))
                val name = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TITLE))
                val timestamp = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CREATED_DATE))
                items.add(TodoItem(id, name, timestamp))
            } while (cursor.moveToNext())
        }
        cursor.close()
        return items
    }

    // Delete an item by ID
    fun deleteItem(id: Long): Int {
        val db = this.writableDatabase
        return db.delete(TABLE_NAME, "$COLUMN_ID = ?", arrayOf(id.toString()))
    }

    fun insertTodoInBulk() {
        val db = this.writableDatabase
        db.beginTransaction()
        try {
            // Prepare ContentValues and insert each item
            for (index in 1..2000) {
                val values = ContentValues().apply {
                    put(COLUMN_TITLE, "ToDo Item $index")
                }
                db.insert(TABLE_NAME, null, values)
            }
            // Mark the transaction as successful
            db.setTransactionSuccessful()
        } finally {
            // End the transaction
            db.endTransaction()
        }
    }
}