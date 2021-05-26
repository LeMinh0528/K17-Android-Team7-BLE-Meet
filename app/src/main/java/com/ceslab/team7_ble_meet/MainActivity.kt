package com.ceslab.team7_ble_meet

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.ceslab.team7_ble_meet.Model.UserDiscovered
import com.ceslab.team7_ble_meet.dashboard.bleFragment.ListDataDiscoveredAdapter
import com.ceslab.team7_ble_meet.databinding.ActivityMainBinding
import com.ceslab.team7_ble_meet.db.UserDiscoveredDataBase
import kotlin.collections.ArrayList


class MainActivity : AppCompatActivity() {
    private val TAG = "MainActivity"

    private var listDataAdapter = ListDataDiscoveredAdapter()

    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        listDataAdapter.data = UserDiscoveredDataBase.getDatabase(this).userDiscoveredDao().getUserDiscover() as ArrayList<UserDiscovered>
        binding.apply {
            rcList.adapter = listDataAdapter
            listDataAdapter.listener = object : ListDataDiscoveredAdapter.IdClickedListener {
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
        UserDiscoveredDataBase.getDatabase(this).userDiscoveredDao().insert(UserDiscovered(listOf(id,1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20,21)))
        listDataAdapter.data = UserDiscoveredDataBase.getDatabase(applicationContext).userDiscoveredDao().getUserDiscover() as ArrayList<UserDiscovered>
    }

    private fun deleteAllUser(){
        UserDiscoveredDataBase.getDatabase(this).userDiscoveredDao().deleteAll()
        listDataAdapter.data = UserDiscoveredDataBase.getDatabase(applicationContext).userDiscoveredDao().getUserDiscover() as ArrayList<UserDiscovered>
    }
}