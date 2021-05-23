package com.ceslab.team7_ble_meet

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.ceslab.team7_ble_meet.repository.KeyValueDB
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions

class UsersFireStoreHandler {

    var userRef = FirebaseFirestore.getInstance().collection("Users")
    var mAuth : FirebaseAuth = FirebaseAuth.getInstance()
    var userResp = MutableLiveData<Resp?>()

    fun setStatus(userId: String){
        userRef.document(userId)
            .set(hashMapOf("Status" to 1))
    }

    fun updateName(name: String){
        var note = mutableMapOf<String,String>()
        note["Name"] = name
        userRef.document(mAuth.currentUser.uid)
            .set(note, SetOptions.merge())
            .addOnSuccessListener {
                userResp.postValue(Resp("NONE","SUCCESS","update tag successful!"))
                KeyValueDB.setUserName(true)
            }
            .addOnFailureListener{
                userResp.postValue(Resp("NONE","FAILED","update tag failed!"))
            }
    }

    fun getGender(): String?{
        var gender : String? = null
        userRef.document(mAuth.currentUser.uid)
            .get()
            .addOnSuccessListener { query->
                if(query != null){
                    Log.d("UserFireStoreHandler","query: ${query.data?.get("Gender")}")
                    gender = query.data?.get("Gender") as String?
                }
            }
        return gender
    }

    fun updateGender(userId: String, gender: String?,inter: String){
        val note = mutableMapOf<String,String?>()
        note["Gender"] = gender
        note["Interested"] = inter
        userRef.document(userId)
            .set(note, SetOptions.merge())
            .addOnSuccessListener {
                userResp.postValue(Resp("NONE","SUCCESS","update gender successful!"))
                KeyValueDB.setRegisterUserGender(true)
            }
            .addOnFailureListener{
                userResp.postValue(Resp("NONE","FAILED","update gender failed!"))

            }
    }

    fun updateTag(userId:String, list: MutableList<String>){
        var note = mutableMapOf<String,MutableList<String>>()
        note["Tag"] = list
        userRef.document(userId)
            .set(note, SetOptions.merge())
            .addOnSuccessListener {
                userResp.postValue(Resp("NONE","SUCCESS","update tag successful!"))
                KeyValueDB.setUserTag(true)
            }
            .addOnFailureListener{
                userResp.postValue(Resp("NONE","FAILED","update tag failed!"))
            }
    }

    fun queryUserByEmail(mEmail: String): ArrayList<String>? {
        var listquery = ArrayList<String>()
        userRef.whereEqualTo("email","email")
            .get()
            .addOnSuccessListener {query ->
                Log.d("ChatsFragments: ","doc ${query} ")
                if(query != null){
                    for(i in query){
                        Log.d("ChatsFragments: ","doc ${i.data} ")
                        listquery.add(i.data["EMAIL"].toString())
                    }
                }
            }
            .addOnFailureListener{
                Log.d("ChatsFragments: ","doc: ${it.message}")
            }
        if(listquery.isEmpty()){
            return null
        }else{
            return listquery
        }
    }

    fun getUserData(id: String){
        userRef.document(id)
            .get()
            .addOnSuccessListener {
                if(it!= null){
                    Log.d("UserFireStoreHandler","user info: $it")
                }
            }
    }

    fun getAllData(){
        userRef.get().addOnSuccessListener { querySnapshot ->
            if(querySnapshot != null){
//                Log.d("ChatFragments","doc: ${it}")
                for(i in querySnapshot){
                    Log.d("ChatFragments","doc: ${i.data}")
                    Log.d("ChatFragments","doc: ${i.id}")
                    Log.d("ChatFragments","doc: ${i.data["Name"]}")
                }
            }else{
                Log.d("ChatFragments","can't get data")
            }
        }.addOnFailureListener{
            Log.d("ChatFragments","$it")
        }
    }

    fun createUser(email: String, pass: String){
        mAuth.createUserWithEmailAndPassword(email,pass)
            .addOnCompleteListener{task ->
                if (task.isSuccessful){
                    Log.d("ChatsFragment","${mAuth.currentUser}")
                    Log.d("ChatsFragment", mAuth.currentUser.uid)
                    var note = mutableMapOf<String, String>()
                    note.put("EMAIL",email)
                    note.put("PASS", pass)
                    //save uid to firestore
                    userRef.document(mAuth.currentUser.uid)
                        .set(note).addOnSuccessListener {
                            Log.d("TAG","add new users successful")
                            Log.d("TAG","add new users successful id: ${mAuth.currentUser.uid}")
                            KeyValueDB.setUserId(mAuth.currentUser.uid)
                            userResp.postValue(Resp("NONE","SUCCESS","add new users successful"))
                        }
                        .addOnFailureListener{
                           //on failed add user to firestore
                            Log.d("TAG","Fail: ${it.message}")
                            userResp.postValue(it.message?.let { it1 -> Resp("NONE","FAILED", it1) })
                        }

                }else if(task.isCanceled){
                    userResp.postValue(null)
                }
            }
            .addOnFailureListener{
                Log.d("TAG","Fail: ${it.message}")
                userResp.postValue(it.message?.let { it1 -> Resp("NONE","FAILED", it1) })
            }
    }

    fun logInUser(email: String, pass: String){
        mAuth.signInWithEmailAndPassword(email,pass)
            .addOnCompleteListener{task ->
                if(task.isSuccessful){
                    Log.d("UserFireStoreHandler","${mAuth.currentUser}")
                    KeyValueDB.setUserId(mAuth.currentUser.uid)
                    KeyValueDB.setRegister(true)
                    userResp.postValue(Resp("NONE","SUCCESS","Login successful!"))
                }else if(task.isCanceled){
                    KeyValueDB.setUserId(mAuth.currentUser.uid)
                    userResp.postValue(Resp("NONE","SUCCESS","Login failed!"))
                }
            }
            .addOnFailureListener{
                userResp.postValue(Resp("NONE","FAILED","Login failed! ${it.message}"))
            }
    }

    fun getFriendsFromID(id: String): ArrayList<String>?{
        var list = ArrayList<String>()
        userRef.document(id)
            .get()
            .addOnSuccessListener {
                if(it != null){
                    list = it.get("ListFriend") as ArrayList<String>
                    Log.d("ChatsFragment", "list friends: ${it.get("ListFriend")}")
                    Log.d("ChatsFragment","chat: $list")
                }
            }
        if(list.isEmpty()){
            return null
        }else{
            return list
        }
    }

    fun deleteData(){
        userRef.document(mAuth.currentUser.uid).delete()
            .addOnSuccessListener {
                mAuth.currentUser.delete().addOnSuccessListener {
                    userResp.postValue(Resp("DELETE","SUCCESS",""))
                }
                    .addOnFailureListener{
                        userResp.postValue(Resp("DELETE","FAILED","${it.message}"))
                    }
            }
            .addOnFailureListener{
                userResp.postValue(Resp("DELETE","FAILED",""))
            }
        KeyValueDB.clearData()
    }

    data class Resp(var type: String,var status: String, var message: String)
}