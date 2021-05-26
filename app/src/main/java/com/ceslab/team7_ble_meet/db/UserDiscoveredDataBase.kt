package com.ceslab.team7_ble_meet.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.ceslab.team7_ble_meet.Model.UserDiscovered

@Database(entities = [UserDiscovered::class], version = 1, exportSchema = false)
public abstract class UserDiscoveredDataBase : RoomDatabase() {

    abstract fun userDiscoveredDao(): UserDiscoveredDao

    companion object {
        @Volatile
        private var INSTANCE: UserDiscoveredDataBase? = null

        fun getDatabase(context: Context): UserDiscoveredDataBase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    UserDiscoveredDataBase::class.java, "users_discovered_database"
                )
                    .allowMainThreadQueries()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}