package com.groovechart.app.model

data class AuthToken(
    val accessToken: String,
    val accessTokenExpirationMs: Long
)
