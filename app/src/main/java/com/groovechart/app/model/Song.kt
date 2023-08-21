package com.groovechart.app.model

data class Song(
    val album: Album,
    val name: String,
    val id: String,
    val uri: String
)
data class Album(
    val artists: List<Artist>,
    val images: List<Image>,
    val id: String,
    val name: String
)
