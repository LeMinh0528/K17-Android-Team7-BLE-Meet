package com.ceslab.team7_ble_meet.Model

import java.util.*

data class TextMessage(val text: String,
                  override val time: Date,
                  override val senderId: String,
                  override val type: String = MessageType.TEXT)
    :Message {
        constructor(): this("", Date(0),"")

}