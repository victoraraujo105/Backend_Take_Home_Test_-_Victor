package com.noom.interview.fullstack.sleep.service

import com.noom.interview.fullstack.sleep.model.SleepLog
import com.noom.interview.fullstack.sleep.model.User
import com.noom.interview.fullstack.sleep.model.dto.SleepLogLastNightCreateRequest
import com.noom.interview.fullstack.sleep.repository.SleepLogRepository
import org.springframework.stereotype.Service

@Service
class SleepLogService(
    val sleepLogRepository: SleepLogRepository,
    val adminUser: User,    // User is hardcoded for this assignment
) {
    fun saveLastNight(request: SleepLogLastNightCreateRequest): SleepLog {
        val sleepLog = request.toEntity(adminUser)
        return sleepLogRepository.save(sleepLog)
    }
}