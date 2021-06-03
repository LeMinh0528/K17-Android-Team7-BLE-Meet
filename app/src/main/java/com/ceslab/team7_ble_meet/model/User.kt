package com.ceslab.team7_ble_meet.model

data class User(val Name: String,
                val bio: String,
                val dob: String,
                val token: MutableList<String>,
                val gender: String,
                val avatar: String?) {
    constructor(): this("", "","", mutableListOf(), "","")
}