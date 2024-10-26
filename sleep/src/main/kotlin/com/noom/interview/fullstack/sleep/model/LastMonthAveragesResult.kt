package com.noom.interview.fullstack.sleep.model

import java.time.LocalDate
import java.time.LocalTime

data class LastMonthAveragesResult(
    val averageTotalTimeSeconds: Long,
    val averageStartTime: LocalTime,
    val averageEndTime: LocalTime,
    val initialDate: LocalDate,
    val finalDate: LocalDate
)