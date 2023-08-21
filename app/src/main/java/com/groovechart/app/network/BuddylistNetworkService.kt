package com.groovechart.app.network

import com.groovechart.app.model.BuddylistActivity
import com.groovechart.app.model.BuddylistData
import com.groovechart.app.model.BuddylistItem
import retrofit2.http.GET
import retrofit2.http.Header

interface BuddylistNetworkService {

    @GET("/presence-view/v1/buddylist")
    suspend fun getFriendActivity(@Header("Authorization") authorization: String) : BuddylistActivity

}