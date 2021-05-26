package com.ceslab.team7_ble_meet.dashboard

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import com.ceslab.team7_ble_meet.R
import com.ceslab.team7_ble_meet.setting.SettingActivity

class InformationFragment: Fragment() {
    lateinit var btnSetting : Button

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        var view = inflater.inflate(R.layout.fragment_information,container,false)
        btnSetting = view.findViewById(R.id.btn_setting)
        btnSetting.setOnClickListener{
            var intent = Intent(activity,SettingActivity::class.java)
            startActivity(intent)
        }
        return view
    }
}