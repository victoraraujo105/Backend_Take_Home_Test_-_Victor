package com.noom.interview.fullstack.sleep.repository;

import com.noom.interview.fullstack.sleep.model.SleepLog;
import com.noom.interview.fullstack.sleep.model.SleepQuality;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Set;

public interface SleepLogRepository extends JpaRepository<SleepLog, Long> {
    interface AveragesResult {
        Long getAverageTotalTimeSeconds();
        LocalTime getAverageStartTime();
        LocalTime getAverageEndTime();
    }

    interface SleepQualityCount {
        SleepQuality getQuality();
        Long getCount();
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

    @Query("SELECT s.quality as quality, count(*) as count FROM SleepLog s WHERE s.userId = :userId AND s.startTime >= :initialDate AND s.endTime < :finalDate GROUP BY s.quality")
    Set<SleepQualityCount> getSleepQualityCount(@Param("userId") Long userId, @Param("initialDate") LocalDateTime initialDate, @Param("finalDate") LocalDateTime finalDate);

}
