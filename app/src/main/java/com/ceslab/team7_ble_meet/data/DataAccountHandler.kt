package com.ceslab.team7_ble_meet.data


import android.util.Log
import android.util.Patterns
import com.ceslab.team7_ble_meet.Model.Account
import com.ceslab.team7_ble_meet.UsersFireStoreHandler
import com.ceslab.team7_ble_meet.isValidEmail
import com.ceslab.team7_ble_meet.isValidPasswordFormat
import java.util.regex.Pattern

class DataAccountHandler() {
    var accountArrayList =  ArrayList<Account>()
    lateinit var signUpCallBack: SignUpCallback
    lateinit var logInCallBack: LogInCallback

    interface SignUpCallback{
        fun resultSignUp(message: String)
        fun onSuccess(message: String)
        fun onFailed(message: String)
        fun onResult(title: String, message: String)
    }

    interface LogInCallback{
        fun resultLogIn(message: String)
    }

    fun logIn(usrNameOrEmail: String, password: String){
        Log.d("TAG","here")
        for(acc in accountArrayList){
            if(usrNameOrEmail == acc.usrName || usrNameOrEmail == acc.email){
                if(password == acc.password){
                    logInCallBack.resultLogIn("Log In Successfully")
                }
                else{
                    logInCallBack.resultLogIn("Password is incorrect")
                }
                return
            }
        }
        logInCallBack.resultLogIn("Account does not exist")
    }

    fun signUp(email:String ,password: String, rePassword: String, msignUpCallback: SignUpCallback){
        signUpCallBack = msignUpCallback
        if(email.isEmpty() || password.isEmpty() || rePassword.isEmpty()){
            signUpCallBack.onFailed("Empty field!")
        }else{
            if(!isValidEmail(email)){
                signUpCallBack.onFailed("Wrong email format!")
                return
            }
            if(!isValidPasswordFormat(password)){
                signUpCallBack.onFailed("Wrong password format")
                return
            }
            if(password != rePassword){
                signUpCallBack.onFailed("Password and and confirm password are not the same!")
                return
            }
            //create user
            createUser(email,password)
        }
    }

    fun createUser(email:String, password: String){
        var instance = UsersFireStoreHandler.instance
        instance.mAuth.createUserWithEmailAndPassword(email,password)
            .addOnCompleteListener{task ->
                if (task.isSuccessful){
                    val note = mutableMapOf<String, String>()
                    note["EMAIL"] = email
                    note["PASS"] = password
                    //save uid to firestore
                    instance.userRef.document(instance.mAuth.currentUser.uid)
                        .set(note).addOnSuccessListener {
                            Log.d("TAG","add new users successful")
                            signUpCallBack.onSuccess("add new users successful!")

                        }
                        .addOnFailureListener{
                            //on failed add user to firestore
                            Log.d("TAG","Fail: $it")
                            signUpCallBack.onSuccess("add new users failed!")
                        }

                }else{
                    task.exception?.let {
                        //on failed create user by email and password
                    }
                }
            }
            .addOnFailureListener{
                Log.d("TAG","Fail: $it")
                signUpCallBack.onFailed(it.message.toString())
            }
    }

    private fun checkFormatUsrName(usrname: String): String{
        if(usrname.isEmpty()) return "Empty Username!"
        for(i in usrname) {
            if (i.isWhitespace()) return "Username has white space!"
        }
        val regex = mapOf(  "Username has less than 6 characters!" to ".{6,}" )
        for((key, value) in regex) {
            Log.d("TAG", Pattern.compile("^$value$").matcher(usrname).matches().toString())
            if(!Pattern.compile("^$value$").matcher(usrname).matches()) {
                return key
            }
        }
        return ""
    }
    private fun checkFormatEmail(email: String): String {
        if(email.isEmpty()) return "Empty Email"
        if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()) return "Invalid Email Format"
        return ""
    }
    private fun checkFormatPassword(password: String): String {
        if(password.isEmpty()) return "Empty Password"
        val regex = mapOf(  "Password have to be at least 8 characters!" to "{8,}",
                            "Password have white spaces!" to "(?=\\S+$)",
                            "Password have to be at least 1 lower case letter!" to "(?=.*[a-z])",
                            "Password have to be at least 1 upper case letter!" to "(?=.*[A-Z])",
                            "Password have to be at least 1 digit!" to "(?=.*[0-9])",
                            "Password have to be consist of a-z and A-Z" to "(?=.*[a-zA-Z])",
                            "Password have to be at least 1 special character!" to "(?=.*[!@#$%^&*()])"
                            )
        for((key, value) in regex) {
            if(Pattern.compile("^$value$").matcher(password).matches()) {
                return key
            }
        }
        return ""
    }
}