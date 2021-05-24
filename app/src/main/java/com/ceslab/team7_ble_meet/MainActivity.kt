package com.ceslab.team7_ble_meet

import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.ceslab.team7_ble_meet.ble.Characteristic
import com.ceslab.team7_ble_meet.dashboard.bleFragment.DataDiscoveredModel
import com.ceslab.team7_ble_meet.dashboard.bleFragment.ListDataDiscoveredAdapter
import com.ceslab.team7_ble_meet.databinding.ActivityMainBinding
import kotlin.collections.ArrayList


class MainActivity : AppCompatActivity() {
    private val TAG = "MainActivity"

    private var listDataDiscovered: ArrayList<List<Int>> = ArrayList()
    private var listDataDisplay: ArrayList<String> = ArrayList()
    private lateinit var arrayAdapter: ArrayAdapter<*>

    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        val list = listOf(DataDiscoveredModel(listOf(1720145,1,1,180,60,1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17)),
            DataDiscoveredModel(listOf(1720145,1,1,180,60,1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,16,17,18,19)),
            DataDiscoveredModel(listOf(1720145,1,1,180,60,1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,16,17,18,19)),
            DataDiscoveredModel(listOf(1720145,1,1,180,60,1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,16,17,18,19)))


        val listDataDiscoveredAdapter = ListDataDiscoveredAdapter()
        binding.rcListDataDiscovered.adapter = listDataDiscoveredAdapter
        listDataDiscoveredAdapter.data = list

        listDataDiscoveredAdapter.listener = object : ListDataDiscoveredAdapter.ListDataDiscoveredAdapterListener{
            override fun onClickListen(dataDiscovered: DataDiscoveredModel) {
                Toast.makeText(this@MainActivity,"${dataDiscovered.ID}", Toast.LENGTH_LONG).show()
            }
        }
    }
}