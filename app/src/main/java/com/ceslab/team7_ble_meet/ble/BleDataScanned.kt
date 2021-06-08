package com.ceslab.team7_ble_meet.ble

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.ceslab.team7_ble_meet.ble.Characteristic

@Entity(tableName = "users_discovered")
class BleDataScanned(
    @PrimaryKey val ID: Int,
    @ColumnInfo(name = "data_discovered") val description: String
) {
    constructor(listChar: List<Int>) : this(
        listChar[0],
        "Age: ${listChar[1]}\n" +
                "Sex: ${Characteristic.sex[listChar[2]]}\n" +
                "Sexuality Orientation: ${Characteristic.sexualOrientation[listChar[3]]}\n" +
                "TAG: " + Characteristic.Tag[listChar[4]] + " - " +
                Characteristic.Tag[listChar[5]] + " - " +
                Characteristic.Tag[listChar[6]] + " - " +
                Characteristic.Tag[listChar[7]] + " - " +
                Characteristic.Tag[listChar[8]]
    )
}