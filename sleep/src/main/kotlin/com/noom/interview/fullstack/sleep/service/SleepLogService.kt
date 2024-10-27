package com.noom.interview.fullstack.sleep.service

import com.noom.interview.fullstack.sleep.model.SleepLog
import com.noom.interview.fullstack.sleep.model.User
import com.noom.interview.fullstack.sleep.model.dto.SleepLogLastMonthStatsResponse
import com.noom.interview.fullstack.sleep.model.dto.SleepLogLastNightCreateRequest
import com.noom.interview.fullstack.sleep.repository.SleepLogRepository
import org.springframework.stereotype.Service
import java.time.Duration
import java.time.LocalDate
import java.util.*

@Service
class SleepLogService(
    val sleepLogRepository: SleepLogRepository,
    val adminUser: User,    // User is hardcoded for this assignment
) {
    fun saveLastNight(request: SleepLogLastNightCreateRequest): SleepLog {
        val sleepLog = request.toEntity(adminUser)
        return sleepLogRepository.save(sleepLog)
    }

    fun getLastMonthStats(): Optional<SleepLogLastMonthStatsResponse> {
        val today = LocalDate.now()
        val lastMonthAverages = sleepLogRepository.getLastMonthAverages(adminUser.id, today)
        if (lastMonthAverages == null) {
            return Optional.empty()
        }
        val lasMonthSleepQualityCount = sleepLogRepository.getLastMonthSleepQualityCount(adminUser.id, today)
        return Optional.of(SleepLogLastMonthStatsResponse(
            averageTotalTime = Duration.ofSeconds(lastMonthAverages.averageTotalTimeSeconds),
            averageWakeUpTime = lastMonthAverages.averageStartTime,
            averageSleepTime = lastMonthAverages.averageEndTime,
            initialDate = lastMonthAverages.initialDate,
            finalDate = lastMonthAverages.finalDate,
            sleepQualityCount = lasMonthSleepQualityCount
        ))
    }
}