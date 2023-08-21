package com.groovechart.app.model

data class TopItems<T>(
    val total: Int,
    val items: List<T>
)
