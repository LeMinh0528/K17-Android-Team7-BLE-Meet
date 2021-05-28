package com.ceslab.team7_ble_meet.db

import android.content.Context
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.ceslab.team7_ble_meet.Model.BleDataScanned

@Database(entities = [BleDataScanned::class], version = 1, exportSchema = false)
public abstract class BleDataScannedDataBase : RoomDatabase() {

    val TAG = "Ble_service"

    abstract fun bleDataScannedDao(): BleDataScannedDao
    var addData: MutableLiveData<Boolean> = MutableLiveData()

    companion object {
        @Volatile
        private var INSTANCE: BleDataScannedDataBase? = null

        fun getDatabase(context: Context): BleDataScannedDataBase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    BleDataScannedDataBase::class.java, "ble_data_scanned_database"
                )
                    .allowMainThreadQueries()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
    fun setAddData(){
        addData.value = true
        Log.d(TAG, "add Data value: ${addData.value}")
    }
    fun setNonAddData(){
        addData.value = false
        Log.d(TAG, "add Data value: ${addData.value}")
    }
}