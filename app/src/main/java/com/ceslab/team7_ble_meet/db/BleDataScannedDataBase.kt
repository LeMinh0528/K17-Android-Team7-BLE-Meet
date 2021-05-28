package com.ceslab.team7_ble_meet.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.ceslab.team7_ble_meet.Model.BleDataScanned

@Database(entities = [BleDataScanned::class], version = 1, exportSchema = false)
public abstract class BleDataScannedDataBase : RoomDatabase() {

    abstract fun bleDataScannedDao(): BleDataScannedDao

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
}