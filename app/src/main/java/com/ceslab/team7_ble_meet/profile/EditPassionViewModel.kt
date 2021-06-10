package com.ceslab.team7_ble_meet.profile

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.ceslab.team7_ble_meet.UsersFireStoreHandler

class EditPassionViewModel:ViewModel() {
    private var instance = UsersFireStoreHandler()
    var userResp: MutableLiveData<UsersFireStoreHandler.Resp?> = instance.userResp

    fun getTags(onComplete:(tags: MutableList<String>)-> Unit){
        instance.getTags { tags ->
            onComplete(tags)
        }
    }

    fun updateTag(tags : MutableList<String>,onComplete: (status: String) -> Unit){
        instance.updateTag(tags){
            onComplete(it)
        }
    }
}