package com.noom.interview.fullstack.sleep.repository

import com.noom.interview.fullstack.sleep.model.SleepLog
import com.noom.interview.fullstack.sleep.model.SleepQuality
import com.noom.interview.fullstack.sleep.model.User
import org.assertj.core.api.Assertions.*
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.context.annotation.Bean
import org.springframework.dao.DataIntegrityViolationException
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

@DataJpaTest
class SleepLogRepositoryTest {

    @Autowired
    private lateinit var sleepLogRepository: SleepLogRepository

    @Autowired
    private lateinit var userRepository: UserRepository

    private lateinit var user: User

    @BeforeEach
    fun setUp() {
        this.user = userRepository.let {
            it.findByUsername("user")
                ?: it.save(User.builder()
                    .username("user")
                    .password("pass")
                    .build())
        }
        sleepLogRepository.saveAll(listOf(
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
    }

    @AfterEach
    fun tearDown() {
        sleepLogRepository.deleteAll()
    }

    @Test
    fun findAll_WhenSleepLogsExist_ReturnsSleepLogs() {
        val user = userRepository.findByUsername("user")
        val sleepLogs = sleepLogRepository.findAll()
        assertThat(sleepLogs).hasSize(3)
        assertThat(sleepLogs.map { it.userId }).allMatch { it == user!!.id }
        assertThat(sleepLogs.map { it.quality }).containsExactlyInAnyOrder(SleepQuality.GOOD, SleepQuality.OK, SleepQuality.BAD)
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

}