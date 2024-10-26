/*
 * Copyright (C) 2023 Noom, Inc.
 */
package com.noom.interview.fullstack.sleep.controller

import com.noom.interview.fullstack.sleep.model.dto.SleepLogLastNightCreateRequest
import com.noom.interview.fullstack.sleep.service.SleepLogService
import org.springframework.web.bind.annotation.*
import javax.validation.Valid

@RestController
@RequestMapping("/sleep-logs")
class SleepLogController(
    val sleepLogService: SleepLogService
) {
    @PostMapping("/last-night")
    fun lastNight(@RequestBody @Valid sleepLog: SleepLogLastNightCreateRequest) {
        sleepLogService.saveLastNight(sleepLog)
    }
}
