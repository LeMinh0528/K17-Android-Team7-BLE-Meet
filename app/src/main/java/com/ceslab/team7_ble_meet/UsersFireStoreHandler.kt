package com.ceslab.team7_ble_meet

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class UsersFireStoreHandler private constructor() {
    private var instance  = UsersFireStoreHandler()
    private var noteRef = FirebaseFirestore.getInstance().collection("Users")
    private var mAuth : FirebaseAuth = FirebaseAuth.getInstance()

    fun queryUserByEmail(email: String): ArrayList<String>? {
        var listquery = ArrayList<String>()
        noteRef.whereEqualTo("email","email")
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

    fun getAllData(){
        noteRef.get().addOnSuccessListener {querySnapshot ->
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
                    noteRef.document(mAuth.currentUser.uid)
                        .set(note).addOnSuccessListener {
                            Log.d("ChatsFragment","add new users successful")
                        }
                        .addOnFailureListener{
                           //on failed add user to firestore
                        }

                }else{
                    task.exception?.let {
                        //on failed create user by email and password
                    }
                }
            }
    }

    fun getFriendsFromID(id: String): ArrayList<String>?{
        var list = ArrayList<String>()
        noteRef.document(id)
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
}