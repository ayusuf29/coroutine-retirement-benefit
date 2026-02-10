package net.research.kt.coroutine.event

import com.fasterxml.jackson.annotation.JsonProperty
import java.time.LocalDateTime

/**
 * Event published when a benefit simulation is completed.
 * This event is sent to Kafka for downstream processing (analytics, notifications, etc.)
 */
data class BenefitSimulationCompletedEvent(
    @JsonProperty("eventId")
    val eventId: String,

    @JsonProperty("eventType")
    val eventType: String = "BenefitSimulationCompleted",

    @JsonProperty("timestamp")
    val timestamp: LocalDateTime,

    @JsonProperty("participantId")
    val participantId: String,

    @JsonProperty("participantName")
    val participantName: String,

    @JsonProperty("currentAge")
    val currentAge: Int,

    @JsonProperty("yearsOfService")
    val yearsOfService: Int,

    @JsonProperty("estimatedLumpSum")
    val estimatedLumpSum: Double,

    @JsonProperty("estimatedMonthlyBenefit")
    val estimatedMonthlyBenefit: Double,

    @JsonProperty("isEligibleForRetirement")
    val isEligibleForRetirement: Boolean,

    @JsonProperty("calculationDurationMs")
    val calculationDurationMs: Long,

    @JsonProperty("metadata")
    val metadata: Map<String, Any> = emptyMap()
)

/**
 * Event wrapper for Kafka message envelope
 */
data class KafkaEventEnvelope<T>(
    @JsonProperty("eventId")
    val eventId: String,

    @JsonProperty("eventType")
    val eventType: String,

    @JsonProperty("timestamp")
    val timestamp: LocalDateTime,

    @JsonProperty("payload")
    val payload: T,

    @JsonProperty("version")
    val version: String = "1.0"
)
