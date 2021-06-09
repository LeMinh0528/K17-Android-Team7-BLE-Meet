package com.ceslab.team7_ble_meet.profile

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.ceslab.team7_ble_meet.UsersFireStoreHandler
import com.ceslab.team7_ble_meet.model.User
import com.ceslab.team7_ble_meet.repository.KeyValueDB
import com.ceslab.team7_ble_meet.utils.ImagesStorageUtils
import com.google.firebase.firestore.SetOptions

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
    fun updateAvatar(avatar: ByteArray,onComplete: (status: String, path: String) -> Unit){
        ImagesStorageUtils.uploadProfilePhoto(avatar){ imagePath ->
            instance.userRef.document(KeyValueDB.getUserShortId())
                .set(hashMapOf("avatar" to imagePath), SetOptions.merge())
                .addOnSuccessListener {
                    onComplete("SUCCESS",imagePath)
                }
                .addOnFailureListener {
                    onComplete("FAILED",it.message.toString())
                }
        }
    }

    fun updateBackground(background: ByteArray,onComplete: (status: String, path: String) -> Unit){
        ImagesStorageUtils.uploadProfilePhoto(background){ imagePath ->
            instance.userRef.document(KeyValueDB.getUserShortId())
                .set(hashMapOf("background" to imagePath), SetOptions.merge())
                .addOnSuccessListener {
                    onComplete("SUCCESS",imagePath)
                }
                .addOnFailureListener {
                    onComplete("FAILED",it.message.toString())
                }
        }
    }

    fun updateBio(){

    }

    fun updateTag(){

    }

}