package net.research.kt.coroutine.domain

import com.fasterxml.jackson.annotation.JsonProperty
import java.time.LocalDate

data class FundReturnRate(
    @JsonProperty("year")
    val year: Int,

    @JsonProperty("returnRate")
    val returnRate: Double
)

data class CurrentFundReturnRate(
    @JsonProperty("currentYear")
    val currentYear: Int,

    @JsonProperty("currentRate")
    val currentRate: Double,

    @JsonProperty("averageRate5Years")
    val averageRate5Years: Double,

    @JsonProperty("averageRate10Years")
    val averageRate10Years: Double,

    @JsonProperty("historicalRates")
    val historicalRates: List<FundReturnRate>
)
