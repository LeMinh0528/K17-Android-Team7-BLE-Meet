package com.ceslab.team7_ble_meet.repository

import androidx.annotation.WorkerThread
import com.ceslab.team7_ble_meet.Model.BleDataScanned
import com.ceslab.team7_ble_meet.db.BleDataScannedDao

class BleDataScannedRepository(private val bleDataScannedDao: BleDataScannedDao) {
    //Room executes all queries on a separate thread.
    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun getAllWord(): List<BleDataScanned>{
        return bleDataScannedDao.getUserDiscover()
    }

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun insert(bleDataScanned: BleDataScanned){
        bleDataScannedDao.insert(bleDataScanned)
    }
}