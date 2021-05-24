package com.ceslab.team7_ble_meet

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.ceslab.team7_ble_meet.Model.DataDiscovered
import com.ceslab.team7_ble_meet.dashboard.bleFragment.ListDataDiscoveredAdapter
import com.ceslab.team7_ble_meet.databinding.ActivityMainBinding
import kotlin.collections.ArrayList


class MainActivity : AppCompatActivity() {
    private val TAG = "MainActivity"

    private var listDataDisplay: ArrayList<DataDiscovered> = ArrayList()

    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        listDataDisplay.add(DataDiscovered(listOf(1720145,1,1,180,60,1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17)))
        listDataDisplay.add(DataDiscovered(listOf(1720147,1,1,180,60,1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17)))
        listDataDisplay.add(DataDiscovered(listOf(1720148,1,1,180,60,1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17)))
        listDataDisplay.add(DataDiscovered(listOf(1720149,1,1,180,60,1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17)))


        val listDataDiscoveredAdapter = ListDataDiscoveredAdapter()
        binding.rcListDataDiscovered.adapter = listDataDiscoveredAdapter
        listDataDiscoveredAdapter.data = listDataDisplay

        listDataDiscoveredAdapter.listener = object : ListDataDiscoveredAdapter.IdClickedListener{
            override fun onClickListen(id: String) {
                TODO("Not yet implemented")
            }
        }
        listDataDisplay.add(DataDiscovered(listOf(1720150,1,1,180,60,1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17)))
    }
}