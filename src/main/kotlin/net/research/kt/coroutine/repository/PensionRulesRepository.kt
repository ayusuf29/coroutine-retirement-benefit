package net.research.kt.coroutine.repository

import kotlinx.coroutines.delay
import net.research.kt.coroutine.domain.PensionRules
import org.slf4j.LoggerFactory

/**
 * Repository for fetching pension rules and regulations.
 * Simulates configuration/rules service calls with delay.
 */
class PensionRulesRepository {

    private val logger = LoggerFactory.getLogger(PensionRulesRepository::class.java)

    private val currentRules = PensionRules(
        normalRetirementAge = 58,
        earlyRetirementAge = 50,
        minimumYearsOfService = 5,
        earlyRetirementPenaltyRate = 0.05,
        monthlyBenefitDivisor = 180
    )

    suspend fun getCurrentRules(): PensionRules {
        logger.info("Fetching current pension rules")
        // Simulate configuration service delay
        delay(80)
        return currentRules
    }
}
