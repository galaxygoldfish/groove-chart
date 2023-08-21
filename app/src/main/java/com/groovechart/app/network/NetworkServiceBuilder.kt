package com.groovechart.app.network

import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object NetworkServiceBuilder {

    const val BASE_URL_SPOTIFY = "https://api.spotify.com/"
    const val BASE_URL_BUDDYLIST = "https://guc-spclient.spotify.com/presence-view/"
    const val BASE_URL_AUTH = "https://open.spotify.com/"

    private val gsonObject = GsonBuilder().setLenient().create()
    private val okHttpClient = OkHttpClient.Builder().addInterceptor(
        HttpLoggingInterceptor().apply { level = HttpLoggingInterceptor.Level.BODY }
    ).build()

    fun getRetrofit(baseUrl: String): Retrofit {
        return Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create(gsonObject))
            .build()
    }

    inline fun <reified T> apiService(baseUrl: String): T {
        return getRetrofit(baseUrl).create(T::class.java)
    }

}