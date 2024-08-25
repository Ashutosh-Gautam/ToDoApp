package com.example.todoapp.utils

import android.content.Context
import android.content.DialogInterface
import android.text.InputType
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import com.example.todoapp.R

fun showAlertDialog(context: Context, onPosClick: () -> Unit) {
    val builder = AlertDialog.Builder(context)
    builder.setTitle(context.getString(R.string.alert))
    builder.setMessage(context.getString(R.string.delete_confirm))
    builder.setPositiveButton("yes") { dialog, _ ->
        dialog.dismiss()
        onPosClick()
    }
    builder.setNegativeButton("no") { dialog, _ ->
        dialog.dismiss()
    }
    val alertDialog = builder.create()
    alertDialog.show()
}

fun showInputDialog(context: Context, handleUserInput: (String) -> Unit) {
    val input = EditText(context)
    input.inputType = InputType.TYPE_CLASS_TEXT

    val builder = AlertDialog.Builder(context)
        .setTitle(context.getString(R.string.enter_todo_title))
        .setView(input)
        .setCancelable(false)
        .setPositiveButton("ok", null)
        .setNegativeButton("Cancel") { dialog, _ ->
            dialog.dismiss()
        }
    val alertDialog = builder.create()
    alertDialog.show()
    alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener {
        val inputString = input.text.toString().trim()
        if (inputString.isEmpty()) {
            input.error = "Input cannot be empty"
        } else {
            handleUserInput(inputString)
            alertDialog.dismiss()
        }
    }
}