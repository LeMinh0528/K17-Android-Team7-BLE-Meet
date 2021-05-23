package com.ceslab.team7_ble_meet.dialog

import android.app.Dialog
import android.os.Bundle
import android.view.Window
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import com.ceslab.team7_ble_meet.R
import com.ceslab.team7_ble_meet.service.MyApplication.Companion.context

interface ConfirmDialogListener {
    fun cancel()
    fun confirm()
}
class ConfirmDialog(private val builder: Builder?): DialogFragment() {
    private var tvTile: TextView? = null
    private var tvInfo: TextView? = null
    private var tvCancel: TextView? = null
    private var tvYes: TextView? = null
    private var btnCancel: LinearLayout? = null
    private var btnOk: LinearLayout? = null

    override fun setupDialog(dialog: Dialog, style: Int) {
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        createDialog(dialog)
        clickEvents()
    }

    fun createDialog(dialog: Dialog) {
        dialog.setContentView(R.layout.dialog_confirm)
        tvTile = dialog.findViewById(R.id.tv_title)
        tvInfo = dialog.findViewById(R.id.tv_info)
        tvCancel = dialog.findViewById(R.id.tv_cancel)
        tvYes = dialog.findViewById(R.id.tv_yes)
        btnCancel = dialog.findViewById(R.id.btn_cancel)
        btnOk = dialog.findViewById(R.id.btn_ok)
        setup()
    }

    fun clickEvents(){
        btnCancel?.setOnClickListener {
            this.builder?.listener?.cancel()
            dismiss()
        }
        btnOk?.setOnClickListener {
            this.builder?.listener?.confirm()
            dismiss()
        }
    }

    fun setup(){
        builder?.let {
            it.title?.let {
                tvTile?.text = it
            }
            it.info?.let {
                tvInfo?.text = it
            }
            it.tvYes?.let {
                tvYes?.text = it
            }
            it.tvCancel?.let {
                tvCancel?.text = it
            }
        }
    }

    data class Builder(var title: String? = context?.getString(R.string.confirmation),
                       var info: String? = null,
                       var tvYes: String? = context?.getString(R.string.yes),
                       var tvCancel: String? = context?.getString(R.string.cancel),
                       var listener: ConfirmDialogListener? = null) {
        fun title(title: String?) = apply { this.title = title }
        fun info(info: String?) = apply { this.info = info }
        fun yesText(text: String?) = apply { this.tvYes = text }
        fun cancelText(text: String?) = apply { this.tvCancel = text}
        fun listener(listener: ConfirmDialogListener?) = apply { this.listener = listener }
        fun build() = ConfirmDialog(this)
    }
}