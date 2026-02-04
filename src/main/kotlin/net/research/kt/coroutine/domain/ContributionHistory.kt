package net.research.kt.coroutine.domain

import com.fasterxml.jackson.annotation.JsonProperty
import java.time.LocalDate

data class ContributionHistory(
    @JsonProperty("participantId")
    val participantId: String,

    @JsonProperty("contributions")
    val contributions: List<Contribution>
) {
    fun getTotalContributions(): Double {
        return contributions.sumOf { it.employeeContribution + it.employerContribution }
    }

    fun getMonthsOfContribution(): Int {
        return contributions.size
    }
}

data class Contribution(
    @JsonProperty("month")
    val month: LocalDate,

    @JsonProperty("employeeContribution")
    val employeeContribution: Double,

    @JsonProperty("employerContribution")
    val employerContribution: Double,

    @JsonProperty("salaryBase")
    val salaryBase: Double
) {
    fun getTotalContribution(): Double {
        return employeeContribution + employerContribution
    }
}
