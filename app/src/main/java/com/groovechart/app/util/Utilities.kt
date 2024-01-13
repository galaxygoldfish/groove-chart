package com.groovechart.app.util

import java.util.concurrent.TimeUnit
import kotlin.math.abs

fun isWithin5Minutes(unixTimestamp: Long): Boolean {
    val currentTime = System.currentTimeMillis() / 1000
    val timestamp = unixTimestamp / 1000
    val fiveMinutesInMillis = 5 * 60
    val timeDifference = abs(currentTime - timestamp)
    return timeDifference <= fiveMinutesInMillis
}

fun calculateTimeDifference(unixTimestamp: Long): String {
    val currentTime = System.currentTimeMillis()
    val timeDifferenceMillis = abs(currentTime - unixTimestamp)
    val seconds = TimeUnit.MILLISECONDS.toSeconds(timeDifferenceMillis)
    val minutes = TimeUnit.MILLISECONDS.toMinutes(timeDifferenceMillis)
    val hours = TimeUnit.MILLISECONDS.toHours(timeDifferenceMillis)
    val days = TimeUnit.MILLISECONDS.toDays(timeDifferenceMillis)
    return when {
        seconds < 60 -> "${seconds}s"
        minutes < 60 -> "${minutes}m"
        hours < 24 -> "${hours}h"
        else -> "${days}d"
    }
}



fun getSimilarStrings(query: String, stringList: List<String>): List<String> {
    return stringList.filter { isSimilar(query, it, 2) }
}

fun isSimilar(query: String, string: String, threshold: Int): Boolean {
    val queryLength = query.length
    val stringLength = string.length
    if (abs(queryLength - stringLength) > threshold) {
        return false
    }
    val dp = Array(queryLength + 1) { IntArray(stringLength + 1) }
    for (i in 0..queryLength) {
        for (j in 0..stringLength) {
            when {
                i == 0 -> dp[i][j] = j
                j == 0 -> dp[i][j] = i
                else -> {
                    dp[i][j] = minOf(
                        dp[i - 1][j] + 1,
                        dp[i][j - 1] + 1,
                        dp[i - 1][j - 1] + if (query[i - 1] == string[j - 1]) 0 else 1
                    )
                }
            }
        }
    }
    return dp[queryLength][stringLength] <= threshold
}