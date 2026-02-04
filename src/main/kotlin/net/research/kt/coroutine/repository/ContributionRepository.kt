package net.research.kt.coroutine.repository

import kotlinx.coroutines.delay
import net.research.kt.coroutine.domain.Contribution
import net.research.kt.coroutine.domain.ContributionHistory
import org.slf4j.LoggerFactory
import java.time.LocalDate
import kotlin.random.Random

/**
 * Repository for fetching contribution history.
 * Simulates database/external service calls with delay.
 */
class ContributionRepository {

    private val logger = LoggerFactory.getLogger(ContributionRepository::class.java)

    suspend fun findByParticipantId(participantId: String): ContributionHistory? {
        logger.info("Fetching contribution history for: $participantId")
        // Simulate database call delay - contributions are typically large datasets
        delay(300)

        return when (participantId) {
            "P001" -> generateContributionHistory(participantId, 19, 15_000_000.0)
            "P002" -> generateContributionHistory(participantId, 14, 12_000_000.0)
            "P003" -> generateContributionHistory(participantId, 24, 20_000_000.0)
            else -> null
        }
    }

    private fun generateContributionHistory(
        participantId: String,
        years: Int,
        currentSalary: Double
    ): ContributionHistory {
        val contributions = mutableListOf<Contribution>()
        val today = LocalDate.now()

        for (i in 0 until (years * 12)) {
            val month = today.minusMonths(i.toLong())
            // Simulate salary growth over time (3% annual increase)
            val yearsAgo = i / 12
            val salaryAtTime = currentSalary / Math.pow(1.03, yearsAgo.toDouble())

            contributions.add(
                Contribution(
                    month = month,
                    employeeContribution = salaryAtTime * 0.02, // 2% employee
                    employerContribution = salaryAtTime * 0.0354, // 3.54% employer
                    salaryBase = salaryAtTime
                )
            )
        }

        return ContributionHistory(
            participantId = participantId,
            contributions = contributions.reversed()
        )
    }
}
