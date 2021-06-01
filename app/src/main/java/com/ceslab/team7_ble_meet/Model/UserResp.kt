package com.ceslab.team7_ble_meet.Model

data class UserResp(
    val page: Long? = null,
    val results: List<UserAPI>? = null,
    val totalPages: Long? = null,
    val totalResults: Long? = null
)