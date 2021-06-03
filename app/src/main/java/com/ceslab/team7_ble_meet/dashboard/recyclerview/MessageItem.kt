package com.ceslab.team7_ble_meet.dashboard.recyclerview

import android.view.Gravity
import android.view.ViewGroup
import android.widget.FrameLayout
import com.ceslab.team7_ble_meet.Model.Message
import com.ceslab.team7_ble_meet.R
import com.ceslab.team7_ble_meet.repository.KeyValueDB
import com.xwray.groupie.kotlinandroidextensions.Item
import com.xwray.groupie.kotlinandroidextensions.ViewHolder
import kotlinx.android.synthetic.main.item_text_message.*
import java.text.SimpleDateFormat

abstract class MessageItem(private var message: Message):Item() {


    override fun bind(viewHolder: ViewHolder, position: Int) {
        createRootGravity(viewHolder)
        createTime(viewHolder)

    }
    private fun createTime(viewHolder: ViewHolder){
        val dateFormat = SimpleDateFormat.getDateTimeInstance(
            SimpleDateFormat.SHORT,
            SimpleDateFormat.SHORT)
        viewHolder.textView_message_time.text = dateFormat.format(message.time)
    }

    private fun createRootGravity(viewHolder: ViewHolder) {
        if (message.senderId == KeyValueDB.getUserShortId()) {
            viewHolder.message_root.apply {
                setBackgroundResource(R.drawable.rect_round_blue_color)
                val lParams = FrameLayout.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT, Gravity.END
                )
                this.layoutParams = lParams
            }
        } else {
            viewHolder.message_root.apply {
                setBackgroundResource(R.drawable.rect_round_white_color)
                val lParams = FrameLayout.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT, Gravity.START
                )
                this.layoutParams = lParams
            }
        }
    }
}