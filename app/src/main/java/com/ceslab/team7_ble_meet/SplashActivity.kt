package com.ceslab.team7_ble_meet

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import com.ceslab.team7_ble_meet.dashboard.DashBoardActivity
import com.ceslab.team7_ble_meet.login.LogInActivity
import com.ceslab.team7_ble_meet.registerInformation.RegisterBirthdayActivity
import com.ceslab.team7_ble_meet.registerInformation.RegisterGenderActivity
import com.ceslab.team7_ble_meet.registerInformation.RegisterTagActivity
import com.ceslab.team7_ble_meet.registerInformation.RegisterUserNameActivity
import com.ceslab.team7_ble_meet.repository.KeyValueDB

class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)
        Handler().postDelayed({
            checkAuth()
        }, 2200)
    }
    override fun onPause() {
        super.onPause()
        overridePendingTransition(0, 0)
    }

    private fun checkAuth(){
        KeyValueDB.createRef(this)
        val selected = KeyValueDB.isRegister()
        val uid = KeyValueDB.getUserId()
        var id = KeyValueDB.getUserShortId()
        Log.d("SplashActivity","id: $id")
        if(uid == "" || !selected){
            //if not registed
            val intent = Intent(this@SplashActivity,LogInActivity::class.java ).apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                }
                startActivity(intent)
        }else{
            val intent = Intent(this@SplashActivity,DashBoardActivity::class.java ).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            }
            startActivity(intent)
            //if registered
//            if(selected){

//            }else{ // if registering
//                val gender = KeyValueDB.isRegisterUserGender()
//                val tag = KeyValueDB.getUserTag()
//                val username = KeyValueDB.isRegisterUserName()
//                val dob = KeyValueDB.isRegisterDOB()
//                if(!username) {
//                    val intent = Intent(this@SplashActivity, RegisterUserNameActivity::class.java).apply {
//                            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
//                        }
//                    startActivity(intent)
//                    return
//                }else if(!gender) {
//                    val intent = Intent(this@SplashActivity, RegisterGenderActivity::class.java).apply {
//                        flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
//                    }
//                    startActivity(intent)
//                    return
//                }else if(!dob){
//                    val intent = Intent(this@SplashActivity, RegisterBirthdayActivity::class.java).apply {
//                        flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
//                    }
//                    startActivity(intent)
//                    return
//                }
//                else if (!tag){
//                    val intent = Intent(this@SplashActivity,RegisterTagActivity::class.java ).apply {
//                        flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
//                    }
//                    startActivity(intent)
//                    return
//                }

//            }
        }
//        if(!selected){
//            //go to sign up
//            val userId = KeyValueDB.getUserId()
//            if(userId == ""){
//                val intent = Intent(this@SplashActivity,SignUpActivity::class.java ).apply {
//                    flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
//                }
//                startActivity(intent)
//            }else{
//                val gender = KeyValueDB.getUserGender()
//                val tag = KeyValueDB.getUserTag()
//                if(gender == ""){
//                    val intent = Intent(this@SplashActivity,RegisterGenderActivity::class.java ).apply {
//                        flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
//                    }
//                    startActivity(intent)
//                    return
//                }else if(!tag){
//                    val intent = Intent(this@SplashActivity,RegisterTagActivity::class.java ).apply {
//                        flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
//                    }
//                    startActivity(intent)
//                    return
//                }
//            }
//        }else{
//            val userId = KeyValueDB.getUserId()
//            if(userId != ""){
//                val intent = Intent(this@SplashActivity,DashBoardActivity::class.java ).apply {
//                    flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
//                }
//                startActivity(intent)
//            }else{
//                val intent = Intent(this@SplashActivity,LogInActivity::class.java ).apply {
//                    flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
//                }
//                startActivity(intent)
//            }
//        }
        Log.d("TAG","Selected: $selected")
    }
}