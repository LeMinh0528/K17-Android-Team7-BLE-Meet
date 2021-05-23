package com.ceslab.team7_ble_meet.ble

import android.annotation.SuppressLint
import android.app.Activity
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.DataBindingUtil
import com.ceslab.team7_ble_meet.R
import com.ceslab.team7_ble_meet.databinding.ItemBleDataDiscoveredBinding

class ListDataDiscoveredAdapter(
    private val context: Activity,
    private val data: ArrayList<List<Int>>
) : ArrayAdapter(context, R.layout.item_ble_data_discovered) {
    private lateinit var binding: ItemBleDataDiscoveredBinding
    override fun getView(position: Int, view: View?, parent: ViewGroup): View {
        Log.d("MainActivity", "listView")
        val inflater = context.layoutInflater
        binding = DataBindingUtil.inflate(inflater, R.layout.item_ble_data_discovered, parent,false)
        binding.apply {
            tvID.text = "aaaaaaa"
        }
//        val rowView = inflater.inflate(R.layout.item_ble_data_discovered, null, true)

//        val tvID = rowView.findViewById(R.id.title) as TextView
//        val imageView = rowView.findViewById(R.id.icon) as ImageView
//        val subtitleText = rowView.findViewById(R.id.description) as TextView
//
//        titleText.text = title[position]
//        imageView.setImageResource(imgid[position])
//        subtitleText.text = description[position]

        return binding.root
    }
}
