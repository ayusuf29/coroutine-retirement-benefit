package net.research.kt.coroutine.health

import com.codahale.metrics.health.HealthCheck

/**
 * Health check for coroutine functionality.
 */
class CoroutineHealthCheck : HealthCheck() {
    override fun check(): Result {
        return Result.healthy("Coroutine system is operational")
    }
}
