package com.ceslab.team7_ble_meet.Model

data class ChatChannel(val userIds: MutableList<String>) {
    constructor() : this(mutableListOf())
}