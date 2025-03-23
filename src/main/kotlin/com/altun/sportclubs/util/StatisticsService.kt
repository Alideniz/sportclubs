package com.altun.sportclubs.util

import org.hibernate.SessionFactory
import org.hibernate.stat.Statistics
import org.slf4j.LoggerFactory
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import javax.sql.DataSource

/**
 * Service for monitoring database and Hibernate performance
 * Based on Vlad Mihalcea's recommendations for tracking database performance metrics
 */
@Service
class StatisticsService(
    private val sessionFactory: SessionFactory,
    private val dataSource: DataSource
) {
    private val logger = LoggerFactory.getLogger(StatisticsService::class.java)
    private val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")

    /**
     * Log performance statistics every 15 minutes
     */
    @Scheduled(fixedRate = 900000) // 15 minutes
    fun logStatistics() {
        val stats = sessionFactory.statistics

        if (!stats.isStatisticsEnabled) {
            return
        }

        val now = LocalDateTime.now().format(formatter)

        logger.info("=== Hibernate Statistics [{$now}] ===")
        logger.info("Sessions: opened={}, closed={}", stats.sessionOpenCount, stats.sessionCloseCount)
        logger.info("Transactions: {}", stats.transactionCount)
        logger.info("Queries: executed={}, max-time={} ms", stats.queryExecutionCount, stats.queryExecutionMaxTime)
        logger.info(
            "Entities: fetched={}, inserted={}, updated={}, deleted={}",
            stats.entityFetchCount, stats.entityInsertCount, stats.entityUpdateCount, stats.entityDeleteCount
        )
        logger.info(
            "Collections: fetched={}, updated={}, recreated={}",
            stats.collectionFetchCount, stats.collectionUpdateCount, stats.collectionRecreateCount
        )
        logger.info(
            "Cache: hits={}, misses={}, puts={}",
            stats.secondLevelCacheHitCount, stats.secondLevelCacheMissCount, stats.secondLevelCachePutCount
        )

        logger.info("======================================")
    }

    /**
     * Reset statistics to start fresh
     */
    @Scheduled(cron = "0 0 0 * * *") // Midnight every day
    fun resetStatistics() {
        val statistics = sessionFactory.statistics
        statistics.clear()
        logger.info("Hibernate statistics reset at {}", LocalDateTime.now().format(formatter))
    }

    /**
     * Get current statistics snapshot
     */
    fun getStatistics(): Statistics {
        return sessionFactory.statistics
    }
} 