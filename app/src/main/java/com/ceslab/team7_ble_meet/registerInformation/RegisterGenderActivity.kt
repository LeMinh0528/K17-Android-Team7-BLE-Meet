package com.ceslab.team7_ble_meet.registerInformation

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.LinearLayout
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.ceslab.team7_ble_meet.Model.UserResp
import com.ceslab.team7_ble_meet.R
import com.ceslab.team7_ble_meet.UsersFireStoreHandler
import com.ceslab.team7_ble_meet.repository.KeyValueDB

class RegisterGenderActivity : AppCompatActivity() {
    lateinit var gender_man : LinearLayout
    lateinit var gender_woman : LinearLayout
    lateinit var inter_man : LinearLayout
    lateinit var inter_woman : LinearLayout
    lateinit var inter_both : LinearLayout
    lateinit var btnContinue: LinearLayout
    private var userId: String = KeyValueDB.getUserId()
    private var userGender: String = KeyValueDB.getUserGender()
    private var userInterested: String = KeyValueDB.getUserInterested()
//    private var chooseGender : String = ""
//    private var chooseInterested : String = ""
    lateinit var viewModel: RegisterGenderViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register_gender)
        Log.d("RegisterGenderActivity","id: $userId")
        Log.d("RegisterGenderActivity","gender: $userGender")
        Log.d("RegisterGenderActivity","inter: $userInterested")

        gender_man = findViewById(R.id.gender_man)
        gender_woman = findViewById(R.id.gender_woman)
        inter_man = findViewById(R.id.inter_man)
        inter_woman = findViewById(R.id.inter_woman)
        inter_both = findViewById(R.id.inter_both)
        btnContinue = findViewById(R.id.btn_continue)

        setupViewModel()
        setupView()
        init()
    }

    private fun setupViewModel(){
        viewModel = ViewModelProvider(this).get(RegisterGenderViewModel::class.java)
    }

    private fun setupView(){
        var drawableNormal = getDrawable(R.drawable.bg_normal)
        var drawableSelected = getDrawable(R.drawable.bg_selected)
        gender_man.setOnClickListener {
            gender_man.setBackgroundDrawable(drawableSelected)
            gender_woman.setBackgroundDrawable(drawableNormal)
            userGender = "Male"
            updateButton()
        }
        gender_woman.setOnClickListener {
            gender_woman.setBackgroundDrawable(drawableSelected)
            gender_man.setBackgroundDrawable(drawableNormal)
            userGender = "Female"
            updateButton()
        }

        inter_man.setOnClickListener {
            inter_man.setBackgroundDrawable(drawableSelected)
            inter_woman.setBackgroundDrawable(drawableNormal)
            inter_both.setBackgroundDrawable(drawableNormal)
            userInterested = "Male"
            updateButton()

        }
        inter_woman.setOnClickListener {
            inter_woman.setBackgroundDrawable(drawableSelected)
            inter_man.setBackgroundDrawable(drawableNormal)
            inter_both.setBackgroundDrawable(drawableNormal)
            userInterested = "Female"
            updateButton()
        }
        inter_both.setOnClickListener {
            inter_both.setBackgroundDrawable(drawableSelected)
            inter_woman.setBackgroundDrawable(drawableNormal)
            inter_man.setBackgroundDrawable(drawableNormal)
            userInterested = "Both"
            updateButton()
        }

        btnContinue.setOnClickListener {
            viewModel.register(userId,userGender,userInterested)
        }

        viewModel.userResp.observe(this, Observer { result ->
            Log.d("RegisterGenderActivity","RegisterGenderActivity observer")
            if(result != null){
                if(result.type == "SUCCESS"){
                    goToTagRegister()
                }else if(result.type == "FAILED"){
                }
            }

        })
    }

    private fun updateButton(){
        btnContinue.isEnabled = !(userGender == "" || userInterested == "")
    }

    private fun init(){
        var drawableNormal = getDrawable(R.drawable.bg_normal)
        var drawableSelected = getDrawable(R.drawable.bg_selected)
        if(userGender!= ""){
            if (userGender == "Male"){
                gender_man.setBackgroundDrawable(drawableSelected)
                gender_woman.setBackgroundDrawable(drawableNormal)
            }
            if(userGender == "Female"){
                gender_woman.setBackgroundDrawable(drawableSelected)
                gender_man.setBackgroundDrawable(drawableNormal)
            }
        }
        if(userInterested != ""){
            if(userInterested == "Male"){
                inter_man.setBackgroundDrawable(drawableSelected)
                inter_woman.setBackgroundDrawable(drawableNormal)
                inter_both.setBackgroundDrawable(drawableNormal)
            }
            if(userInterested == "Female"){
                inter_woman.setBackgroundDrawable(drawableSelected)
                inter_man.setBackgroundDrawable(drawableNormal)
                inter_both.setBackgroundDrawable(drawableNormal)
            }
            if(userInterested == "Both"){
                inter_woman.setBackgroundDrawable(drawableNormal)
                inter_man.setBackgroundDrawable(drawableNormal)
                inter_both.setBackgroundDrawable(drawableSelected)
            }
        }
        updateButton()
    }

    private fun goToTagRegister(){
        val intent = Intent(this, RegisterTagActivity::class.java).apply {
        }
        startActivity(intent)
    }
}