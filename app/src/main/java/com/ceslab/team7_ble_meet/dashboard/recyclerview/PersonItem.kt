package com.ceslab.team7_ble_meet.dashboard.recyclerview

import android.content.Context
import android.util.Log
import com.bumptech.glide.Glide
import com.ceslab.team7_ble_meet.Model.Message
import com.ceslab.team7_ble_meet.Model.MessageType
import com.ceslab.team7_ble_meet.Model.Person
import com.ceslab.team7_ble_meet.R
import com.ceslab.team7_ble_meet.UsersFireStoreHandler
import com.ceslab.team7_ble_meet.repository.KeyValueDB
import com.ceslab.team7_ble_meet.service.MyApplication.Companion.context
import com.ceslab.team7_ble_meet.utils.GlideApp
import com.ceslab.team7_ble_meet.utils.ImagesStorageUtils
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.Query

import com.xwray.groupie.kotlinandroidextensions.Item
import com.xwray.groupie.kotlinandroidextensions.ViewHolder
import kotlinx.android.synthetic.main.activity_register_picture.*
import kotlinx.android.synthetic.main.item_person.*


class PersonItem(
    var userName: String,
    val userId: String,
    var imagePath: String,
    var channelId: String,
    var context: Context
) : Item() {
    lateinit var listener : ListenerRegistration
    override fun bind(viewHolder: ViewHolder, position: Int) {

        UsersFireStoreHandler().userRef.document(userId)
            .get()
            .addOnSuccessListener { data ->
                if(data!= null){
                    if(data["Name"] != null){
                        viewHolder.tv_name.text = data["Name"].toString()
                        userName = data["Name"].toString()
                    }else{
                        viewHolder.tv_name.text = ""
                    }
                }
                if(data["avatar"] != null){
                    imagePath = data["avatar"] as String
                    GlideApp.with(context)
                        .load(ImagesStorageUtils.pathToReference(data["avatar"] as String))
                        .placeholder(R.drawable.ic_user)
                        .into(viewHolder.image)
                }
            }

//        UsersFireStoreHandler().chatChannelRef.document(channelId).collection("messages")
//            .orderBy("time", Query.Direction.DESCENDING).limit(1)
//            .get().addOnSuccessListener {
//                it.documents.forEach {
//                    Log.d("PersonItem","item: $it")
//                    Log.d("PersonItem","item: ${it["type"]}")
//                    if(it["type"] == MessageType.IMAGE){
//                        if(it["senderId"] == KeyValueDB.getUserShortId()){
//                            viewHolder.tv_lastText.text = "${viewHolder.tv_name.text} send an image"
//                        }else{
//                            viewHolder.tv_lastText.text = "you send an image"
//                        }
//                    }else{
//                        viewHolder.tv_lastText.text = it["text"] as String
//                    }
//                }
//            }

        listener = UsersFireStoreHandler().setLastTextListener(channelId){
            viewHolder.tv_lastText.text = it
        }
    }

    override fun getLayout(): Int {
        return R.layout.item_person
    }
    override fun isSameAs(other: com.xwray.groupie.Item<*>?): Boolean {
        if(other !is PersonItem){
            return false
        }
        if(this.userId != other.userId){
            return false
        }
        return true
    }

    override fun equals(other: Any?): Boolean {
        return isSameAs(other as? TextMessageItem)
    }


}