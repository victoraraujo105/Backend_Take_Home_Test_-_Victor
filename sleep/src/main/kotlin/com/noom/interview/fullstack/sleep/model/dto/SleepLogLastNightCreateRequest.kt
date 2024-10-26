package com.noom.interview.fullstack.sleep.model.dto

import com.fasterxml.jackson.annotation.JsonFormat
import com.noom.interview.fullstack.sleep.model.SleepLog
import com.noom.interview.fullstack.sleep.model.SleepQuality
import com.noom.interview.fullstack.sleep.model.User
import java.time.LocalDateTime
import javax.validation.constraints.NotNull

data class SleepLogLastNightCreateRequest(
    @NotNull
    @JsonFormat(pattern = "MM-dd-yyyy HH:mm")
    val startTime: LocalDateTime,

    @NotNull
    @JsonFormat(pattern = "MM-dd-yyyy HH:mm")
    val endTime: LocalDateTime,

    @NotNull
    val sleepQuality: SleepQuality
) {
    fun toEntity(user: User) = SleepLog.builder()
            .userId(user.id)
            .startTime(startTime)
            .endTime(endTime)
            .quality(sleepQuality)
            .build()
}

