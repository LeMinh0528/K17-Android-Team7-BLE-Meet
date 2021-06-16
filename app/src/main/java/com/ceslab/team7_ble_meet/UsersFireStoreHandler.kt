package com.ceslab.team7_ble_meet

import android.content.Context
import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.ceslab.team7_ble_meet.Model.*
import com.ceslab.team7_ble_meet.dashboard.recyclerview.ImageMessageItem
import com.ceslab.team7_ble_meet.dashboard.recyclerview.PersonItem
import com.ceslab.team7_ble_meet.dashboard.recyclerview.TextMessageItem
import com.ceslab.team7_ble_meet.model.User
import com.ceslab.team7_ble_meet.repository.KeyValueDB
import com.ceslab.team7_ble_meet.service.MessagingService
import com.ceslab.team7_ble_meet.utils.ImagesStorageUtils
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.*
import com.google.firebase.messaging.FirebaseMessaging
import com.xwray.groupie.kotlinandroidextensions.Item
import java.util.*

class UsersFireStoreHandler {
    private val fireStoreInstance: FirebaseFirestore by lazy { FirebaseFirestore.getInstance() }

    var userRef = fireStoreInstance.collection("Users")
    private var uidRef = fireStoreInstance.collection("UUID")
    var chatChannelRef = fireStoreInstance.collection("chatChannels")
    var mAuth: FirebaseAuth = FirebaseAuth.getInstance()
    var userResp = MutableLiveData<Resp?>()

    fun updateBirthDay(birthDay: String) {
        val note = mutableMapOf<String, String>()
        note["DayOfBirth"] = birthDay
        userRef.document(KeyValueDB.getUserShortId())
            .set(note, SetOptions.merge())
            .addOnSuccessListener {
                uidRef.document(KeyValueDB.getUserId())
                    .set(hashMapOf("isRegisterDoB" to true), SetOptions.merge())
                userResp.postValue(Resp("NONE", "SUCCESS", "update tag successful!"))
                KeyValueDB.setDayOfBirth(true)
            }
            .addOnFailureListener {
                userResp.postValue(Resp("NONE", "FAILED", "update tag failed!"))
                KeyValueDB.setDayOfBirth(false)
            }
    }

    fun updateName(name: String) {
        val note = mutableMapOf<String, String>()
        note["Name"] = name
        note["bio"] = ""
        note["avatar"] = ""
        note["background"] = ""
        userRef.document(KeyValueDB.getUserShortId())
            .set(note, SetOptions.merge())
            .addOnSuccessListener {
                uidRef.document(KeyValueDB.getUserId())
                    .set(hashMapOf("isRegisterName" to true), SetOptions.merge())
                userResp.postValue(Resp("NONE", "SUCCESS", "update name successful!"))
            }
            .addOnFailureListener {
                userResp.postValue(Resp("NONE", "FAILED", "update name failed!"))
            }
    }

    fun getGender(): String? {
        var gender: String? = null
        mAuth.currentUser?.let {
            userRef.document(it.uid)
                .get()
                .addOnSuccessListener { query ->
                    if (query != null) {
                        Log.d("UserFireStoreHandler", "query: ${query.data?.get("Gender")}")
                        gender = query.data?.get("Gender") as String?
                    }
                }
        }
        return gender
    }

    fun updateGender(gender: String?, inter: String) {
        val note = mutableMapOf<String, String?>()
        note["Gender"] = gender
        note["Interested"] = inter
        userRef.document(KeyValueDB.getUserShortId())
            .set(note, SetOptions.merge())
            .addOnSuccessListener {
                uidRef.document(KeyValueDB.getUserId())
                    .set(hashMapOf("isRegisterGender" to true), SetOptions.merge())
                userResp.postValue(Resp("NONE", "SUCCESS", "update gender successful!"))
//                KeyValueDB.setRegisterUserGender(true)
            }
            .addOnFailureListener {
                userResp.postValue(Resp("NONE", "FAILED", "update gender failed!"))
//                KeyValueDB.setRegisterUserGender(false)

            }
    }

    fun updateTag(list: MutableList<String>, onComplete: (status: String) -> Unit) {
        Log.d("UserFireStoreHandler","update tag passion")
        val note = mutableMapOf<String, MutableList<String>>()
        note["Tag"] = list
        userRef.document(KeyValueDB.getUserShortId())
            .set(note, SetOptions.merge())
            .addOnSuccessListener {
                uidRef.document(KeyValueDB.getUserId())
                    .set(hashMapOf("isRegisterTag" to true), SetOptions.merge())
                Log.d("UserFireStoreHandler","update tag passion sucsses ")
                KeyValueDB.setUserTag(true)
                onComplete("SUCCESS")
            }
            .addOnCanceledListener {
                Log.d("UserFireStoreHandler","update tagfaiiled ")
                onComplete("FAILED")
            }
            .addOnFailureListener {
                Log.d("UserFireStoreHandler","update tagfaiiled ")
                onComplete("FAILED")
            }

    }

    fun getTags(onComplete: (tags: MutableList<String>) -> Unit) {
        userRef.document(KeyValueDB.getUserShortId())
            .get()
            .addOnSuccessListener {
                if (it != null) {
                    if (it["Tag"] != null) {
                        onComplete(it["Tag"] as MutableList<String>)
                        Log.d("UserFireStoreHandler", "shortId: ${it["Tag"]}")
                    } else {
                        onComplete(mutableListOf())
                    }
                } else {
                    onComplete(mutableListOf())
                }
            }
    }

    fun updateAvatar(byteArray: ByteArray) {
        Log.d("UserFireStoreHandler", "shortId: ${getUserShortId()}")
        ImagesStorageUtils.uploadProfilePhoto(byteArray) {
            userRef.document(KeyValueDB.getUserShortId())
                .set(hashMapOf("avatar" to it), SetOptions.merge())
                .addOnSuccessListener {
                    uidRef.document(KeyValueDB.getUserId())
                        .set(hashMapOf("isRegisterAvatar" to true), SetOptions.merge())
                    KeyValueDB.setUserAvatar(true)
                    KeyValueDB.setRegister(true)
                    userResp.postValue(
                        Resp(
                            "NONE",
                            "SUCCESS",
                            ""
                        )
                    )
                }
                .addOnFailureListener {
                    userResp.postValue(Resp("NONE", "FAILED", "update avatar failed!"))
                }

        }
    }

    private fun getListUserShortId(): MutableList<String> {
        val list: MutableList<String> = mutableListOf()
        userRef.get()
            .addOnSuccessListener {
                if (it != null) {
                    for (i in it) {
                        list.add(i.id)
                    }
                }
            }
        return list
    }

    private fun getUserShortId(): String {
        var id = ""
        Log.d("UserFireStoreHandler", "shortId: ${mAuth.currentUser?.uid}")
        mAuth.currentUser?.let { setting ->
            uidRef.document(setting.uid)
                .get()
                .addOnSuccessListener {
                    if (it != null) {
                        id = it["shortID"] as String
                    }
                }
        }
        return id
    }

    private fun getUserSetting() {
        mAuth.currentUser?.let { setting ->
            uidRef.document(setting.uid)
                .get()
                .addOnSuccessListener {
                    if (it != null) {
                        Log.d("UserFireStoreHandler", "it: ${it["shortID"]}")
                        KeyValueDB.setUserShortId(it["shortID"] as String)
                        KeyValueDB.setUserId(mAuth.currentUser!!.uid)
                        if (!(it["isRegisterName"] as Boolean)) {
                            userResp.postValue(Resp("LOGIN", "SUCCESS", "USERNAME"))
                        } else if (!(it["isRegisterGender"] as Boolean)) {
                            userResp.postValue(Resp("LOGIN", "SUCCESS", "GENDER"))
                        } else if (!(it["isRegisterDoB"] as Boolean)) {
                            userResp.postValue(Resp("LOGIN", "SUCCESS", "DOB"))
                        } else if (!(it["isRegisterTag"] as Boolean)) {
                            userResp.postValue(Resp("LOGIN", "SUCCESS", "TAG"))
                        } else if (!(it["isRegisterAvatar"] as Boolean)) {
                            userResp.postValue(Resp("LOGIN", "SUCCESS", "IMAGE"))
                        } else {
                            KeyValueDB.setRegister(true)
                            getCurrentToken()

                            userResp.postValue(Resp("LOGIN", "SUCCESS", "DASHBOARD"))
                        }
                    }
                }
        }
    }

    private fun getCurrentToken() {
        FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
            if (!task.isSuccessful) {
                Log.d(
                    "UserFireStoreHandler",
                    "Fetching FCM registration token failed",
                    task.exception
                )
                return@OnCompleteListener
            }

            // Get new FCM registration token
            val token = task.result
            Log.d("UserFireStoreHandler", "Fetching FCM registration token $token")
            if (token != null) {
                MessagingService.updateToken(token)
            }
        })
    }

    private fun randomUId(): String {
        var uid: String = generateUniqueID()
        val list: MutableList<String> = getListUserShortId()
        return if (list.isEmpty()) {
            uid
        } else {
            while (list.indexOf(uid) != -1) {
                uid = generateUniqueID()
            }
            uid
        }
    }

    fun createUser(email: String, pass: String) {
        val uid = randomUId()
        mAuth.createUserWithEmailAndPassword(email, pass)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val note = mutableMapOf<String, String>()

                    note["EMAIL"] = email
                    note["PASS"] = pass
                    userRef.document(uid)
                        .set(note).addOnSuccessListener {
                            mAuth.currentUser?.let { it1 -> KeyValueDB.setUserId(it1.uid) }
                            KeyValueDB.setUserShortId(uid)
                            getCurrentToken()
                            mAuth.currentUser?.let { it1 ->
                                uidRef.document(it1.uid)
                                    .set(
                                        hashMapOf(
                                            "shortID" to uid,
                                            "isRegisterName" to false,
                                            "isRegisterDoB" to false,
                                            "isRegisterTag" to false,
                                            "isRegisterGender" to false,
                                            "isRegisterAvatar" to false
                                        )
                                    )
                            }
//                            userRef.document(uid).collection("engagedChatChannel")
                            userResp.postValue(Resp("NONE", "SUCCESS", "add new users successful"))
                        }
                        .addOnFailureListener {
                            userResp.postValue(it.message?.let { it1 ->
                                Resp(
                                    "NONE",
                                    "FAILED",
                                    it1
                                )
                            })
                        }

                } else if (task.isCanceled) {
                    userResp.postValue(Resp("NONE", "FAILED", "ERROR"))
                }
            }
            .addOnFailureListener {
                Log.d("TAG", "Fail: ${it.message}")
                userResp.postValue(it.message?.let { it1 -> Resp("NONE", "FAILED", it1) })
            }
    }

    fun getCurrentUser(id: String, onComplete: (User) -> Unit) {
        userRef.document(id).get()
            .addOnSuccessListener {
                onComplete(it.toObject(User::class.java)!!)
            }
    }

    fun logInUser(email: String, pass: String) {
        Log.d("UserFireStoreHandler","goto login $email , $pass")
        mAuth.signInWithEmailAndPassword(email, pass)
            .addOnCompleteListener { task ->
                Log.d("UserFireStoreHandler","TASK $task")
                Log.d("UserFireStoreHandler","TASK complete ${task.isComplete}")
                Log.d("UserFireStoreHandler","TASK success ${task.isSuccessful}")
                Log.d("UserFireStoreHandler","TASK cancled ${task.isCanceled}")
                Log.d("UserFireStoreHandler","TASK ${task.exception}")
                if (task.isSuccessful) {
                    Log.d("UserFireStoreHandler", "${mAuth.currentUser}")
                    getUserSetting()
                } else if (task.isCanceled) {
                    mAuth.currentUser?.let { KeyValueDB.setUserId(it.uid) }
                    userResp.postValue(Resp("NONE", "SUCCESS", "Login failed!"))
                }
            }
            .addOnCanceledListener {
                userResp.postValue(Resp("NONE", "FAILED", "Login failed!"))
            }
            .addOnFailureListener {
                userResp.postValue(Resp("NONE", "FAILED", "Login failed! ${it.message}"))
            }
    }


    fun deleteData() {
        userRef.document(KeyValueDB.getUserShortId()).delete()
            .addOnSuccessListener {
                mAuth.currentUser?.let { it1 -> uidRef.document(it1.uid).delete() }
                mAuth.currentUser?.delete()?.addOnSuccessListener {
                    KeyValueDB.clearData()
                    userResp.postValue(Resp("DELETE", "SUCCESS", ""))

                }
                    ?.addOnFailureListener {
                        userResp.postValue(Resp("DELETE", "FAILED", "${it.message}"))
                    }

            }
            .addOnFailureListener {
                userResp.postValue(Resp("DELETE", "FAILED", ""))
            }

    }

    fun addUserListener(
        context: Context,
        onListen: (List<PersonItem>) -> Unit
    ): ListenerRegistration {
        return userRef.document(KeyValueDB.getUserShortId())
            .collection("engagedChatChannel")
            .addSnapshotListener(MetadataChanges.INCLUDE) { querySnapshot, exception ->
                if (exception != null) {
                    Log.d("UserFireStoreHandler", "error: ${exception.message}")
                    return@addSnapshotListener
                }
                if (querySnapshot == null) {
                    Log.d("UserFireStoreHandler", "collection hasn't created")
                    return@addSnapshotListener
                }
                val items = mutableListOf<PersonItem>()
                querySnapshot.documents.forEach { documentSnapShot ->
                    Log.d("UserFireStoreHandler", "channelId: ${documentSnapShot["channelId"]}")
                    items.add(
                        PersonItem(
                            "",
                            documentSnapShot.id.trim(),
                            "",
                            documentSnapShot["channelId"] as String,
                            context
                        )
                    )
                }
                onListen(items)
            }
    }

    fun removeListener(registration: ListenerRegistration) = registration.remove()

    fun getOrCreateChatChannel(otherUserId: String?, onComplete: (channelId: String) -> Unit) {
        Log.d("UserFireStoreHandler", "key: ${KeyValueDB.getUserShortId()}")
        if (otherUserId != null) {
            userRef.document(KeyValueDB.getUserShortId())
                .collection("engagedChatChannel")
                .document(otherUserId).get().addOnSuccessListener {
                    if (it.exists()) {
                        onComplete(it["channelId"] as String)
                        return@addOnSuccessListener
                    }
                    val newChannel = chatChannelRef.document()
                    newChannel.set(
                        ChatChannel(
                            mutableListOf(
                                KeyValueDB.getUserShortId(),
                                otherUserId
                            )
                        )
                    )
                    userRef.document(KeyValueDB.getUserShortId())
                        .collection("engagedChatChannel")
                        .document(otherUserId)
                        .set(hashMapOf("channelId" to newChannel.id), SetOptions.merge())
                    userRef.document(otherUserId)
                        .collection("engagedChatChannel")
                        .document(KeyValueDB.getUserShortId())
                        .set(hashMapOf("channelId" to newChannel.id), SetOptions.merge())
                    onComplete(newChannel.id)

                }
        }
    }

    fun addChatListener(
        channelId: String,
        context: Context,
        onListen: (List<Item>) -> Unit
    ): ListenerRegistration {
        return chatChannelRef.document(channelId).collection("messages")
            .orderBy("time")
            .addSnapshotListener(MetadataChanges.INCLUDE) { querySnapshot, exception ->
                if (exception != null) {
                    return@addSnapshotListener
                }
                val items = mutableListOf<Item>()
                querySnapshot!!.documents.forEach {
                    if (it["type"] == MessageType.TEXT) {
                        items.add(TextMessageItem(it.toObject(TextMessage::class.java)!!, context))
                    } else {
                        //image
                        items.add(
                            ImageMessageItem(
                                it.toObject(ImageMessage::class.java)!!,
                                context
                            )
                        )
                    }
                    return@forEach
                }
                onListen(items)
            }
    }

    fun sendMessage(message: Message, channelId: String) {
        chatChannelRef.document(channelId)
            .collection("messages")
            .add(message)
    }

    fun getUserToken(onListen: (MutableList<String>) -> Unit) {
        Log.d("UserFireStoreHandler", "key: ${KeyValueDB.getUserShortId()}")
        userRef.document(KeyValueDB.getUserShortId())
            .get()
            .addOnSuccessListener {
                if (it != null) {
                    if (it["token"] != null) {
                        onListen(it["token"] as MutableList<String>)
                    } else onListen(mutableListOf())
                }
            }
    }

    fun setUserToken(newToken: MutableList<String>) {
        userRef.document(KeyValueDB.getUserShortId())
            .set(hashMapOf("token" to newToken), SetOptions.merge())
            .addOnSuccessListener {

            }
    }

    fun deleteToken(token: String, onComplete: (status: String) -> Unit) {
        Log.d("UserFireStoreHandler", "get user token")

        getUserToken {
            Log.d("UserFireStoreHandler", "get user token: $it")
            if (it.contains(token)) {
                it.remove(token)
                userRef.document(KeyValueDB.getUserShortId())
                    .set(hashMapOf("token" to it), SetOptions.merge())
                    .addOnSuccessListener {
                        KeyValueDB.clearData()
                        onComplete("SUCCESS")
                    }
                    .addOnFailureListener {
                        onComplete("FAILED")
                    }
            } else {
                KeyValueDB.clearData()
                onComplete("SUCCESS")
            }
        }
    }

    fun setLastTextListener(channelId: String, onComplete: (text: String) -> Unit):ListenerRegistration{
        Log.d("UserFireStoreHandler","go to lasst text")
        return chatChannelRef.document(channelId).collection("messages")
            .orderBy("time").addSnapshotListener(MetadataChanges.INCLUDE){ querySnapshot, exception ->
                if (querySnapshot != null) {
                    if(querySnapshot.documents.size > 0){
                        Log.d("UserFireStoreHandler","query1: ${querySnapshot.size()}")
                        val message : Message
                        if(querySnapshot.documents[querySnapshot.size()-1]["type"] == "TEXT") {
                            onComplete(querySnapshot.documents[querySnapshot.size()-1]["text"] as String)
                        }else{
                            if(querySnapshot.documents[querySnapshot.size()-1]["senderId"] == KeyValueDB.getUserShortId()){
                                onComplete("You send an image")
                            }else{
                                onComplete("Other send an image")
                            }
                        }
                    }
//                    val message = querySnapshot.documents[0].toObject(Message::class.java)
//                    Log.d("UserFireStoreHandler","query:2 ${querySnapshot.documents[0].toObject(Message::class.java)}")
//                    if (message != null) {
//                        Log.d("UserFireStoreHandler","query3: ${message.type}")
//                    }

                }

            }
    }

    fun setTagChangedListener(onComplete: (listTag: MutableList<String>) -> Unit):ListenerRegistration{
        return userRef.document(KeyValueDB.getUserShortId())
            .addSnapshotListener(MetadataChanges.INCLUDE){ documentSnapshot, exception ->
                Log.d("UserFireStoreHandle","${documentSnapshot?.get("Tag")} ")
                if(exception != null){
                    onComplete(mutableListOf())
                }
                if(documentSnapshot!= null){
                    onComplete(documentSnapshot.get("Tag") as MutableList<String>)
                }
            }
    }

    data class Resp(var type: String, var status: String, var message: String)
}