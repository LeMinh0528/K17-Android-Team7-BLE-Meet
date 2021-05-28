package com.ceslab.team7_ble_meet.dialog

import android.app.Dialog
import android.view.Window
import androidx.fragment.app.DialogFragment
import com.ceslab.team7_ble_meet.R


class LoadingDialog : DialogFragment() {

    override fun setupDialog(dialog: Dialog, style: Int) {
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(false)
        createDialog(dialog)
    }

    fun createDialog(dialog: Dialog) {
        dialog.setContentView(R.layout.dialog_loading)
    }
}