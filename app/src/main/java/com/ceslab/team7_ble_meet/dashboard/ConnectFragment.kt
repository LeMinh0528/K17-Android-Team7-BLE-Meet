package com.ceslab.team7_ble_meet.dashboard

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
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.getSystemService
import androidx.core.view.isGone
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.ceslab.team7_ble_meet.R
import com.ceslab.team7_ble_meet.ble.BleHandle
import com.ceslab.team7_ble_meet.bytesToHex
import com.ceslab.team7_ble_meet.databinding.FragmentConnectBinding
import com.ceslab.team7_ble_meet.toast
import kotlin.collections.ArrayList


class ConnectFragment : Fragment() {

    companion object {
        const val PERMISSIONS_REQUEST_CODE: Int = 12
    }
    private val TAG = "ConnectFragment"
    private lateinit var binding: FragmentConnectBinding

    // Initializes Bluetooth adapter.
    private lateinit var bluetoothManager : BluetoothManager
    private lateinit var bluetoothAdapter: BluetoothAdapter
    private lateinit var connect: BleHandle

    private var listDataReceived: ArrayList<String> = ArrayList()
    private lateinit var arrayAdapter: ArrayAdapter<*>

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

    private fun setUpBle(){
        val filter = IntentFilter()
        filter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED)
        requireContext().registerReceiver(broadcastReceiver, filter)

        bluetoothManager = activity?.let { getSystemService(it, BluetoothManager::class.java) }!!
        bluetoothAdapter = bluetoothManager.adapter
        connect = BleHandle()
    }

    private fun bleDataHandler(value: ByteArray){
        var exist = false
        for(i in listDataReceived){
            if(i == bytesToHex(value)){
                Log.d(TAG, "exist")
                exist = true
            }
        }
        if(!exist){
            listDataReceived.add(bytesToHex(value))
            arrayAdapter.notifyDataSetChanged()
        }
    }

    private fun setUpUI(inflater: LayoutInflater, container: ViewGroup?){
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_connect, container, false)
        binding.apply {
            if(bluetoothAdapter.isEnabled){
                swTurnOnOffBLE.isChecked = true
            }
            swTurnOnOffBLE.setOnCheckedChangeListener {_, isChecked ->
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
                    val data = byteArrayOf(1, 7, 2, 4, 5, 6, 7, 8, 9, 15, 11)
                    connect.advertise(data)
                    connect.discover()
                } else {
                    Toast.makeText(activity, "Please turn on Bluetooth", Toast.LENGTH_SHORT).show()
                }
                if(vRipple.isRippleAnimationRunning) vRipple.stopRippleAnimation() else vRipple.startRippleAnimation()
            }

            arrayAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, listDataReceived)
            binding.listViewBleDataReceived.adapter = arrayAdapter
            connect.bleDataReceived.observe(viewLifecycleOwner,{
                requireContext().toast("Receive data")
                if(!binding.btnFindFriend.isGone){
                    binding.btnFindFriend.isGone = true
                    binding.vRipple.isGone = true
                }
                bleDataHandler(it)
            })
        }
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

    override fun onDestroy() {
        super.onDestroy()
        requireContext().unregisterReceiver(broadcastReceiver)
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
}
