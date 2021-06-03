package com.ceslab.team7_ble_meet.Model

data class Person(val Name: String,
                  val avatar: String, val lastText: String)
 {
    constructor() : this("","","")
}