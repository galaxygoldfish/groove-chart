package com.groovechart.app.model

data class BuddylistItem(
    val timestamp: Long,
    val user: BuddylistUser,
    val track: BuddylistTrack
)