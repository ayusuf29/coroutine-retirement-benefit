package net.research.kt.coroutine.repository

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import net.research.kt.coroutine.dao.FundReturnRateDao
import net.research.kt.coroutine.domain.CurrentFundReturnRate
import org.jdbi.v3.core.Jdbi
import org.slf4j.LoggerFactory
import java.time.Year

/**
 * Repository for fetching fund return rates from MySQL database.
 */
class FundReturnRateRepository(private val jdbi: Jdbi) {

    private val logger = LoggerFactory.getLogger(FundReturnRateRepository::class.java)

    suspend fun getCurrentReturnRate(): CurrentFundReturnRate = withContext(Dispatchers.IO) {
        logger.info("Fetching current fund return rate")
        // Simulate external API call delay
        delay(100)

        jdbi.withExtension<CurrentFundReturnRate, FundReturnRateDao, Exception>(FundReturnRateDao::class.java) { dao ->
            val historicalRates = dao.findAll()
            val currentYear = Year.now().value
            val currentRate = historicalRates.find { it.year == currentYear }?.returnRate ?: 0.085

            val last5Years = dao.findRecent(5).reversed()
            val avg5Years = if (last5Years.isNotEmpty()) {
                last5Years.map { it.returnRate }.average()
            } else {
                0.085
            }

            val last10Years = dao.findRecent(10).reversed()
            val avg10Years = if (last10Years.isNotEmpty()) {
                last10Years.map { it.returnRate }.average()
            } else {
                0.085
            }

            CurrentFundReturnRate(
                currentYear = currentYear,
                currentRate = currentRate,
                averageRate5Years = avg5Years,
                averageRate10Years = avg10Years,
                historicalRates = historicalRates
            )
        }
    }
}
