package com.ceslab.team7_ble_meet

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import com.ceslab.team7_ble_meet.dashboard.DashBoardActivity
import com.ceslab.team7_ble_meet.repository.KeyValueDB
import com.ceslab.team7_ble_meet.signup.SignUpActivity

class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        var intent = Intent(this@SplashActivity, DashBoardActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)
        }
        Handler().postDelayed({
            startActivity(intent)
            checkAuth()
        }, 2200)
    }
    override fun onPause() {
        super.onPause()
        overridePendingTransition(0, 0)
    }

    fun checkAuth(){
        KeyValueDB.createRef(this)
        val selected = KeyValueDB.isFirstTimeRegister()
        if(!selected){
            //go to sign up
            var intent = Intent(this@SplashActivity,SignUpActivity::class.java ).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)
            }
            startActivity(intent)

        }else{
            //go to sign in

            //go to dash board
        }
        Log.d("TAG","Selected: $selected")
    }
}