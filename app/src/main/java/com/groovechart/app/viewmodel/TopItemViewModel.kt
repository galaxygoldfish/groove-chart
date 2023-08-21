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

class TopItemViewModel : ViewModel() {

    private val networkProvider = NetworkProvider()

    var timeRangeSelection by mutableStateOf(0)

    val topSongList = mutableStateListOf<Song>()
    val topArtistList = mutableStateListOf<Artist>()

    suspend fun fetchTopItems(context: MainActivity, type: Int) {
        if (type == 0) {
            topSongList.apply {
                clear()
                addAll(
                    networkProvider.getTopTracks(
                        context = context,
                        timeRange = translateTimeRange(timeRangeSelection),
                        limit = 50,
                        offset = 0
                    ).items
                )
            }
        } else {
            topArtistList.apply {
                clear()
                addAll(
                    networkProvider.getTopArtists(
                        context = context,
                        timeRange = translateTimeRange(timeRangeSelection),
                        limit = 50,
                        offset = 0
                    ).items
                )
            }
        }
    }

    private fun translateTimeRange(value: Int): String {
        return when (value) {
            0 -> "short_term"
            1 -> "medium_term"
            else -> "long_term"
        }
    }


}