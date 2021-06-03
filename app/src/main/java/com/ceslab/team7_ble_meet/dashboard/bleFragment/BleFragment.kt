package com.ceslab.team7_ble_meet.dashboard.bleFragment

import android.Manifest
import android.bluetooth.BluetoothAdapter
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
import androidx.core.content.ContextCompat
import androidx.core.view.isGone
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.ceslab.team7_ble_meet.*
import com.ceslab.team7_ble_meet.chat.ChatActivity
import com.ceslab.team7_ble_meet.model.BleDataScanned
import com.ceslab.team7_ble_meet.databinding.FragmentBleBinding
import com.ceslab.team7_ble_meet.db.BleDataScannedDataBase

class BleFragment : Fragment() {

    companion object {
        const val PERMISSIONS_REQUEST_CODE: Int = 12
    }

    private val TAG = "Ble_Lifecycle"

    private lateinit var bleFragmentBinding: FragmentBleBinding
    private lateinit var bleFragmentViewModel: BleFragmentViewModel

    // Initializes Bluetooth adapter.
    private lateinit var bluetoothAdapter: BluetoothAdapter

    //adapter for recycler view
    private var listDataDiscoveredAdapter = ListBleDataScannedAdapter()

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
                        bleFragmentBinding.swTurnOnOffBLE.isChecked = false
                        bleFragmentBinding.vRipple.stopRippleAnimation()
                        Toast.makeText(
                            requireContext(),
                            "Broadcast Receiver: Bluetooth off",
                            Toast.LENGTH_LONG
                        ).show()
                        bleFragmentViewModel.stopFindFriend(requireContext())
                    }
                    BluetoothAdapter.STATE_ON -> {
                        bleFragmentBinding.swTurnOnOffBLE.isChecked = true
                        Toast.makeText(
                            requireContext(),
                            "Broadcast Receiver: Bluetooth on",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        Log.d(TAG,"BleFragment on Create View 1")
        checkPermissions()
        bleFragmentViewModel = ViewModelProvider(requireActivity()).get(BleFragmentViewModel::class.java)
        setUpBle()
        setUpUI(inflater, container)
        return bleFragmentBinding.root
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

    private fun setUpBle() {
        val filter = IntentFilter()
        filter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED)
        requireContext().registerReceiver(broadcastReceiver, filter)

        // state of bluetooth
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
    }

    private fun setUpUI(inflater: LayoutInflater, container: ViewGroup?) {
        bleFragmentBinding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_ble, container, false)
        bleFragmentBinding.apply {
            if (bluetoothAdapter.isEnabled) {
                swTurnOnOffBLE.isChecked = true
            }
            swTurnOnOffBLE.setOnCheckedChangeListener { _, isChecked ->
                bleFragmentViewModel.changeBluetoothStatus(isChecked)
            }

            btnFindFriend.setOnClickListener {
                bleFragmentViewModel.findFriend(requireContext())
            }

            bleFragmentViewModel.isRunning.observe(requireActivity(), {
                if (it) {
                    vRipple.startRippleAnimation()
                } else {
                    vRipple.stopRippleAnimation()
                }
            })

            btnStartFindFriend.setOnClickListener {
                bleFragmentViewModel.startFindFriend(requireContext())
            }

            btnStopFindFriend.setOnClickListener {
                bleFragmentViewModel.stopFindFriend(requireContext())
            }

            btnDelete.setOnClickListener {
                bleFragmentViewModel.deleteBleDataScanned(requireContext())
            }

            bleFragmentViewModel.setUpListDataScanned(requireContext(),requireActivity())
            rcListBleDataScanned.adapter = listDataDiscoveredAdapter
            bleFragmentViewModel.isBleDataScannedDisplay.observe(requireActivity(), {
                if (it) {
                    btnFindFriend.isGone = true
                    vRipple.isGone = true
                    rcListBleDataScanned.visibility = View.VISIBLE
                    listDataDiscoveredAdapter.data = BleDataScannedDataBase
                        .getDatabase(requireActivity())
                        .bleDataScannedDao()
                        .getUserDiscover() as ArrayList<BleDataScanned>
                } else {
                    Log.d(TAG, "hide the list")
                    btnFindFriend.isGone = false
                    vRipple.isGone = false
                    rcListBleDataScanned.visibility = View.GONE
                }
            })

            listDataDiscoveredAdapter.listener =
                object : ListBleDataScannedAdapter.IdClickedListener {
                    override fun onClickListen(id: String) {
                        Log.d(TAG, id)
//                    val intent = Intent(activity, ProfileActivity::class.java)
//                    intent.putExtra("idFromConnectFragmentToProfile", id)
//                    startActivity(intent)
                    }
                }
            listDataDiscoveredAdapter.nextlistener =
                object : ListBleDataScannedAdapter.onClickNextListender{
                    override fun onClick(id: String) {
                        val intent = Intent(activity, ChatActivity::class.java)
                        intent.putExtra(AppConstants.USER_ID, id)
                        startActivity(intent)
                    }
                }
        }
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