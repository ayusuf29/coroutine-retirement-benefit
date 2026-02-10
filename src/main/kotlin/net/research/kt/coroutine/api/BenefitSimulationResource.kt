package net.research.kt.coroutine.api

import com.codahale.metrics.annotation.Timed
import jakarta.validation.constraints.NotEmpty
import jakarta.ws.rs.*
import jakarta.ws.rs.core.MediaType
import jakarta.ws.rs.core.Response
import kotlinx.coroutines.async
import kotlinx.coroutines.runBlocking
import net.research.kt.coroutine.domain.BenefitSimulationResult
import net.research.kt.coroutine.event.EventMapper
import net.research.kt.coroutine.exception.ParticipantNotFoundException
import net.research.kt.coroutine.kafka.EventPublisher
import net.research.kt.coroutine.service.BenefitSimulationService
import org.slf4j.LoggerFactory

/**
 * REST API Resource for pension benefit simulations.
 *
 * Endpoints:
 * - GET /api/simulations/{participantId} - Simulate benefit for single participant (synchronous)
 * - POST /api/simulations/async/{participantId} - Simulate and publish to Kafka (async with events)
 * - POST /api/simulations/batch - Simulate benefits for multiple participants
 */
@Path("/api/simulations")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
class BenefitSimulationResource(
    private val simulationService: BenefitSimulationService,
    private val eventPublisher: EventPublisher?  // Optional for graceful degradation if Kafka is not configured
) {

    private val logger = LoggerFactory.getLogger(BenefitSimulationResource::class.java)

    @GET
    @Path("/{participantId}")
    @Timed
    fun simulateBenefit(
        @PathParam("participantId") @NotEmpty participantId: String
    ): Response = runBlocking {
        logger.info("Received simulation request for participant: $participantId")

        return@runBlocking try {
            val result = simulationService.simulateBenefit(participantId)
            Response.ok(result).build()
        } catch (e: ParticipantNotFoundException) {
            logger.warn("Participant not found: $participantId")
            Response.status(Response.Status.NOT_FOUND)
                .entity(mapOf("error" to e.message))
                .build()
        } catch (e: Exception) {
            logger.error("Error simulating benefit for participant: $participantId", e)
            Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity(mapOf("error" to "Internal server error"))
                .build()
        }
    }

    /**
     * NEW: Async endpoint that simulates benefit AND publishes event to Kafka.
     * 
     * This demonstrates event-driven architecture with Kotlin coroutines:
     * 1. Performs simulation (IO-heavy)
     * 2. Publishes event to Kafka (IO-heavy) 
     * 3. Both operations run in parallel using async/await
     * 4. Returns immediately after publishing event
     */
    @POST
    @Path("/async/{participantId}")
    @Timed
    fun simulateBenefitAsync(
        @PathParam("participantId") @NotEmpty participantId: String
    ): Response = runBlocking {
        logger.info("Received ASYNC simulation request for participant: $participantId")

        if (eventPublisher == null) {
            logger.warn("Event publisher not configured, falling back to synchronous simulation")
            return@runBlocking simulateBenefit(participantId)
        }

        return@runBlocking try {
            // Perform simulation
            val result = simulationService.simulateBenefit(participantId)
            
            // Convert to event
            val event = EventMapper.toEvent(result)
            
            // Publish to Kafka asynchronously
            val publishJob = async {
                eventPublisher.publishSimulationCompleted(event)
            }
            
            // Wait for Kafka publish to complete
            publishJob.await()
            
            logger.info("Successfully published event for participant: $participantId")
            
            // Return response with event metadata
            Response.accepted(
                mapOf(
                    "message" to "Simulation completed and event published",
                    "eventId" to event.eventId,
                    "participantId" to participantId,
                    "result" to result
                )
            ).build()
            
        } catch (e: ParticipantNotFoundException) {
            logger.warn("Participant not found: $participantId")
            Response.status(Response.Status.NOT_FOUND)
                .entity(mapOf("error" to e.message))
                .build()
        } catch (e: Exception) {
            logger.error("Error in async simulation for participant: $participantId", e)
            Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity(mapOf("error" to "Internal server error: ${e.message}"))
                .build()
        }
    }

    @POST
    @Path("/batch")
    @Timed
    fun simulateBenefitBatch(
        request: BatchSimulationRequest
    ): Response = runBlocking {
        logger.info("Received batch simulation request for ${request.participantIds.size} participants")

        return@runBlocking try {
            val results = simulationService.simulateBenefitBatch(request.participantIds)
            Response.ok(BatchSimulationResponse(results)).build()
        } catch (e: Exception) {
            logger.error("Error simulating batch benefits", e)
            Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity(mapOf("error" to "Internal server error"))
                .build()
        }
    }

    data class BatchSimulationRequest(
        val participantIds: List<String>
    )

    data class BatchSimulationResponse(
        val results: List<BenefitSimulationResult>
    )
}
