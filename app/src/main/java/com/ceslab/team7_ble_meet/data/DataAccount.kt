package com.ceslab.team7_ble_meet.data


import android.util.Log
import android.util.Patterns
import com.ceslab.team7_ble_meet.Model.Account
import java.util.regex.Pattern

class DataAccount private constructor() {
    var accountArrayList =  ArrayList<Account>()
    lateinit var signUpCallBack: SignUpCallback
    lateinit var logInCallBack: LogInCallback

    companion object{
        var accountList = DataAccount()
    }

    interface SignUpCallback{
        fun resultSignUp(message: String)
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

    fun signUp(account: Account, rePassword: String){
        Log.d("TAG","username: ${account.usrName}, email: ${account.email}, password: ${account.password}, repassword: $rePassword")
        var check = checkFormatUsrName(account.usrName)
        if(check.isNotEmpty()){
            signUpCallBack.resultSignUp(check)
            return
        }
        for(acc in accountArrayList){
            if(account.usrName == acc.usrName){
                signUpCallBack.resultSignUp("Username has been used")
                return
            }
            if(account.email == acc.email){
                signUpCallBack.resultSignUp("Username has been used")
                return
            }
        }
        check = checkFormatEmail(account.email)
        if(check.isNotEmpty()){
            signUpCallBack.resultSignUp(check)
            return
        }

        check = checkFormatPassword(account.email)
        if(check.isNotEmpty()){
            signUpCallBack.resultSignUp(check)
            return
        }
        for(acc in accountArrayList){
            if(account.usrName == acc.usrName || account.email == acc.email){
                signUpCallBack.resultSignUp("Account has been registered!")
                return
            }
        }
        if(account.password != rePassword){
            signUpCallBack.resultSignUp("Confirm password is not correct")
            return
        }

        val temp = Account(account.usrName, account.email, account.password)
        accountArrayList.add(temp)
//        accountArrayList.add(Account("minh","minh@gmail.com","minhbede"))
//        accountArrayList.add(Account("phat","phat@gmail.com","phatbede"))
//        accountArrayList.add(Account("phuc","phuc@gmail.com","phucbede"))
//        accountArrayList.add(Account("khang","khang@gmail.com","khangbede"))
        signUpCallBack.resultSignUp("Sign Up Successfully")
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