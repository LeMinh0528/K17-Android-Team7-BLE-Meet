package com.ceslab.team7_ble_meet

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.ceslab.team7_ble_meet.Model.BleCharacter
import com.ceslab.team7_ble_meet.databinding.ActivityMainBinding
import java.util.*
import kotlin.experimental.or


class MainActivity : AppCompatActivity() {
    private val TAG = "MainActivity"
    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)

//        Log.d(TAG, "byte array: ${bytesToHex(setUpDataAdvertise())}")
    }
}