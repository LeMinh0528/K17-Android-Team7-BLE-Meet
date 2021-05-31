package com.ceslab.team7_ble_meet.model

import com.google.gson.annotations.SerializedName

data class User(
    @SerializedName("profile_path")
    val profilePath: String? = null,
    val adult: Boolean? = null,
    val id: Long? = null,
    val name: String? = null,
    val popularity: Double? = null

){
    fun getImagePath(): String{
        return "https://image.tmdb.org/t/p/w300$profilePath"
    }
}