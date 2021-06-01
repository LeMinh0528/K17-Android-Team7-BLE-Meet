package com.ceslab.team7_ble_meet.dialog

import android.app.Dialog
import android.view.Window
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import com.ceslab.team7_ble_meet.R
import com.ceslab.team7_ble_meet.service.MyApplication

interface ReadDialogListener {
    fun ok(){

    }
}
class ReadDialog(private val builder: ReadDialog.Builder?): DialogFragment() {
    private var tvTile: TextView? = null
    private var tvInfo: TextView? = null
    private var tvYes: TextView? = null
    private var btnOk: LinearLayout? = null

    override fun setupDialog(dialog: Dialog, style: Int) {
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        createDialog(dialog)
        clickEvents()
    }

    fun createDialog(dialog: Dialog) {
        dialog.setContentView(R.layout.custom_edit_passions_dialog)
        tvTile = dialog.findViewById(R.id.tv_title)
        tvInfo = dialog.findViewById(R.id.tv_info)
        tvYes = dialog.findViewById(R.id.tv_yes)
        btnOk = dialog.findViewById(R.id.btn_ok)
        setup()
    }

    fun clickEvents(){
        btnOk?.setOnClickListener {
            this.builder?.listener?.ok()
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

        }
    }

    data class Builder(var title: String? = MyApplication.context?.getString(R.string.needpassions),
                       var info: String? = MyApplication.context?.getString(R.string.needpassionsabout),
                       var tvYes: String? = MyApplication.context?.getString(R.string.yes),
                       var listener: ReadDialogListener? = null) {
        fun title(title: String?) = apply { this.title = title }
        fun info(info: String?) = apply { this.info = info }
        fun yesText(text: String?) = apply { this.tvYes = text }
        fun listener(listener: ReadDialogListener?) = apply { this.listener = listener }
        fun build() = ReadDialog(this)
    }
}