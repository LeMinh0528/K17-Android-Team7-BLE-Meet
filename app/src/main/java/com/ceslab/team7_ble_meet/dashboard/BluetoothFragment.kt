package com.ceslab.team7_ble_meet.dashboard

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.ceslab.team7_ble_meet.R
import android.widget.ImageView
import com.skyfishjy.library.RippleBackground
import kotlinx.android.synthetic.main.activity_scan.*

class BluetoothFragment: Fragment() {
    lateinit var p : RippleBackground
    lateinit var img : ImageView
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
savedInstanceState: Bundle?
): View? {
        var view = inflater.inflate(R.layout.activity_scan,container,false)
        p = view.findViewById(R.id.btnConnect)
        img = view.findViewById(R.id.centerImage)
        img.setOnClickListener{
            p.startRippleAnimation();
        }
        p.setOnClickListener{
            p.stopRippleAnimation();
        }
    return view
}
}