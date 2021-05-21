package com.ceslab.team7_ble_meet

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import com.ceslab.team7_ble_meet.dashboard.DashBoardActivity
import com.ceslab.team7_ble_meet.registerInformation.RegisterGenderActivity
import com.ceslab.team7_ble_meet.registerInformation.RegisterTagActivity
import com.ceslab.team7_ble_meet.repository.KeyValueDB
import com.ceslab.team7_ble_meet.signup.SignUpActivity

class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)
//        val intent = Intent(this@SplashActivity, DashBoardActivity::class.java).apply {
//            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
//        }
        Handler().postDelayed({
//            startActivity(intent)
            checkAuth()
        }, 2200)
    }
    override fun onPause() {
        super.onPause()
        overridePendingTransition(0, 0)
    }

    private fun checkAuth(){
        KeyValueDB.createRef(this)
        val selected = KeyValueDB.isFirstTimeRegister()
        if(!selected){
            //go to sign up
            val intent = Intent(this@SplashActivity,SignUpActivity::class.java ).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)
            }
            startActivity(intent)
            return
        }else{
            //go to sign in
            val gender = KeyValueDB.getUserGender()
            val tag = KeyValueDB.getUserTag()
            if(gender == ""){
                val intent = Intent(this@SplashActivity,RegisterGenderActivity::class.java ).apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                }
                startActivity(intent)
                return
            }else if(!tag){
                val intent = Intent(this@SplashActivity,RegisterTagActivity::class.java ).apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                }
                startActivity(intent)
                return
            }
            //go to register gender
            //go to register birthDay
            //go to register tag
            //go to dash board
        }
        Log.d("TAG","Selected: $selected")
    }
}