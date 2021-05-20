package com.ceslab.team7_ble_meet.registerInformation

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.ceslab.team7_ble_meet.R
import com.hbb20.CountryCodePicker

class RegisterPhoneActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register_phone)
        var ccp = findViewById(R.id.ccp) as CountryCodePicker

    }
}