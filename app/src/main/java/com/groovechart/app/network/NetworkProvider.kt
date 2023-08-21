package com.groovechart.app.network

import com.groovechart.app.MainActivity
import com.groovechart.app.model.Artist
import com.groovechart.app.model.BuddylistActivity
import com.groovechart.app.model.GenreSeeds
import com.groovechart.app.model.PlaylistAddBody
import com.groovechart.app.model.PlaylistBody
import com.groovechart.app.model.PlaylistResponse
import com.groovechart.app.model.RecommendationResponse
import com.groovechart.app.model.Song
import com.groovechart.app.model.TopItems
import com.groovechart.app.model.User
import com.groovechart.app.network.NetworkServiceBuilder.BASE_URL_AUTH
import com.groovechart.app.network.NetworkServiceBuilder.BASE_URL_BUDDYLIST
import com.groovechart.app.network.NetworkServiceBuilder.BASE_URL_SPOTIFY
import com.groovechart.app.network.NetworkServiceBuilder.apiService
import com.spotify.sdk.android.auth.AuthorizationClient
import com.spotify.sdk.android.auth.AuthorizationRequest
import com.spotify.sdk.android.auth.AuthorizationResponse
import com.tencent.mmkv.MMKV

class NetworkProvider() {

    private val mmkv = MMKV.defaultMMKV()

    private val spotifyNetworkService: SpotifyNetworkService by lazy {
        apiService(BASE_URL_SPOTIFY)
    }

    private val buddylistNetworkService: BuddylistNetworkService by lazy {
        apiService(BASE_URL_BUDDYLIST)
    }

    private val authNetworkService: AuthNetworkService by lazy {
        apiService(BASE_URL_AUTH)
    }

    fun promptUserLogin(context: MainActivity) {
        AuthorizationClient.openLoginActivity(
            context,
            MainActivity.TOKEN_REQUEST_CODE,
            AuthorizationRequest.Builder(
                Credentials.CLIENT_ID,
                AuthorizationResponse.Type.TOKEN,
                "groovechart://login"
            ).setScopes(
                arrayOf(
                    "user-follow-read",
                    "user-top-read",
                    "user-read-private",
                    "user-read-email",
                    "playlist-modify-public",
                    "playlist-modify-private"
                )
            )
                .setShowDialog(false)
                .build()
        )
    }

    private fun accessToken(context: MainActivity): String {
        verifyToken(context)
        return "Bearer ${mmkv.getString("AUTH_TOKEN", "")}"
    }

    fun verifyToken(context: MainActivity) {
        val currentTime = System.currentTimeMillis()
        val expiryTime = mmkv.getLong("AUTH_EXPIRY", System.currentTimeMillis())
        if (mmkv.decodeString("AUTH_TOKEN", "")!!.isBlank()) {
            return promptUserLogin(context)
        } else if (currentTime >= expiryTime) {
            return promptUserLogin(context)
        }
    }

    private suspend fun getFAAccessToken() : String {
        val FAExpiry = mmkv.getLong("FA_AUTH_EXPIRY", 0L)
        val FAToken = mmkv.getString("FA_AUTH_TOKEN", "")!!
        val currentTime = System.currentTimeMillis()
        if (FAToken.isBlank() || FAExpiry <= currentTime) {
            authNetworkService.getAccessToken().apply {
                mmkv.putString("FA_AUTH_TOKEN", accessToken)
                mmkv.putLong("FA_AUTH_EXPIRY", accessTokenExpirationMs)
            }
        }
        return "Bearer ${mmkv.getString("FA_AUTH_TOKEN", "")}"
    }

    suspend fun getUserDetails(context: MainActivity) : User
        = spotifyNetworkService.getUserDetails(accessToken(context))

    suspend fun getTopTracks(
        context: MainActivity,
        timeRange: String,
        limit: Int,
        offset: Int
    ) : TopItems<Song> = spotifyNetworkService.getTopTracks(
        accessToken(context),
        timeRange, limit, offset
    )

    suspend fun getTopArtists(
        context: MainActivity,
        timeRange: String,
        limit: Int,
        offset: Int
    ) : TopItems<Artist> = spotifyNetworkService.getTopArtists(
        accessToken(context),
        timeRange, limit, offset
    )

    suspend fun getRecommendations(
        context: MainActivity,
        limit: Int,
        artistSeeds: List<String>? = null,
        trackSeeds: String? = null,
        genreSeeds: String? = null,
        danceability: Float? = null,
        energy: Float? = null,
        instrumentalness: Float? = null,
        liveness: Float? = null,
        popularity: Int? = null,
        speechiness: Float? = null
    ) : RecommendationResponse = spotifyNetworkService.getRecommendations(
        authorization = accessToken(context),
        limit = limit,
        artistSeeds = artistSeeds,
        trackSeeds = trackSeeds,
        genreSeeds = genreSeeds,
        danceability = danceability,
        energy = energy,
        instrumentalness = instrumentalness,
        liveness = liveness,
        popularity = popularity,
        speechiness = speechiness,
    )

    suspend fun createPlaylist(
        context: MainActivity,
        playlistBody: PlaylistBody
    ) : PlaylistResponse = spotifyNetworkService.createNewPlaylist(
        authorization = accessToken(context),
        body = playlistBody
    )

    suspend fun addTracksToPlaylist(
        context: MainActivity,
        playlistID: String,
        playlistAddBody: PlaylistAddBody
    ) = spotifyNetworkService.addTracksToPlaylist(
        authorization = accessToken(context),
        body = playlistAddBody,
        playlistID = playlistID
    )

    suspend fun getFriendActivity() : BuddylistActivity
        = buddylistNetworkService.getFriendActivity(getFAAccessToken())

    suspend fun getAvailableGenreSeeds(context: MainActivity) : GenreSeeds
        = spotifyNetworkService.getAvailableGenreSeeds(accessToken(context))

}