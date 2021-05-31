package com.ceslab.team7_ble_meet.model

data class UserResp(
    val page: Long? = null,
    val results: List<User>? = null,
    val totalPages: Long? = null,
    val totalResults: Long? = null
)