package com.ceslab.team7_ble_meet.profile

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.ceslab.team7_ble_meet.UsersFireStoreHandler
import com.ceslab.team7_ble_meet.model.User
import com.ceslab.team7_ble_meet.repository.KeyValueDB

class EditProfileViewModel: ViewModel() {
    private var instance = UsersFireStoreHandler()
    var userResp: MutableLiveData<UsersFireStoreHandler.Resp?> = instance.userResp
    var avatar : String = ""
    var background: String = ""
    var bio: String = ""
    fun getUserInfor(onComplete:(User) -> Unit){
        instance.getCurrentUser(KeyValueDB.getUserShortId()){
            onComplete(it)
        }
    }
    fun updateAvatar(){

    }

    fun updateBackground(){

    }

    fun updateBio(){

    }

    fun updateTag(){

    }

}