package com.ceslab.team7_ble_meet.EditDialog

import android.app.AlertDialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import com.ceslab.team7_ble_meet.R
import kotlinx.android.synthetic.main.activity_profile.*
import kotlinx.android.synthetic.main.custom_spinner_gender.*

class Edit_Dialog (context: Context) : AlertDialog(context)
{
    private lateinit var editDialogCallback: EditDialogCallback
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // USE BINDING SET ojbect
        val view = LayoutInflater.from(context).inflate(R.layout.custom_spinner_gender,null,false)
        setContentView(view)



    }
    fun setEditDialogCallback(editDialogCallback: EditDialogCallback) {
        this.editDialogCallback = editDialogCallback
    }

    interface EditDialogCallback {
        fun onConfirmClicked(data: String)
    }
}
