package com.ceslab.team7_ble_meet.setting

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import com.ceslab.team7_ble_meet.R
import com.ceslab.team7_ble_meet.dashboard.DashBoardActivity
import com.ceslab.team7_ble_meet.login.LogInActivity
import com.ceslab.team7_ble_meet.repository.KeyValueDB

class SettingActivity : AppCompatActivity() {
    lateinit var btnLogout: CardView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_setting)
        btnLogout.setOnClickListener{
            clearLocalData()
            goToLogIn()
        }

    }

    private fun clearLocalData(){
        KeyValueDB.setUserId("")
    }

    private fun goToLogIn(){
        val intent = Intent(this, LogInActivity::class.java ).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        startActivity(intent)
    }
}