package com.groovechart.app.viewmodel

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import com.groovechart.app.model.BuddylistItem
import com.groovechart.app.network.NetworkProvider
import com.tencent.mmkv.MMKV

class FriendViewModel : ViewModel() {

    val networkProvider = NetworkProvider()

    val friendActivityList = mutableStateListOf<BuddylistItem>()

    suspend fun getFriendActivity() {
        friendActivityList.apply {
            clear()
            addAll(
                networkProvider.getFriendActivity().friends.sortedBy {
                    item -> item.timestamp
                }.reversed()
            )
        }
    }

    fun checkIfNewAuthNeeded() : Boolean {
        val mmkv = MMKV.defaultMMKV()
        return (
            mmkv.decodeString("FRIEND_ACTIVITY_COOKIE", "")!!.isBlank() ||
            mmkv.decodeLong("FA_AUTH_EXPIRY", 0L) < System.currentTimeMillis()
        )
    }

}