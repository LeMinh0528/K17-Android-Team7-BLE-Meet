package com.ceslab.team7_ble_meet

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.android.volley.DefaultRetryPolicy
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.ceslab.team7_ble_meet.Model.*
import com.ceslab.team7_ble_meet.dashboard.recyclerview.ImageMessageItem
import com.ceslab.team7_ble_meet.dashboard.recyclerview.PersonItem
import com.ceslab.team7_ble_meet.dashboard.recyclerview.TextMessageItem
import com.ceslab.team7_ble_meet.model.User
import com.ceslab.team7_ble_meet.repository.KeyValueDB
import com.ceslab.team7_ble_meet.service.MessagingService
import com.ceslab.team7_ble_meet.utils.ImagesStorageUtils
import com.firebase.ui.auth.AuthUI
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.*
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.storage.FirebaseStorage
import com.xwray.groupie.kotlinandroidextensions.Item
import org.json.JSONObject
import java.security.Key
import java.util.*
import kotlin.collections.ArrayList

class UsersFireStoreHandler {
    private val fireStoreInstance: FirebaseFirestore by lazy { FirebaseFirestore.getInstance() }
    private val storageInstance: FirebaseStorage by lazy { FirebaseStorage.getInstance() }

    var userRef = fireStoreInstance.collection("Users")
    var uidRef = fireStoreInstance.collection("UUID")
    var imageRef = storageInstance.reference.child("images")
    var chatChannelRef = fireStoreInstance.collection("chatChannels")
    var mAuth: FirebaseAuth = FirebaseAuth.getInstance()
    var userResp = MutableLiveData<Resp?>()

    fun setStatus(userId: String) {
        userRef.document(userId)
            .set(hashMapOf("Status" to 1))
    }

    fun getUserSize(): Int {
        var num = 0
        userRef.get()
            .addOnSuccessListener {
                if (it != null) {
                    num = it.size()
                }
            }
        return num
    }


    fun updateBirthDay(birthDay: String) {
        var note = mutableMapOf<String, String>()
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
        var note = mutableMapOf<String, String>()
        note["Name"] = name
        userRef.document(KeyValueDB.getUserShortId())
            .set(note, SetOptions.merge())
            .addOnSuccessListener {
                uidRef.document(KeyValueDB.getUserId())
                    .set(hashMapOf("isRegisterName" to true), SetOptions.merge())
                userResp.postValue(Resp("NONE", "SUCCESS", "update tag successful!"))
//                KeyValueDB.setUserName(true)
            }
            .addOnFailureListener {
                userResp.postValue(Resp("NONE", "FAILED", "update tag failed!"))
//                KeyValueDB.setUserName(false)
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

    fun updateTag(list: MutableList<String>) {
        var note = mutableMapOf<String, MutableList<String>>()
        note["Tag"] = list
        userRef.document(KeyValueDB.getUserShortId())
            .set(note, SetOptions.merge())
            .addOnSuccessListener {
                uidRef.document(KeyValueDB.getUserId())
                    .set(hashMapOf("isRegisterTag" to true), SetOptions.merge())
                userResp.postValue(Resp("NONE", "SUCCESS", "update tag successful!"))
                KeyValueDB.setUserTag(true)
            }
            .addOnFailureListener {
                userResp.postValue(Resp("NONE", "FAILED", "update tag failed!"))
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
                        UsersFireStoreHandler.Resp(
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
//        var ref = imageRef.child(KeyValueDB.getUserShortId()).child("avatar")
//            ref.putFile(uri)
//            .addOnSuccessListener {
//                ref.downloadUrl.addOnSuccessListener {
//                    userRef.document(KeyValueDB.getUserShortId()).set(hashMapOf("avatar" to it.toString()), SetOptions.merge())
//                        .addOnSuccessListener {
//                            userResp.postValue(Resp("NONE", "SUCCESS", "update avatar successful!"))
//                            uidRef.document(KeyValueDB.getUserId())
//                                .set(hashMapOf("isRegisterAvatar" to true), SetOptions.merge())
//                            KeyValueDB.setUserAvatar(true)
//                            KeyValueDB.setRegister(true)
//                        }
//                }
//            }
//            .addOnFailureListener {
//                userResp.postValue(Resp("NONE", "FAILED", "update avatar failed!"))
//            }
    }

    fun uploadProfilePhoto(
        imageBytes: ByteArray,
        onSuccess: (imagePath: String) -> Unit
    ) {
        val ref = imageRef.child("profilePictures/${UUID.nameUUIDFromBytes(imageBytes)}")
        ref.putBytes(imageBytes)
            .addOnSuccessListener {
                onSuccess(ref.path)
            }
    }

    fun queryUserByEmail(mEmail: String): ArrayList<String>? {
        //query account voi field = value da cho
        var listquery = ArrayList<String>()
        userRef.whereEqualTo("email", mEmail)
            .get()
            .addOnSuccessListener { query ->
                Log.d("ChatsFragments: ", "doc ${query} ")
                if (query != null) {
                    for (i in query) {
                        Log.d("ChatsFragments: ", "doc ${i.data} ")
                        listquery.add(i.data["EMAIL"].toString())
                    }
                }
            }
            .addOnFailureListener {
                Log.d("ChatsFragments: ", "doc: ${it.message}")
            }
        if (listquery.isEmpty()) {
            return null
        } else {
            return listquery
        }
    }

    fun getUserData(id: String) {
        userRef.document(id)
            .get()
            .addOnSuccessListener {
                if (it != null) {
                    Log.d("UserFireStoreHandler", "user info: $it")
                }
            }
    }

    fun getAllData() {
        userRef.get().addOnSuccessListener { querySnapshot ->
            if (querySnapshot != null) {
//                Log.d("ChatFragments","doc: ${it}")
                for (i in querySnapshot) {
                    Log.d("ChatFragments", "doc: ${i.data}")
                    Log.d("ChatFragments", "doc: ${i.id}")
                    Log.d("ChatFragments", "doc: ${i.data["Name"]}")
                }
            } else {
                Log.d("ChatFragments", "can't get data")
            }
        }.addOnFailureListener {
            Log.d("ChatFragments", "$it")
        }
    }

    fun getListUserShortId(): MutableList<String> {
        var list: MutableList<String> = mutableListOf()
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

    fun getUserShortId(): String {
        var id = ""
        Log.d("UserFireStoreHandler", "shortId: ${mAuth.currentUser?.uid}")
        mAuth.currentUser?.let {
            uidRef.document(it.uid)
                .get()
                .addOnSuccessListener {
                    if (it != null) {
                        id = it["shortID"] as String
                    }
                }
        }
        return id
    }

    fun getUserSetting() {
        mAuth.currentUser?.let {
            uidRef.document(it.uid)
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

    fun getCurrentToken(){
        FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
            if (!task.isSuccessful) {
                Log.d("UserFireStoreHandler", "Fetching FCM registration token failed", task.exception)
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

    fun randomUId(): String {
        var uid: String = generateUniqueID()
        var list: MutableList<String> = getListUserShortId()
        if (list.isEmpty()) {
            return uid
        } else {
            while (list.indexOf(uid) != -1) {
                uid = generateUniqueID()
            }
            return uid
        }
    }

    fun createUser(email: String, pass: String) {
        //get userAll uniqueid
        var uid = randomUId()
        mAuth.createUserWithEmailAndPassword(email, pass)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    var note = mutableMapOf<String, String>()
                    getCurrentToken()
                    note["EMAIL"] = email
                    note["PASS"] = pass
                    userRef.document(uid)
                        .set(note).addOnSuccessListener {
                            mAuth.currentUser?.let { it1 -> KeyValueDB.setUserId(it1.uid) }
                            KeyValueDB.setUserShortId(uid)
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

//                    if(list.isEmpty()){
//                        var random = generateUniqueID()
//                        userRef.document(random)
//                            .set(note).addOnSuccessListener {
//                                Log.d("TAG","add new users successful id: ${mAuth.currentUser.uid}")
//                                KeyValueDB.setUserId(mAuth.currentUser.uid)
//                                KeyValueDB.setUserShortId(random)
//                                uidRef.document(mAuth.currentUser.uid)
//                                    .set(hashMapOf("shortID" to random))
//                                userResp.postValue(Resp("NONE","SUCCESS","add new users successful"))
//                            }
//                            .addOnFailureListener{
//                                //on failed add user to firestore
//                                Log.d("TAG","Fail: ${it.message}")
//                                userResp.postValue(it.message?.let { it1 -> Resp("NONE","FAILED", it1) })
//                            }
//                    }else{
//                        var random = generateUniqueID()
//                        while(list.indexOf(random) != -1){
//                            random = generateUniqueID()
//                        }
//                        userRef.document(random)
//                            .set(note).addOnSuccessListener {
//                                KeyValueDB.setUserId(mAuth.currentUser.uid)
//                                KeyValueDB.setUserShortId(random)
//                                uidRef.document(mAuth.currentUser.uid)
//                                    .set(hashMapOf("shortID" to random))
//                                userResp.postValue(Resp("NONE","SUCCESS","add new users successful"))
//                            }
//                            .addOnFailureListener{
//                                userResp.postValue(it.message?.let { it1 -> Resp("NONE","FAILED", it1) })
//                            }
//                    }

                } else if (task.isCanceled) {
                    userResp.postValue(Resp("NONE", "FAILED", "ERROR"))
                }
            }
            .addOnFailureListener {
                Log.d("TAG", "Fail: ${it.message}")
                userResp.postValue(it.message?.let { it1 -> Resp("NONE", "FAILED", it1) })
            }
    }

    fun getCurrentUser(id: String,onComplete: (User) -> Unit) {
        userRef.document(id).get()
            .addOnSuccessListener {
                onComplete(it.toObject(User::class.java)!!)
            }
    }

    fun logInUser(email: String, pass: String) {
        mAuth.signInWithEmailAndPassword(email, pass)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Log.d("UserFireStoreHandler", "${mAuth.currentUser}")
                    getUserSetting()
//                    KeyValueDB.setUserId(mAuth.currentUser.uid)
//                    KeyValueDB.setUserShortId(getUserShortId())
//                    KeyValueDB.setRegister(true)
//                    userResp.postValue(Resp("NONE","SUCCESS","Login successful!"))
                } else if (task.isCanceled) {
                    mAuth.currentUser?.let { KeyValueDB.setUserId(it.uid) }
                    userResp.postValue(Resp("NONE", "SUCCESS", "Login failed!"))
                }
            }
            .addOnFailureListener {
                userResp.postValue(Resp("NONE", "FAILED", "Login failed! ${it.message}"))
            }
    }

    fun getFriendsFromID(id: String): ArrayList<String>? {
        var list = ArrayList<String>()
        userRef.document(id)
            .get()
            .addOnSuccessListener {
                if (it != null) {
                    list = it.get("ListFriend") as ArrayList<String>
                    Log.d("ChatsFragment", "list friends: ${it.get("ListFriend")}")
                    Log.d("ChatsFragment", "chat: $list")
                }
            }
        if (list.isEmpty()) {
            return null
        } else {
            return list
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
//        if(userRef.document(KeyValueDB.getUserShortId()).collection("engagedChatChannel"))
        return userRef.document(KeyValueDB.getUserShortId())
            .collection("engagedChatChannel")
            .addSnapshotListener { querySnapshot, exception ->
                if (exception != null) {
                    Log.d("UserFireStoreHandler", "error: ${exception.message}")
                    return@addSnapshotListener
                }
                if(querySnapshot == null){
                    Log.d("UserFireStoreHandler", "collection hasn't created")
                    return@addSnapshotListener
                }
                val items = mutableListOf<PersonItem>()
                querySnapshot?.documents?.forEach { documentSnapShot ->
//                    val person = documentSnapShot.toObject(Person::class.java)
//                    if (person != null) {
                    Log.d("UserFireStoreHandler", "channelId: ${documentSnapShot["channelId"]}")
                        items.add(PersonItem("",documentSnapShot.id.trim(),"",documentSnapShot["channelId"] as String, context))
//                    }
                    onListen(items)
//                    Log.d("UserFireStoreHandler","size: querySransot size: ${querySnapshot.size()}")
//                    userRef.document(documentSnapShot.id)
//                        .get().addOnSuccessListener {
//                            Log.d("UserFireStoreHandler","size: item size: ${items.size}")
//                            val m = it.toObject(User::class.java)
//                            if(m != null){
//                                Log.d("UserFireStoreHandler","data: ${m.Name}")
//                                items.add(PersonItem(m.Name))
//
//                                if(items.size == querySnapshot.size()-1){
//                                    onListen(items)
//                                }
//                            }
//
//                        }

//                    userRef.document(documentSnapShot["Name"].toString())
//                    items.add(PersonItem(documentSnapShot.id))
                }

            }
    }

    fun removeListener(registration: ListenerRegistration) = registration.remove()

    fun getOrCreateChatChannel(otherUserId: String?, onComplete: (channelId: String) -> Unit) {
        Log.d("UserFireStoreHandler","if exists:$otherUserId")
        if (otherUserId != null) {
            userRef.document(KeyValueDB.getUserShortId())
                .collection("engagedChatChannel")
                .document(otherUserId).get().addOnSuccessListener {
                    Log.d("UserFireStoreHandler","if exists: ${it["channelId"]}")
                    //neu co ton tai 1 kenh chat cho 2 dua
                    if (it.exists()) {
                        Log.d("UserFireStoreHandler","if exists: ${it["channelId"]}")
                        onComplete(it["channelId"] as String)
                        return@addOnSuccessListener
                    }
                    //neu chua co -> create new
                    val newChannel = chatChannelRef.document()
                    //set new channel in ChatChannels collection
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
                        .set(hashMapOf("channelId" to newChannel.id),SetOptions.merge())
                    //set new channel in engagedChatChannel in other user
                    userRef.document(otherUserId)
                        .collection("engagedChatChannel")
                        .document(KeyValueDB.getUserShortId())
                        .set(hashMapOf("channelId" to newChannel.id),SetOptions.merge())
                    onComplete(newChannel.id)
                    //set new channel in endgagedChatChannel in current user
//                    userRef.document(KeyValueDB.getUserShortId())
//                        .get()
//                        .addOnSuccessListener {
//                            if (it != null) {
////                                val person = it.toObject(Person::class.java)
//                                userRef.document(KeyValueDB.getUserShortId())
//                                    .collection("engagedChatChannel")
//                                    .document(otherUserId)
//                                    .set("channelId" to newChannel.id, SetOptions.merge())
//                                    userRef.document(KeyValueDB.getUserShortId())
//                                        .collection("engagedChatChannel")
//                                        .document(otherUserId)
//                                        .set(hashMapOf("Name" to it["Name"]
//                                            ,"avatar" to it["avatar"]), SetOptions.merge())
//                            }
//                        }
//                    userRef.document(otherUserId)
//                        .get()
//                        .addOnSuccessListener {
//                            if (it != null) {
//                                userRef.document(otherUserId)
//                                    .collection("engagedChatChannel")
//                                    .document(KeyValueDB.getUserShortId())
//                                    .set("channelId" to newChannel.id, SetOptions.merge())
//                                    userRef.document(otherUserId)
//                                        .collection("engagedChatChannel")
//                                        .document(KeyValueDB.getUserShortId())
//                                        .set(hashMapOf("Name" to it["Name"]
//                                            ,"avatar" to it["avatar"]), SetOptions.merge())
//                            }
//                        }

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
            .addSnapshotListener { querySnapshot, exception ->
                if (exception != null) {
                    return@addSnapshotListener
                }
                val items = mutableListOf<Item>()
                querySnapshot!!.documents.forEach {
                    if (it["type"] == MessageType.TEXT) {
                        items.add(TextMessageItem(it.toObject(TextMessage::class.java)!!, context))
                    } else {
                        //image
                        items.add(ImageMessageItem(it.toObject(ImageMessage::class.java)!!,context))
                    }
                    return@forEach
                }
                onListen(items)
            }
    }

    fun sendMessage(message: Message, otherUserId: String, channelId: String) {
        chatChannelRef.document(channelId)
            .collection("messages")
            .add(message)
//        userRef.document(KeyValueDB.getUserShortId())
//            .collection("engagedChatChannel")
//            .document(otherUserId)
//            .set(hashMapOf("lastMessageType" to message.type
//                ,"lastMessageText" to message.))
//        userRef.document(otherUserId)
//            .collection("engagedChatChannel")
//            .document(KeyValueDB.getUserShortId())
//            .collection("lastMessage")
//            .add(message)
    }




    fun getUserToken(onListen: (MutableList<String>) -> Unit){
        userRef.document(KeyValueDB.getUserShortId())
            .get()
            .addOnSuccessListener {
                if (it != null) {
                    if (it["token"] != null) {
                        onListen(it["token"] as MutableList<String>)
                    }else onListen(mutableListOf())
                }
            }
    }

    fun setUserToken(newToken: MutableList<String>){
        userRef.document(KeyValueDB.getUserShortId())
            .set(hashMapOf("token" to newToken),SetOptions.merge())
    }


    data class Resp(var type: String, var status: String, var message: String)
}