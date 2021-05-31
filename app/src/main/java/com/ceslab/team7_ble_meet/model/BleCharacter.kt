package com.ceslab.team7_ble_meet.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class BleCharacter(var data: Int = 0,
                        var size: Int = 0) : Parcelable