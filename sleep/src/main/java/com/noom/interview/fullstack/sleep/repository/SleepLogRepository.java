package com.noom.interview.fullstack.sleep.repository;

import com.noom.interview.fullstack.sleep.model.SleepLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.time.LocalTime;

public interface SleepLogRepository extends JpaRepository<SleepLog, Long> {
    interface AveragesResult {
        Long getAverageTotalTimeSeconds();
        LocalTime getAverageStartTime();
        LocalTime getAverageEndTime();
    }

    @Query(value =
        "SELECT " +
        "EXTRACT(EPOCH FROM AVG(end_time - start_time)) AS averageTotalTimeSeconds, " +
        "CAST(AVG(start_time - CAST(start_time AS DATE)) AS TIME) AS averageStartTime, " +
        "CAST(AVG(end_time - CAST(end_time AS DATE)) AS TIME) AS averageEndTime " +
        "FROM sleep_logs " +
        "WHERE user_id = :userId " +
        "AND start_time >= :initialDate " +
        "AND end_time <= :finalDate", nativeQuery = true)
    AveragesResult getAverages(Long userId, LocalDateTime initialDate, LocalDateTime finalDate);
}
