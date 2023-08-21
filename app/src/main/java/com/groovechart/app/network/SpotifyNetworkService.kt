package com.groovechart.app.network

import androidx.compose.ui.geometry.Offset
import com.groovechart.app.model.Artist
import com.groovechart.app.model.GenreSeeds
import com.groovechart.app.model.PlaylistAddBody
import com.groovechart.app.model.PlaylistBody
import com.groovechart.app.model.PlaylistResponse
import com.groovechart.app.model.RecommendationResponse
import com.groovechart.app.model.Song
import com.groovechart.app.model.TopItems
import com.groovechart.app.model.User
import com.tencent.mmkv.BuildConfig
import com.tencent.mmkv.MMKV
import okhttp3.Credentials
import retrofit2.http.Body
import retrofit2.http.Field
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface SpotifyNetworkService {

    @GET("/v1/me")
    suspend fun getUserDetails(@Header("Authorization") authorization: String) : User

    @GET("/v1/me/top/tracks")
    suspend fun getTopTracks(
        @Header("Authorization") authorization: String,
        @Query("time_range") timeRange: String,
        @Query("limit") limit: Int,
        @Query("offset") offset: Int
    ) : TopItems<Song>

    @GET("/v1/me/top/artists")
    suspend fun getTopArtists(
        @Header("Authorization") authorization: String,
        @Query("time_range") timeRange: String,
        @Query("limit") limit: Int,
        @Query("offset") offset: Int
    ) : TopItems<Artist>

    @GET("/v1/recommendations")
    suspend fun getRecommendations(
        @Header("Authorization") authorization: String,
        @Query("limit") limit: Int,
        @Query("seed_artists") artistSeeds: List<String>? = null,
        @Query("seed_tracks") trackSeeds: String? = null,
        @Query("seed_genres") genreSeeds: String? = null,
        @Query("target_danceability") danceability: Float? = null,
        @Query("target_energy") energy: Float? = null,
        @Query("target_instrumentalness") instrumentalness: Float? = null,
        @Query("target_liveness") liveness: Float? = null,
        @Query("target_popularity") popularity: Int? = null,
        @Query("target_speechiness") speechiness: Float? = null
    ) : RecommendationResponse

    @POST("v1/users/{user_id}/playlists")
    suspend fun createNewPlaylist(
        @Header("Authorization") authorization: String,
        @Path("user_id") userID: String = MMKV.defaultMMKV().getString("USER_ID", "")!!,
        @Body body: PlaylistBody
    ) : PlaylistResponse

    @POST("v1/playlists/{playlist_id}/tracks")
    suspend fun addTracksToPlaylist(
        @Header("Authorization") authorization: String,
        @Path("playlist_id") playlistID: String,
        @Body body: PlaylistAddBody
    )

    @GET("v1/recommendations/available-genre-seeds")
    suspend fun getAvailableGenreSeeds(@Header("Authorization") authorization: String) : GenreSeeds

}