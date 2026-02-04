package net.research.kt.coroutine.domain

import com.fasterxml.jackson.annotation.JsonProperty

data class PensionRules(
    @JsonProperty("normalRetirementAge")
    val normalRetirementAge: Int = 58,

    @JsonProperty("earlyRetirementAge")
    val earlyRetirementAge: Int = 50,

    @JsonProperty("minimumYearsOfService")
    val minimumYearsOfService: Int = 5,

    @JsonProperty("earlyRetirementPenaltyRate")
    val earlyRetirementPenaltyRate: Double = 0.05,

    @JsonProperty("monthlyBenefitDivisor")
    val monthlyBenefitDivisor: Int = 180
) {
    fun isEligibleForRetirement(age: Int, yearsOfService: Int): Boolean {
        return age >= earlyRetirementAge && yearsOfService >= minimumYearsOfService
    }

    fun isEligibleForNormalRetirement(age: Int): Boolean {
        return age >= normalRetirementAge
    }

    fun calculateEarlyRetirementPenalty(age: Int): Double {
        return if (age < normalRetirementAge) {
            val yearsDifference = normalRetirementAge - age
            yearsDifference * earlyRetirementPenaltyRate
        } else {
            0.0
        }
    }
}
