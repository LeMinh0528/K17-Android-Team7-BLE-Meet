package com.ceslab.team7_ble_meet.dashboard.recyclerview

import android.content.Context
import com.ceslab.team7_ble_meet.Model.ImageMessage
import com.ceslab.team7_ble_meet.Model.Message
import com.ceslab.team7_ble_meet.R
import com.ceslab.team7_ble_meet.service.MyApplication.Companion.context
import com.ceslab.team7_ble_meet.utils.GlideApp
import com.ceslab.team7_ble_meet.utils.ImagesStorageUtils
import com.xwray.groupie.kotlinandroidextensions.ViewHolder
import kotlinx.android.synthetic.main.item_image_message.*

class ImageMessageItem(private var message: ImageMessage,val context: Context): MessageItem(message) {

    override fun bind(viewHolder: ViewHolder, position: Int) {
        super.bind(viewHolder, position)
        GlideApp.with(context)
            .load(ImagesStorageUtils.pathToReference(message.imagePath))
            .into(viewHolder.imageView_message_image)
    }
    override fun getLayout() = R.layout.item_image_message

    override fun isSameAs(other: com.xwray.groupie.Item<*>?): Boolean {
        if(other !is ImageMessageItem){
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