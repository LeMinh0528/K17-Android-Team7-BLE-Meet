package com.ceslab.team7_ble_meet.registerInformation

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import androidx.cardview.widget.CardView
import com.ceslab.team7_ble_meet.R

class RegisterOjectFindActivity : AppCompatActivity() {
    lateinit var CheckLongRelationship : ImageView
    lateinit var CheckFriends :ImageView
    lateinit var CheckFun : ImageView
    lateinit var btn_LongTermRelationship : CardView
    lateinit var  btn_Friend : CardView
    lateinit var  btn_Fun : CardView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register_oject_find)
        CheckLongRelationship = findViewById(R.id.check_LongTermRelationship) as ImageView
        CheckFriends = findViewById(R.id.check_FriendRelationship) as ImageView
        CheckFun = findViewById(R.id.check_FunRelationship) as ImageView
        btn_LongTermRelationship = findViewById(R.id.btn_LongTermRelationship) as CardView
        btn_Friend = findViewById(R.id.btn_FriendRelationship) as CardView
        btn_Fun = findViewById(R.id.btn_FunRelationship) as CardView
        setupView()


    }

    fun setupView() {

//        var drawableUncheck = R.drawable.icon_uncheck
//        var drawableCheck = R.drawable.icon_check
//        var drawableNormal = getDrawable(R.drawable.bg_normal)
//        var drawableSelected = getDrawable(R.drawable.bg_selected)
        btn_LongTermRelationship.setOnClickListener {
            CheckLongRelationship.setImageResource(R.drawable.ic_baseline_check_circle_outline_24)
            CheckFriends.setImageResource(R.drawable.ic_baseline_radio_button_unchecked_24)
            CheckFun.setImageResource(R.drawable.ic_baseline_radio_button_unchecked_24)
        }
        btn_Friend.setOnClickListener {
            CheckLongRelationship.setImageResource(R.drawable.ic_baseline_radio_button_unchecked_24)
            CheckFriends.setImageResource(R.drawable.ic_baseline_check_circle_outline_24)
            CheckFun.setImageResource(R.drawable.ic_baseline_radio_button_unchecked_24)
        }
        btn_Fun.setOnClickListener {
            CheckLongRelationship.setImageResource(R.drawable.ic_baseline_radio_button_unchecked_24)
            CheckFriends.setImageResource(R.drawable.ic_baseline_radio_button_unchecked_24)
            CheckFun.setImageResource(R.drawable.ic_baseline_check_circle_outline_24)
        }
    }
}