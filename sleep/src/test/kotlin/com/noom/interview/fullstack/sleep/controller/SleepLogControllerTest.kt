package com.noom.interview.fullstack.sleep.controller

import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.jacksonMapperBuilder
import com.noom.interview.fullstack.sleep.model.LastMonthAveragesResult
import com.noom.interview.fullstack.sleep.model.SleepLog
import com.noom.interview.fullstack.sleep.model.SleepQuality
import com.noom.interview.fullstack.sleep.model.User
import com.noom.interview.fullstack.sleep.model.dto.SleepLogLastMonthStatsResponse
import com.noom.interview.fullstack.sleep.model.dto.SleepLogLastNightCreateRequest
import com.noom.interview.fullstack.sleep.repository.SleepLogRepository
import com.noom.interview.fullstack.sleep.service.SleepLogService
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.ArgumentCaptor
import org.mockito.Mockito
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.argThat
import org.mockito.kotlin.verify
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.boot.test.mock.mockito.SpyBean
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter


@WebMvcTest(SleepLogController::class)
@ExtendWith(MockitoExtension::class)
class SleepLogControllerTest {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @MockBean
    private lateinit var sleepLogRepository: SleepLogRepository
    @MockBean
    private lateinit var adminUser: User

    @SpyBean
    private lateinit var sleepLogService: SleepLogService

    private val dateTimeFormat = DateTimeFormatter.ofPattern("MM-dd-yyyy HH:mm")

    @BeforeEach
    fun setUp() {
        Mockito.`when`(adminUser.id).thenReturn(1)
    }

    @Test
    fun postLastNight_WhenValidRequest_ThenSave() {
        val now = LocalDateTime.now().withSecond(0).withNano(0)
        val startTime = now.minusDays(1)
            .withHour(22)
            .withMinute(13)
        val endTime = now
            .withHour(6)
            .withMinute(8)
        val sleepQuality = SleepQuality.GOOD
        // Given
        Mockito.`when`(sleepLogRepository.save(Mockito.any(SleepLog::class.java)))
            .thenAnswer { it.getArgument<SleepLog>(0) }

        // When
        this.mockMvc.perform(
            post("/sleep-logs/last-night")
                .contentType("application/json")
                .content(
                    """
                    {
                        "startTime": "${startTime.format(dateTimeFormat)}",
                        "endTime": "${endTime.format(dateTimeFormat)}",
                        "sleepQuality": "${sleepQuality}"
                    }
                    """.trimIndent()
        )).andExpect { status().isOk }

        // Then
        // Verify that the sleepLogService.saveLastNight method was called
        verify(sleepLogService).saveLastNight(argThat {
            this.startTime == startTime &&
            this.endTime == endTime &&
            this.sleepQuality == sleepQuality
        })
        val argumentCaptor = ArgumentCaptor.forClass(SleepLog::class.java)
        verify(sleepLogRepository).save(argumentCaptor.capture())
        val savedSleepLog = argumentCaptor.value
        assertThat(savedSleepLog.startTime).isEqualTo(startTime)
        assertThat(savedSleepLog.endTime).isEqualTo(endTime)
        assertThat(savedSleepLog.quality).isEqualTo(sleepQuality)
        assertThat(savedSleepLog.userId).isEqualTo(adminUser.id)
    }

    @Test
    fun getLastMonthStats_WhenCalled_ThenReturnStats() {
        // Given
        val now = LocalDateTime.now().withSecond(0).withNano(0)
        val today = now.toLocalDate()
        val lastMonthAverages = LastMonthAveragesResult(
            averageTotalTimeSeconds = LocalTime.of(7, 47, 26).toSecondOfDay().toLong(),
            averageStartTime = LocalTime.of(22, 7, 26),
            averageEndTime = LocalTime.of(6, 54, 52),
            initialDate = today.minusDays(30),
            finalDate = today
        )
        val sleepQualityCount = mapOf(
            SleepQuality.BAD to 3L,
            SleepQuality.OK to 5L,
            SleepQuality.GOOD to 22L
        )

        Mockito.`when`(sleepLogRepository.getLastMonthAverages(adminUser.id, today))
            .thenReturn(lastMonthAverages)
        Mockito.`when`(sleepLogRepository.getLastMonthSleepQualityCount(adminUser.id, today))
            .thenReturn(sleepQualityCount)

        // When
        this.mockMvc.perform(
            get("/sleep-logs/last-month-stats")
        ).andExpect { status().isOk }
            .andDo {
                val mapper = jacksonMapperBuilder()
                    .addModule(JavaTimeModule())
                    .build()
                val response = mapper.readValue(it.response.contentAsString, SleepLogLastMonthStatsResponse::class.java)
                assertThat(response.averageTotalTime.toSeconds()).isEqualTo(lastMonthAverages.averageTotalTimeSeconds)
                assertThat(response.averageWakeUpTime).isEqualTo(lastMonthAverages.averageStartTime)
                assertThat(response.averageSleepTime).isEqualTo(lastMonthAverages.averageEndTime)
                assertThat(response.initialDate).isEqualTo(lastMonthAverages.initialDate)
                assertThat(response.finalDate).isEqualTo(lastMonthAverages.finalDate)
                assertThat(response.sleepQualityCount).containsExactlyInAnyOrderEntriesOf(sleepQualityCount)
            }
    }

    @Test
    fun getLastMonthStats_WhenNoStats_ThenReturnNoContent() {
        // Given
        val today = LocalDateTime.now().toLocalDate()
        Mockito.`when`(sleepLogRepository.getLastMonthAverages(adminUser.id, today))
            .thenReturn(null)

        // When
        this.mockMvc.perform(
            get("/sleep-logs/last-month-stats")
        ).andExpect { status().isNoContent }
    }
}