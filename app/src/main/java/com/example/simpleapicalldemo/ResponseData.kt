package com.example.simpleapicalldemo

/**
 * Data classes for Gson to use as models when converting JSON to java object
 */
data class ResponseData(
    val message: String,
    val user_id: Int,
    val name: String,
    val email: String,
    val mobile: Long,
    val profile_details: ProfileDetails,
    val data_list: List<DataListDetail>,
)

data class ProfileDetails(
    val is_profile_completed: Boolean,
    val rating: Double,
)

data class DataListDetail(
    val id: Int,
    val value: String
)