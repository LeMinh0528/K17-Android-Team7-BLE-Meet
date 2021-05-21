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

    fun updateGender(userId: String, gender: String,inter: String){
        val note = mutableMapOf<String,String>()
        note["Gender"] = gender
        note["Interested"] = inter
        userRef.document(userId)
            .set(note, SetOptions.merge())
            .addOnSuccessListener {
                userResp.postValue(Resp("SUCCESS","update gender successful!"))
                KeyValueDB.setUserGender(gender)
                KeyValueDB.setUserInterested(inter)
            }
            .addOnFailureListener{
                userResp.postValue(Resp("FAILED","update gender failed!"))

            }
    }

    fun updateTag(userId:String, list: MutableList<String>){
        var note = mutableMapOf<String,MutableList<String>>()
        note["Tag"] = list
        userRef.document(userId)
            .set(note, SetOptions.merge())
            .addOnSuccessListener {
                userResp.postValue(Resp("SUCCESS","update tag successful!"))
                KeyValueDB.setUserTag(true)
            }
            .addOnFailureListener{
                userResp.postValue(Resp("FAILED","update tag failed!"))

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

                            KeyValueDB.setFirstTimeRegister(true)
                            KeyValueDB.setUserId(mAuth.currentUser.uid)
                            userResp.postValue(Resp("SUCCESS","add new users successful"))
                        }
                        .addOnFailureListener{
                           //on failed add user to firestore
                            Log.d("TAG","Fail: ${it.message}")
                            userResp.postValue(it.message?.let { it1 -> Resp("FAILED", it1) })
                        }

                }else if(task.isCanceled){
                    userResp.postValue(null)
                }
            }
            .addOnFailureListener{
                Log.d("TAG","Fail: ${it.message}")
                userResp.postValue(it.message?.let { it1 -> Resp("FAILED", it1) })
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


    data class Resp(var type: String, var message: String)
}