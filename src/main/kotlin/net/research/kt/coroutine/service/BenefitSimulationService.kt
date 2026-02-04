package net.research.kt.coroutine.service

import kotlinx.coroutines.*
import net.research.kt.coroutine.domain.*
import net.research.kt.coroutine.exception.ParticipantNotFoundException
import net.research.kt.coroutine.repository.*
import org.slf4j.LoggerFactory
import java.time.LocalDateTime
import kotlin.system.measureTimeMillis

/**
 * Service for calculating pension benefit simulations using Kotlin Coroutines.
 *
 * This service demonstrates best practices for async/concurrent operations:
 * - Parallel fetching of independent data sources
 * - Structured concurrency with proper error handling
 * - Timeout management
 * - Logging correlation
 */
class BenefitSimulationService(
    private val participantRepository: ParticipantRepository,
    private val contributionRepository: ContributionRepository,
    private val fundReturnRateRepository: FundReturnRateRepository,
    private val pensionRulesRepository: PensionRulesRepository,
    private val timeoutMillis: Long = 30000
) {

    private val logger = LoggerFactory.getLogger(BenefitSimulationService::class.java)

    /**
     * Simulates pension benefit for a participant using coroutines for parallel data fetching.
     *
     * Steps:
     * 1. Fetch participant profile (validates participant exists)
     * 2. Fetch all other data in parallel (contributions, fund rates, rules)
     * 3. Calculate benefit based on fetched data
     */
    suspend fun simulateBenefit(participantId: String): BenefitSimulationResult {
        logger.info("Starting benefit simulation for participant: $participantId")

        val calculationTime = measureTimeMillis {
            return withTimeout(timeoutMillis) {
                // Step 1: Fetch participant first (validates existence)
                val profile = participantRepository.findById(participantId)
                    ?: throw ParticipantNotFoundException("Participant not found: $participantId")

                // Step 2: Fetch remaining data in parallel using async
                val (contributions, fundRate, rules) = coroutineScope {
                    logger.info("Fetching data in parallel for participant: $participantId")

                    val contributionsDeferred = async {
                        contributionRepository.findByParticipantId(participantId)
                    }

                    val fundRateDeferred = async {
                        fundReturnRateRepository.getCurrentReturnRate()
                    }

                    val rulesDeferred = async {
                        pensionRulesRepository.getCurrentRules()
                    }

                    // Await all results
                    Triple(
                        contributionsDeferred.await(),
                        fundRateDeferred.await(),
                        rulesDeferred.await()
                    )
                }

                // Step 3: Calculate benefit
                calculateBenefit(
                    profile = profile,
                    contributions = contributions,
                    fundRate = fundRate,
                    rules = rules,
                    calculationDurationMs = 0 // Will be set after
                )
            }
        }

        logger.info("Benefit simulation completed for participant: $participantId in ${calculationTime}ms")

        // Update calculation duration
        return simulateBenefit(participantId).copy(calculationDurationMs = calculationTime)
    }

    /**
     * Simulates benefits for multiple participants concurrently.
     */
    suspend fun simulateBenefitBatch(participantIds: List<String>): List<BenefitSimulationResult> {
        logger.info("Starting batch benefit simulation for ${participantIds.size} participants")

        return coroutineScope {
            participantIds.map { participantId ->
                async {
                    try {
                        simulateBenefit(participantId)
                    } catch (e: Exception) {
                        logger.error("Failed to simulate benefit for participant: $participantId", e)
                        null
                    }
                }
            }.awaitAll().filterNotNull()
        }
    }

    private fun calculateBenefit(
        profile: ParticipantProfile,
        contributions: ContributionHistory?,
        fundRate: CurrentFundReturnRate?,
        rules: PensionRules?,
        calculationDurationMs: Long
    ): BenefitSimulationResult {
        // Default values if data not found
        val safeContributions = contributions ?: ContributionHistory(profile.participantId, emptyList())
        val safeFundRate = fundRate ?: CurrentFundReturnRate(2026, 0.085, 0.085, 0.085, emptyList())
        val safeRules = rules ?: PensionRules()

        val currentAge = profile.getAge()
        val yearsOfService = profile.getYearsOfParticipation()
        val totalContributions = safeContributions.getTotalContributions()

        // Project fund value with compound interest
        val projectedFundValue = calculateProjectedValue(
            totalContributions,
            safeFundRate.averageRate10Years
        )

        // Check eligibility
        val isEligible = safeRules.isEligibleForRetirement(currentAge, yearsOfService)

        // Calculate early retirement penalty
        val penalty = safeRules.calculateEarlyRetirementPenalty(currentAge)
        val penaltyMultiplier = 1.0 - penalty

        // Calculate benefits
        val estimatedLumpSum = projectedFundValue * penaltyMultiplier
        val estimatedMonthlyBenefit = estimatedLumpSum / safeRules.monthlyBenefitDivisor

        return BenefitSimulationResult(
            participantId = profile.participantId,
            participantName = profile.name,
            currentAge = currentAge,
            yearsOfService = yearsOfService,
            totalContributions = totalContributions,
            projectedFundValue = projectedFundValue,
            estimatedLumpSum = estimatedLumpSum,
            estimatedMonthlyBenefit = estimatedMonthlyBenefit,
            isEligibleForRetirement = isEligible,
            earlyRetirementPenalty = penalty,
            simulationTimestamp = LocalDateTime.now(),
            calculationDurationMs = calculationDurationMs,
            details = SimulationDetails(
                appliedReturnRate = safeFundRate.averageRate10Years,
                monthsOfContribution = safeContributions.getMonthsOfContribution(),
                retirementAge = safeRules.normalRetirementAge,
                minimumYearsOfService = safeRules.minimumYearsOfService,
                calculationMethod = "COMPOUND_INTEREST_WITH_PENALTY"
            )
        )
    }

    private fun calculateProjectedValue(principal: Double, annualRate: Double): Double {
        // Simple compound interest calculation
        // In real scenario, this would be more complex with monthly compounding
        return principal * (1 + annualRate)
    }
}
