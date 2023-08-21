package com.groovechart.app.viewmodel

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.text.input.TextFieldValue
import androidx.lifecycle.ViewModel
import com.groovechart.app.MainActivity
import com.groovechart.app.model.PlaylistAddBody
import com.groovechart.app.model.PlaylistBody
import com.groovechart.app.network.NetworkProvider

class MusicViewModel : ViewModel() {

    var playlistTitle by mutableStateOf(TextFieldValue())
    var playlistLength by mutableStateOf(0.2F)
    var playlistPublic by mutableStateOf(true)
    var playlistType by mutableStateOf("")
    var selectedGenres by mutableStateOf("")

    var danceability = mutableStateOf(0.2F)
    var energy = mutableStateOf(0.2F)
    var liveness = mutableStateOf(0.2F)
    var instrumentalness = mutableStateOf(0.2F)
    var popularity = mutableStateOf(0.2F)
    var speechiness = mutableStateOf(0.2F)

    private val networkProvider = NetworkProvider()

    suspend fun generateMonthlyPlaylist(context: MainActivity): String {
        val top5TracksMonth = networkProvider.getTopTracks(
            context = context,
            limit = 5,
            offset = 0,
            timeRange = "short_term"
        ).items.map { it.id }
        val recommendationTrackURIs = networkProvider.getRecommendations(
            context = context,
            limit = (playlistLength * 100F).toInt(),
            trackSeeds = top5TracksMonth.joinToString(",")
        ).tracks.map { it.uri }
        return createPlaylistWithTracks(
            trackURIs = recommendationTrackURIs,
            context = context
        )
    }

    suspend fun generateYearPlaylist(context: MainActivity): String {
        val topTracksYear = networkProvider.getTopTracks(
            context = context,
            limit = ((playlistLength * 100F).toInt() / 2),
            offset = 0,
            timeRange = "medium_term"
        )
        val mixtapeTracks = networkProvider.getRecommendations(
            context = context,
            limit = ((playlistLength * 100F).toInt() / 2),
            trackSeeds = topTracksYear.items.map { it.id }
                .shuffled()
                .take(5)
                .joinToString(",")
        )
        val totalTrackList = mutableListOf<String>().apply {
            addAll(topTracksYear.items.map { it.uri })
            addAll(mixtapeTracks.tracks.map { it.uri })
        }
        return createPlaylistWithTracks(
            trackURIs = totalTrackList.shuffled(),
            context = context
        )
    }

    suspend fun generateGenrePlaylist(context: MainActivity): String {
        val recommendationTracks = networkProvider.getRecommendations(
            context = context,
            limit = (playlistLength * 100F).toInt(),
            genreSeeds = selectedGenres
        )
        return createPlaylistWithTracks(
            trackURIs = recommendationTracks.tracks.map { it.uri },
            context = context
        )
    }

    suspend fun generateCustomPlaylist(context: MainActivity): String {
        val recommendationTracks = networkProvider.getRecommendations(
            context = context,
            limit = (playlistLength * 100F).toInt(),
            genreSeeds = selectedGenres,
            danceability = danceability.value,
            energy = energy.value,
            instrumentalness = instrumentalness.value,
            speechiness = speechiness.value,
            popularity = (popularity.value * 100F).toInt(),
            liveness = liveness.value
        )
        return createPlaylistWithTracks(
            trackURIs = recommendationTracks.tracks.map { it.uri },
            context = context
        )
    }

    private suspend fun createPlaylistWithTracks(
        trackURIs: List<String>,
        context: MainActivity
    ): String {
        val createdPlaylist = networkProvider.createPlaylist(
            context = context,
            playlistBody = PlaylistBody(
                name = playlistTitle.text.ifBlank { "Playlist" },
                public = playlistPublic
            )
        )
        networkProvider.addTracksToPlaylist(
            context = context,
            playlistID = createdPlaylist.id,
            playlistAddBody = PlaylistAddBody(
                uris = trackURIs
            )
        )
        return createdPlaylist.uri
    }

}