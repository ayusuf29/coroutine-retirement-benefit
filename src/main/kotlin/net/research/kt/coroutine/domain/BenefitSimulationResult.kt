package net.research.kt.coroutine.domain

import com.fasterxml.jackson.annotation.JsonProperty
import java.time.LocalDateTime

data class BenefitSimulationResult(
    @JsonProperty("participantId")
    val participantId: String,

    @JsonProperty("participantName")
    val participantName: String,

    @JsonProperty("currentAge")
    val currentAge: Int,

    @JsonProperty("yearsOfService")
    val yearsOfService: Int,

    @JsonProperty("totalContributions")
    val totalContributions: Double,

    @JsonProperty("projectedFundValue")
    val projectedFundValue: Double,

    @JsonProperty("estimatedLumpSum")
    val estimatedLumpSum: Double,

    @JsonProperty("estimatedMonthlyBenefit")
    val estimatedMonthlyBenefit: Double,

    @JsonProperty("isEligibleForRetirement")
    val isEligibleForRetirement: Boolean,

    @JsonProperty("earlyRetirementPenalty")
    val earlyRetirementPenalty: Double,

    @JsonProperty("simulationTimestamp")
    val simulationTimestamp: LocalDateTime,

    @JsonProperty("calculationDurationMs")
    val calculationDurationMs: Long,

    @JsonProperty("details")
    val details: SimulationDetails
)

data class SimulationDetails(
    @JsonProperty("appliedReturnRate")
    val appliedReturnRate: Double,

    @JsonProperty("monthsOfContribution")
    val monthsOfContribution: Int,

    @JsonProperty("retirementAge")
    val retirementAge: Int,

    @JsonProperty("minimumYearsOfService")
    val minimumYearsOfService: Int,

    @JsonProperty("calculationMethod")
    val calculationMethod: String
)
