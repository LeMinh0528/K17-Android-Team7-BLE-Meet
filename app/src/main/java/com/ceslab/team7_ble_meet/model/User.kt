package com.ceslab.team7_ble_meet.model

import com.google.firebase.firestore.PropertyName

data class User(
    @get: PropertyName("Name") @set: PropertyName("Name") var Name: String,
    val bio: String,
    @get: PropertyName("DayOfBirth") @set: PropertyName("DayOfBirth") var dob: String,
    val token: MutableList<String>,
    @get: PropertyName("Gender") @set: PropertyName("Gender") var gender: String,
    @get: PropertyName("Tag") @set: PropertyName("Tag") var tag: MutableList<String>,
    val avatar: String?
) {
    constructor() : this("", "", "", mutableListOf(), "", mutableListOf(), "")
}