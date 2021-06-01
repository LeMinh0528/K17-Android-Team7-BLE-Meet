package com.ceslab.team7_ble_meet.dashboard.recyclerview

import android.app.ActionBar
import android.content.Context
import android.view.Gravity
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.widget.FrameLayout
import com.ceslab.team7_ble_meet.Model.TextMessage
import com.ceslab.team7_ble_meet.R
import com.ceslab.team7_ble_meet.repository.KeyValueDB
import com.xwray.groupie.kotlinandroidextensions.Item
import com.xwray.groupie.kotlinandroidextensions.ViewHolder
import kotlinx.android.synthetic.main.item_text_message.*
import java.text.SimpleDateFormat

class TextMessageItem(val message: TextMessage
                    ,context: Context)
    : MessageItem(message) {
    override fun bind(viewHolder: ViewHolder, position: Int) {
        viewHolder.textView_message_text.text = message.text
        super.bind(viewHolder,position)
    }


    override fun getLayout() = R.layout.item_text_message

    override fun isSameAs(other: com.xwray.groupie.Item<*>?): Boolean {
        if(other !is TextMessageItem){
            return false
        }
        if(this.message != other.message){
            return false
        }
        return true
    }

    override fun equals(other: Any?): Boolean {
        return isSameAs(other as? TextMessageItem)
    }
}