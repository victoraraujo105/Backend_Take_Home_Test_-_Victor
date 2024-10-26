package com.noom.interview.fullstack.sleep.repository

import com.noom.interview.fullstack.sleep.model.SleepLog
import com.noom.interview.fullstack.sleep.model.SleepQuality
import com.noom.interview.fullstack.sleep.model.User
import io.zonky.test.db.AutoConfigureEmbeddedDatabase
import org.assertj.core.api.Assertions.*
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import java.time.Duration
import java.time.LocalDateTime

@DataJpaTest
@AutoConfigureEmbeddedDatabase
class SleepLogRepositoryTest {

    @Autowired
    private lateinit var sleepLogRepository: SleepLogRepository

    @Autowired
    private lateinit var userRepository: UserRepository

    private lateinit var user: User

    private val logs = arrayListOf<SleepLog>()

    private val now: LocalDateTime = LocalDateTime.now()

    private lateinit var lastMonthLogs: List<SleepLog>
    
    @BeforeEach
    fun setUp() {
        this.user = userRepository.let {
            it.findByUsername("user")
                ?: it.save(User.builder()
                    .username("user")
                    .password("pass")
                    .build())
        }
        this.logs.addAll(listOf(
            SleepLog.builder()
                .quality(SleepQuality.GOOD)
                .startTime(
                    LocalDateTime.of(2021, 1, 1, 22, 0))
                .endTime(
                    LocalDateTime.of(2021, 1, 2, 6, 0))
                .userId(user.id)
                .build(),
            SleepLog.builder()
                .quality(SleepQuality.OK)
                .startTime(
                    LocalDateTime.of(2021, 1, 2, 22, 0))
                .endTime(
                    LocalDateTime.of(2021, 1, 3, 6, 0))
                .userId(user.id)
                .build(),
            SleepLog.builder()
                .quality(SleepQuality.BAD)
                .startTime(
                    LocalDateTime.of(2021, 1, 3, 22, 0))
                .endTime(
                    LocalDateTime.of(2021, 1, 4, 6, 0))
                .userId(user.id)
                .build()
        ))
        this.setUp_LastMonth()
        sleepLogRepository.saveAll(logs)
    }

    fun setUp_LastMonth() {
        this.lastMonthLogs = generateSequence(1) { it + 1 }
            .take(30)
            .map {
                val startTime = now
                    .minusDays(it.toLong())
                    .withHour((19..23).random())
                    .withMinute((0..59).random())
                val endTime = startTime
                    .plusHours((6..8).random().toLong())
                    .plusMinutes((0..59).random().toLong())
                SleepLog.builder()
                    .quality(SleepQuality.entries.random())
                    .startTime(startTime)
                    .endTime(endTime)
                    .userId(user.id)
                .build()
            }
            .toList()
        logs.addAll(this.lastMonthLogs)
    }

    @AfterEach
    fun tearDown() {
        sleepLogRepository.deleteAll()
    }

    @Test
    fun findAll_WhenSleepLogsExist_ReturnsSleepLogs() {
        val user = userRepository.findByUsername("user")
        val sleepLogs = sleepLogRepository.findAll()
        assertThat(sleepLogs).hasSize(logs.size)
        assertThat(sleepLogs.map { it.userId }).allMatch { it == user!!.id }
        assertThat(sleepLogs.map { it.quality }).containsExactlyInAnyOrderElementsOf(this.logs.map { it.quality })
    }

    @Test
    fun createSleepLog_WhenSleepLogDoesNotExist_SavesSleepLog() {
        val sleepLog = SleepLog.builder()
            .quality(SleepQuality.GOOD)
            .startTime(
                LocalDateTime.of(2021, 1, 4, 22, 0))
            .endTime(
                LocalDateTime.of(2021, 1, 5, 6, 0))
            .userId(this.user.id)
        .build()
        val savedSleepLog = sleepLogRepository.save(sleepLog)
        assertThat(savedSleepLog.id).isNotNull
        assertThat(savedSleepLog.quality).isEqualTo(SleepQuality.GOOD)
        assertThat(savedSleepLog.startTime).isEqualTo(LocalDateTime.of(2021, 1, 4, 22, 0))
        assertThat(savedSleepLog.endTime).isEqualTo(LocalDateTime.of(2021, 1, 5, 6, 0))
        assertThat(savedSleepLog.userId).isEqualTo(this.user.id)
    }

    @Test
    fun createSleepLog_WhenSleepLogExists_UpdatesSleepLog() {
        val sleepLog = sleepLogRepository.findAll().first()
        sleepLog.quality = SleepQuality.OK
        val updatedSleepLog = sleepLogRepository.save(sleepLog)
        assertThat(updatedSleepLog.quality).isEqualTo(SleepQuality.OK)
    }
    
    @Test
    fun getLastMonthAverages_WhenSleepLogsExist_ReturnsAverages() {
        val averages = sleepLogRepository.getLastMonthAverages(this.user.id, now.toLocalDate())
        assertThat(averages).isNotNull
        assertThat(averages.averageTotalTimeSeconds).isEqualTo(this.lastMonthLogs.map { Duration.between(it.startTime, it.endTime).seconds }.average().toLong())
        assertThat(averages.averageStartTime.toSecondOfDay()).isEqualTo(this.lastMonthLogs.map { it.startTime.toLocalTime().toSecondOfDay() }.average().toInt())
        assertThat(averages.averageEndTime.toSecondOfDay()).isEqualTo(this.lastMonthLogs.map { it.endTime.toLocalTime().toSecondOfDay() }.average().toInt())
        assertThat(averages.initialDate).isEqualTo(this.lastMonthLogs.minOfOrNull { it.startTime.toLocalDate() })
        assertThat(averages.finalDate).isEqualTo(this.lastMonthLogs.maxOfOrNull { it.endTime.toLocalDate() })
    }

    @Test
    fun getLastMonthSleepQualityCount_WhenSleepLogsExist_ReturnsQualityCount() {
        val qualityCount = sleepLogRepository.getLastMonthSleepQualityCount(this.user.id, now.toLocalDate())
        assertThat(qualityCount).isNotNull
        assertThat(qualityCount[SleepQuality.GOOD]).isEqualTo(this.lastMonthLogs.count { it.quality == SleepQuality.GOOD }.toLong())
        assertThat(qualityCount[SleepQuality.OK]).isEqualTo(this.lastMonthLogs.count { it.quality == SleepQuality.OK }.toLong())
        assertThat(qualityCount[SleepQuality.BAD]).isEqualTo(this.lastMonthLogs.count { it.quality == SleepQuality.BAD }.toLong())
    }

}