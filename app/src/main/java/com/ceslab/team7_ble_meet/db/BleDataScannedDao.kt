package com.ceslab.team7_ble_meet.db

import androidx.room.*
import com.ceslab.team7_ble_meet.Model.BleDataScanned

@Dao
interface BleDataScannedDao {
    @Query("SELECT * FROM users_discovered")
    fun getUserDiscover(): List<BleDataScanned>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insert(bleDataScanned: BleDataScanned)

    @Query("DELETE FROM users_discovered")
    fun deleteAll()

    @Delete
    suspend fun delete(bleDataScanned: BleDataScanned)

    @Update
    suspend fun update(bleDataScanned: BleDataScanned)
}