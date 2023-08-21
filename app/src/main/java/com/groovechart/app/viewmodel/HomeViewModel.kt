package com.groovechart.app.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.groovechart.app.MainActivity
import com.groovechart.app.model.Artist
import com.groovechart.app.model.Song
import com.groovechart.app.network.NetworkProvider
import com.tencent.mmkv.MMKV
import kotlin.math.exp

class HomeViewModel : ViewModel() {

    private val networkProvider = NetworkProvider()

    var userName by mutableStateOf("")
    var userProfileImage by mutableStateOf("")

    var topSongList = mutableStateListOf<Song>()
    val topArtistList = mutableStateListOf<Artist>()
    val topGenreList = mutableStateListOf<String>()

    var currentNavDestination by mutableStateOf(0)

    suspend fun initialize(context: MainActivity) {
        val currentTime = System.currentTimeMillis()
        val expirationTime = MMKV.defaultMMKV().getLong("AUTH_EXPIRY", 0L)
        if (currentTime >= expirationTime) {
            networkProvider.verifyToken(context)
        } else {
            fetchUserDetail(context)
            fetchTopItems(context)
        }
    }

    suspend fun fetchUserDetail(context: MainActivity) {
        networkProvider.getUserDetails(context).let {
            userName = it.display_name
            userProfileImage = it.images[0].url
            MMKV.defaultMMKV().putString("USER_ID", it.id)
        }
    }

    suspend fun fetchTopItems(context: MainActivity) {
        topSongList.apply {
            clear()
            addAll(
                networkProvider.getTopTracks(
                    context,
                    timeRange = "medium_term",
                    limit = 3,
                    offset = 0
                ).items
            )
        }
        topArtistList.apply {
            clear()
            addAll(
                networkProvider.getTopArtists(
                    context,
                    timeRange = "short_term",
                    limit = 3,
                    offset = 0
                ).items
            )
        }
        calculateTopGenres(context)
    }

    private suspend fun calculateTopGenres(context: MainActivity) {
        val genreSampleArtistList = mutableListOf<Artist>()
        genreSampleArtistList.addAll(
            networkProvider.getTopArtists(
                context = context,
                timeRange = "short_term",
                limit = 25,
                offset = 0
            ).items
        )
        val totalGenreList = mutableListOf<String>()
        genreSampleArtistList.forEach { artist ->
            totalGenreList.addAll(artist.genres!!)
        }
        topGenreList.apply {
            clear()
            findTopSixFrequentItems(totalGenreList).forEach { item ->
                add(item.first)
            }
        }
    }

    private fun findTopSixFrequentItems(list: List<String>): List<Pair<String, Int>> {
        val frequencyMap = mutableMapOf<String, Int>()
        for (item in list) {
            frequencyMap[item] = frequencyMap.getOrDefault(item, 0) + 1
        }
        val sortedList = frequencyMap.toList().sortedByDescending { (_, frequency) -> frequency }
        return sortedList.take(6)
    }

}