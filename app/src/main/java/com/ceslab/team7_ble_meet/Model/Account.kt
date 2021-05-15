package com.ceslab.team7_ble_meet.Model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Account(var usrName: String = "",
                   var email: String = "",
                   var password: String = "",
                   var phoneNumber: String = "",
                   var self_level_1: ArrayList<Int> = ArrayList(),
                   var self_level_2: ArrayList<Int> = ArrayList(),
                   var target_level_1: ArrayList<Int> = ArrayList(),
                   var target_level_2: ArrayList<Int> = ArrayList()): Parcelable