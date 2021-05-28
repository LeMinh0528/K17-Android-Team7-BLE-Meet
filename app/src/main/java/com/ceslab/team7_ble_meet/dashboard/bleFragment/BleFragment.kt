package com.ceslab.team7_ble_meet.dashboard.bleFragment

import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.app.ServiceCompat
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.getSystemService
import androidx.core.content.ContextCompat.startForegroundService
import androidx.core.view.isGone
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.ceslab.team7_ble_meet.*
import com.ceslab.team7_ble_meet.Model.BleDataScanned
import com.ceslab.team7_ble_meet.ble.BleHandle
import com.ceslab.team7_ble_meet.databinding.FragmentBleBinding
import com.ceslab.team7_ble_meet.db.BleDataScannedDataBase
import com.ceslab.team7_ble_meet.service.BleService
import kotlin.experimental.or

class BleFragment : Fragment() {

    companion object {
        const val PERMISSIONS_REQUEST_CODE: Int = 12
    }

    private val TAG = "BleFragment"

    private lateinit var binding: FragmentBleBinding

    // Initializes Bluetooth adapter.
    private lateinit var bluetoothManager : BluetoothManager
    private lateinit var bluetoothAdapter: BluetoothAdapter
    private lateinit var bleHandle: BleHandle

    private var listDataDiscoveredAdapter = ListBleDataScanedAdapter()

    private val characteristicUser = listOf(1720149, 25, 1, 1, 165, 60, 1, 2, 3, 4, 5, 6, 7, 8, 10, 11, 12, 13, 14, 15, 16, 17)
    private val target = listOf(18, 30, 1, 2, 3, 1, 2, 3, 160, 180, 50, 80, 1, 2, 3, 4, 5, 6, 7, 8, 9,10, 11, 12, 13, 14, 15, 16, 17, 18)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        checkPermissions()
        setUpBle()
        setUpUI(inflater, container)

        return binding.root
    }

    private fun checkPermissions() {
        val reqPermissions = ArrayList<String>()
        if (activity?.let {
                ContextCompat.checkSelfPermission(it, Manifest.permission.ACCESS_FINE_LOCATION)
            } != PackageManager.PERMISSION_GRANTED) {
            reqPermissions.add(Manifest.permission.ACCESS_FINE_LOCATION)
        }

        if (Build.VERSION.SDK_INT >= 23 && activity?.let {
                ContextCompat.checkSelfPermission(it, Manifest.permission.ACCESS_COARSE_LOCATION)
            } != PackageManager.PERMISSION_GRANTED
        ) {
            reqPermissions.add(Manifest.permission.ACCESS_COARSE_LOCATION)
        }
        if (reqPermissions.isNotEmpty()) {
            activity?.let {
                ActivityCompat.requestPermissions(
                    it, reqPermissions.toTypedArray(), PERMISSIONS_REQUEST_CODE
                )
            }
        }
    }

    private fun setUpBle(){
        val filter = IntentFilter()
        filter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED)
        requireContext().registerReceiver(broadcastReceiver, filter)

        // state of bluetooth
        bluetoothManager = activity?.let { getSystemService(it, BluetoothManager::class.java) }!!
        bluetoothAdapter = bluetoothManager.adapter

        bleHandle = BleHandle()
    }

    private val broadcastReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val action = intent?.action
            if (action == BluetoothAdapter.ACTION_STATE_CHANGED) {
                val state = intent.getIntExtra(
                    BluetoothAdapter.EXTRA_STATE,
                    BluetoothAdapter.ERROR
                )
                when (state) {
                    BluetoothAdapter.STATE_OFF -> {
                        binding.swTurnOnOffBLE.isChecked = false
                        binding.vRipple.stopRippleAnimation()
                        Toast.makeText(requireContext(), "Bluetooth off", Toast.LENGTH_LONG).show()
                    }
                    BluetoothAdapter.STATE_ON -> {
                        binding.swTurnOnOffBLE.isChecked = true
                        Toast.makeText(requireContext(), "Bluetooth on", Toast.LENGTH_LONG).show()
                    }
                }
            }
        }
    }

    private fun setUpUI(inflater: LayoutInflater, container: ViewGroup?){
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_ble, container, false)
        binding.apply {
            if(bluetoothAdapter.isEnabled){
                swTurnOnOffBLE.isChecked = true
            }
            swTurnOnOffBLE.setOnCheckedChangeListener { _, isChecked ->
                if (isChecked){
                    bluetoothAdapter.enable()
                    Log.d(TAG, "BLE is enable")
                } else{
                    bluetoothAdapter.disable()
                    Log.d(TAG, "BLE is disable")
                }
            }
            btnFindFriend.setOnClickListener {
                if (bluetoothAdapter.isEnabled) {
                    if(!vRipple.isRippleAnimationRunning){
                        startBleService()
                        vRipple.startRippleAnimation()
                    }else{
                        stopBleService()
                        vRipple.stopRippleAnimation()
                    }
                } else {
                    Toast.makeText(activity, "Please turn on Bluetooth", Toast.LENGTH_SHORT).show()
                }
            }
            btnStopFindFriend.setOnClickListener {
                vRipple.stopRippleAnimation()
                if (bluetoothAdapter.isEnabled) {
                    stopBleService()
                }
            }
            btnDelete.setOnClickListener{
                deleteAllUser()
                btnFindFriend.isGone = false
                vRipple.isGone = false
                rcListDataDiscovered.isGone = true
            }

            listDataDiscoveredAdapter.data = BleDataScannedDataBase.getDatabase(requireContext()).bleDataScannedDao().getUserDiscover() as ArrayList<BleDataScanned>
            rcListDataDiscovered.adapter = listDataDiscoveredAdapter
            if(BleDataScannedDataBase.getDatabase(requireContext())
                    .bleDataScannedDao()
                    .getUserDiscover()
                    .isNotEmpty()
            ){
                btnFindFriend.isGone = true
                vRipple.isGone = true
                rcListDataDiscovered.visibility = View.VISIBLE
            }

            BleDataScannedDataBase.getDatabase(requireContext()).addData.observe(requireActivity(),{
                if(it){
                    Log.d(TAG,"add data changed")
                    btnFindFriend.isGone = true
                    vRipple.isGone = true
                    rcListDataDiscovered.visibility = View.VISIBLE
                }
            })

            listDataDiscoveredAdapter.listener = object : ListBleDataScanedAdapter.IdClickedListener{
                override fun onClickListen(id: String) {
                    Log.d(TAG,id)
//                    val intent = Intent(activity, ProfileActivity::class.java)
//                    intent.putExtra("idFromConnectFragmentToProfile", id)
//                    startActivity(intent)
                }
            }
        }
    }

    private fun deleteAllUser() {
        BleDataScannedDataBase.getDatabase(requireContext()).bleDataScannedDao().deleteAll()
        BleDataScannedDataBase.getDatabase(requireContext()).setAddData()
    }

    private fun startBleService(){
        val intent = Intent(activity,BleService::class.java)
        activity?.startService(intent)
    }
    private fun stopBleService(){
        val intent = Intent(activity,BleService::class.java)
        activity?.stopService(intent)
    }
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            PERMISSIONS_REQUEST_CODE -> {
                if ((grantResults.isNotEmpty())) {
                    grantResults.forEach { result ->
                        if (result != PackageManager.PERMISSION_GRANTED) {
//                            finish()
                            return
                        }
                    }
                }
            }
        }
    }


    override fun onDestroy() {
        super.onDestroy()
        requireContext().unregisterReceiver(broadcastReceiver)
    }
}