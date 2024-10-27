package com.noom.interview.fullstack.sleep.model.dto

import com.fasterxml.jackson.annotation.JsonFormat
import com.noom.interview.fullstack.sleep.model.SleepQuality
import java.time.LocalTime
import javax.validation.constraints.NotNull
import java.time.Duration
import java.time.LocalDate

data class SleepLogLastMonthStatsResponse(
    @NotNull
    val averageTotalTime: Duration,

    @NotNull
    @JsonFormat(pattern = "HH:mm:ss")
    val averageWakeUpTime: LocalTime,

    @NotNull
    @JsonFormat(pattern = "HH:mm:ss")
    val averageSleepTime: LocalTime,

    @NotNull
    @JsonFormat(pattern = "MM-dd-yyyy")
    val initialDate: LocalDate,

    @NotNull
    @JsonFormat(pattern = "MM-dd-yyyy")
    val finalDate: LocalDate,

    @NotNull
    val sleepQualityCount: Map<SleepQuality, Long>
)

