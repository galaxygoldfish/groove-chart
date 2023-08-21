package com.groovechart.app.model

data class Artist(
    val name: String,
    val images: List<Image>,
    val genres: List<String>? = null
)
