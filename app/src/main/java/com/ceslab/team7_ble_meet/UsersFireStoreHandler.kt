package com.ceslab.team7_ble_meet

import android.net.Uri
import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.ceslab.team7_ble_meet.repository.KeyValueDB
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.storage.FirebaseStorage

class UsersFireStoreHandler {

    var userRef = FirebaseFirestore.getInstance().collection("Users")
    var uidRef = FirebaseFirestore.getInstance().collection("UUID")
    var imageRef = FirebaseStorage.getInstance().reference.child("images")
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
//                KeyValueDB.setUserTag(true)
//                KeyValueDB.setRegister(true)
                KeyValueDB.setUserTag(true)
            }
            .addOnFailureListener {
                userResp.postValue(Resp("NONE", "FAILED", "update tag failed!"))
//                KeyValueDB.setUserTag(false)
            }
    }

    fun updateAvatar(uri: Uri) {
        Log.d("UserFireStoreHandler", "shortId: ${getUserShortId()}")

        imageRef.child(KeyValueDB.getUserShortId()).child("avatar")
//        imageRef.child("/${shortId}/avatar")
            .putFile(uri)
            .addOnSuccessListener {
                userResp.postValue(Resp("NONE", "SUCCESS", "update avatar successful!"))
                uidRef.document(KeyValueDB.getUserId())
                    .set(hashMapOf("isRegisterAvatar" to true), SetOptions.merge())
                KeyValueDB.setUserAvatar(true)
                KeyValueDB.setRegister(true)
            }
            .addOnFailureListener {
                userResp.postValue(Resp("NONE", "FAILED", "update avatar failed!"))
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
                            userResp.postValue(Resp("LOGIN", "SUCCESS", "DASHBOARD"))
                        }
                    }
                }
        }
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

    data class Resp(var type: String, var status: String, var message: String)
}