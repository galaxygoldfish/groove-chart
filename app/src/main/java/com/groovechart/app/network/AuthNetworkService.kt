package com.groovechart.app.network

import com.groovechart.app.model.AuthToken
import com.tencent.mmkv.MMKV
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query

interface AuthNetworkService {

    @GET("/get_access_token")
    suspend fun getAccessToken(
        @Query("reason") reason: String = "transport",
        @Query("productType") productType: String = "web_player",
        @Header("Cookie") cookie: String = "sp_dc=${MMKV.defaultMMKV().getString("FRIEND_ACTIVITY_COOKIE", null)}"
    ) : AuthToken

}