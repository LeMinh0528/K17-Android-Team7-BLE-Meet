package com.ceslab.team7_ble_meet.repository

import androidx.annotation.WorkerThread
import com.ceslab.team7_ble_meet.Model.UserDiscovered
import com.ceslab.team7_ble_meet.db.UserDiscoveredDao

class UserDiscoveredRepository(private val userDiscoveredDao: UserDiscoveredDao) {
    //Room executes all queries on a separate thread.
    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun getAllWord(): List<UserDiscovered>{
        return userDiscoveredDao.getUserDiscover()
    }

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun insert(userDiscovered: UserDiscovered){
        userDiscoveredDao.insert(userDiscovered)
    }
}