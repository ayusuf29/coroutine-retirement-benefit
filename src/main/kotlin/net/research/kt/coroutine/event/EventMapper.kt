package net.research.kt.coroutine.event

import net.research.kt.coroutine.domain.BenefitSimulationResult
import java.time.LocalDateTime
import java.util.*

/**
 * Mapper for converting domain objects to events
 */
object EventMapper {

    /**
     * Converts a BenefitSimulationResult to a BenefitSimulationCompletedEvent
     */
    fun toEvent(result: BenefitSimulationResult): BenefitSimulationCompletedEvent {
        return BenefitSimulationCompletedEvent(
            eventId = UUID.randomUUID().toString(),
            eventType = "BenefitSimulationCompleted",
            timestamp = LocalDateTime.now(),
            participantId = result.participantId,
            participantName = result.participantName,
            currentAge = result.currentAge,
            yearsOfService = result.yearsOfService,
            estimatedLumpSum = result.estimatedLumpSum,
            estimatedMonthlyBenefit = result.estimatedMonthlyBenefit,
            isEligibleForRetirement = result.isEligibleForRetirement,
            calculationDurationMs = result.calculationDurationMs,
            metadata = mapOf(
                "totalContributions" to result.totalContributions,
                "projectedFundValue" to result.projectedFundValue,
                "earlyRetirementPenalty" to result.earlyRetirementPenalty,
                "appliedReturnRate" to result.details.appliedReturnRate,
                "calculationMethod" to result.details.calculationMethod
            )
        )
    }
}
