package com.groovechart.app.model

import com.google.gson.annotations.SerializedName

data class AuthResponse(
    @SerializedName("access_token")
    val accessToken: String,
    @SerializedName("expires_in")
    val expires: Long,
    @SerializedName("refresh_token")
    val refreshToken: String
)
