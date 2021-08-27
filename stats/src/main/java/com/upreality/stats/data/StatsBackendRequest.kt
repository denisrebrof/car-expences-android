package com.upreality.stats.data

data class StatsBackendRequest(
    val types: List<Long>? = null,
    val startTime: Long? = null,
    val endTime: Long? = null,
)
