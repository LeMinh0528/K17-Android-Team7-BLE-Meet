package com.ceslab.team7_ble_meet.registerInformation

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.LinearLayout
import com.ceslab.team7_ble_meet.R

class RegisterGenderActivity : AppCompatActivity() {
    lateinit var gender_man : LinearLayout
    lateinit var gender_woman : LinearLayout
    lateinit var inter_man : LinearLayout
    lateinit var inter_woman : LinearLayout
    lateinit var inter_both : LinearLayout
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register_gender)
        gender_man = findViewById(R.id.gender_man)
        gender_woman = findViewById(R.id.gender_woman)
        inter_man = findViewById(R.id.inter_man)
        inter_woman = findViewById(R.id.inter_woman)
        inter_both = findViewById(R.id.inter_both)
        setupView()
    }
    fun setupView(){
        var drawableNormal = getDrawable(R.drawable.bg_normal)
        var drawableSelected = getDrawable(R.drawable.bg_selected)
        gender_man.setOnClickListener {
            gender_man.setBackgroundDrawable(drawableSelected)
            gender_woman.setBackgroundDrawable(drawableNormal)
        }
        gender_woman.setOnClickListener {
            gender_woman.setBackgroundDrawable(drawableSelected)
            gender_man.setBackgroundDrawable(drawableNormal)
        }

        inter_man.setOnClickListener {
            inter_man.setBackgroundDrawable(drawableSelected)
            inter_woman.setBackgroundDrawable(drawableNormal)
            inter_both.setBackgroundDrawable(drawableNormal)

        }
        inter_woman.setOnClickListener {
            inter_woman.setBackgroundDrawable(drawableSelected)
            inter_man.setBackgroundDrawable(drawableNormal)
            inter_both.setBackgroundDrawable(drawableNormal)
        }
        inter_both.setOnClickListener {
            inter_both.setBackgroundDrawable(drawableSelected)
            inter_woman.setBackgroundDrawable(drawableNormal)
            inter_man.setBackgroundDrawable(drawableNormal)
        }

    }
}