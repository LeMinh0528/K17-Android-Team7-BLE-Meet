package com.ceslab.team7_ble_meet

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.ceslab.team7_ble_meet.Model.BleDataScanned
import com.ceslab.team7_ble_meet.dashboard.bleFragment.ListBleDataScannedAdapter
import com.ceslab.team7_ble_meet.databinding.ActivityMainBinding
import com.ceslab.team7_ble_meet.db.BleDataScannedDataBase
import kotlin.collections.ArrayList


class MainActivity : AppCompatActivity() {
    private val TAG = "MainActivity"

    private var listDataAdapter = ListBleDataScannedAdapter()

    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        listDataAdapter.data = BleDataScannedDataBase.getDatabase(this).bleDataScannedDao().getUserDiscover() as ArrayList<BleDataScanned>
        binding.apply {
            rcList.adapter = listDataAdapter
            listDataAdapter.listener = object : ListBleDataScannedAdapter.IdClickedListener {
                override fun onClickListen(id: String) {
                    Log.d(TAG, id)
                }
            }
            btnAdd.setOnClickListener {
                val id = edtID.text.toString().trim()
                addUser(id.toInt())
            }
            btnDelete.setOnClickListener {
                deleteAllUser()
            }
        }
    }

    private fun addUser(id: Int) {
        BleDataScannedDataBase.getDatabase(this).bleDataScannedDao().insert(BleDataScanned(listOf(id,1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20,21)))
        listDataAdapter.data = BleDataScannedDataBase.getDatabase(applicationContext).bleDataScannedDao().getUserDiscover() as ArrayList<BleDataScanned>
    }

    private fun deleteAllUser(){
        BleDataScannedDataBase.getDatabase(this).bleDataScannedDao().deleteAll()
        listDataAdapter.data = BleDataScannedDataBase.getDatabase(applicationContext).bleDataScannedDao().getUserDiscover() as ArrayList<BleDataScanned>
    }
}