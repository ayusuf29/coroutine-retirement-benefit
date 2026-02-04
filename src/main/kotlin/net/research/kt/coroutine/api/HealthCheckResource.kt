package net.research.kt.coroutine.api

import com.codahale.metrics.annotation.Timed
import jakarta.ws.rs.GET
import jakarta.ws.rs.Path
import jakarta.ws.rs.Produces
import jakarta.ws.rs.core.MediaType
import java.time.LocalDateTime

/**
 * Simple health check endpoint.
 */
@Path("/api/health")
@Produces(MediaType.APPLICATION_JSON)
class HealthCheckResource {

    @GET
    @Timed
    fun health(): HealthResponse {
        return HealthResponse(
            status = "UP",
            timestamp = LocalDateTime.now().toString(),
            application = "Coroutine Sample"
        )
    }

    data class HealthResponse(
        val status: String,
        val timestamp: String,
        val application: String
    )
}
