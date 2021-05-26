package com.ceslab.team7_ble_meet.db

import androidx.room.*
import com.ceslab.team7_ble_meet.Model.UserDiscovered

@Dao
interface UserDiscoveredDao {
    @Query("SELECT * FROM users_discovered")
    fun getUserDiscover(): List<UserDiscovered>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insert(user: UserDiscovered)

    @Query("DELETE FROM users_discovered")
    fun deleteAll()

    @Delete
    suspend fun delete(user: UserDiscovered)

    @Update
    suspend fun update(user: UserDiscovered)
}