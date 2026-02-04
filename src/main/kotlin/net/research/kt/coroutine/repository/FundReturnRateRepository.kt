package net.research.kt.coroutine.repository

import kotlinx.coroutines.delay
import net.research.kt.coroutine.domain.CurrentFundReturnRate
import net.research.kt.coroutine.domain.FundReturnRate
import org.slf4j.LoggerFactory
import java.time.Year

/**
 * Repository for fetching fund return rates.
 * Simulates external service/market data calls with delay.
 */
class FundReturnRateRepository {

    private val logger = LoggerFactory.getLogger(FundReturnRateRepository::class.java)

    private val historicalRates = listOf(
        FundReturnRate(2014, 0.0823),
        FundReturnRate(2015, 0.0795),
        FundReturnRate(2016, 0.0864),
        FundReturnRate(2017, 0.0891),
        FundReturnRate(2018, 0.0742),
        FundReturnRate(2019, 0.0813),
        FundReturnRate(2020, 0.0698),
        FundReturnRate(2021, 0.0872),
        FundReturnRate(2022, 0.0756),
        FundReturnRate(2023, 0.0834),
        FundReturnRate(2024, 0.0867),
        FundReturnRate(2025, 0.0845),
        FundReturnRate(2026, 0.0880)
    )

    suspend fun getCurrentReturnRate(): CurrentFundReturnRate {
        logger.info("Fetching current fund return rate")
        // Simulate external API call delay
        delay(100)

        val currentYear = Year.now().value
        val currentRate = historicalRates.find { it.year == currentYear }?.returnRate ?: 0.085

        val last5Years = historicalRates.takeLast(5)
        val avg5Years = last5Years.map { it.returnRate }.average()

        val last10Years = historicalRates.takeLast(10)
        val avg10Years = last10Years.map { it.returnRate }.average()

        return CurrentFundReturnRate(
            currentYear = currentYear,
            currentRate = currentRate,
            averageRate5Years = avg5Years,
            averageRate10Years = avg10Years,
            historicalRates = historicalRates
        )
    }
}
