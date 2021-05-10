package com.ceslab.team7_ble_meet.EditDialog

import android.app.AlertDialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import com.ceslab.team7_ble_meet.R

class Edit_Dialog (context: Context) : AlertDialog(context)
{
    private lateinit var editDialogCallback: EditDialogCallback
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val inflate = LayoutInflater.from(context).inflate(R.layout.custom_spinner_gender,null,false)
        setContentView(inflate)
    }
    fun setEditDialogCallback(editDialogCallback: EditDialogCallback) {
        this.editDialogCallback = editDialogCallback
    }

    interface EditDialogCallback {
        fun onConfirmClicked(data: String)
    }
}
