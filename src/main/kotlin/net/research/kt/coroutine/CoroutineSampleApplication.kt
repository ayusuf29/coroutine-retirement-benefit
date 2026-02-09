package net.research.kt.coroutine

import com.fasterxml.jackson.module.kotlin.KotlinModule
import io.dropwizard.core.Application
import io.dropwizard.core.setup.Bootstrap
import io.dropwizard.core.setup.Environment
import net.research.kt.coroutine.api.BenefitSimulationResource
import net.research.kt.coroutine.api.HealthCheckResource
import net.research.kt.coroutine.config.CoroutineSampleConfiguration
import net.research.kt.coroutine.health.CoroutineHealthCheck
import net.research.kt.coroutine.repository.*
import net.research.kt.coroutine.service.BenefitSimulationService
import org.jdbi.v3.core.Jdbi
import org.slf4j.LoggerFactory

/**
 * Main DropWizard Application for Pension Fund Benefit Simulation.
 *
 * This application demonstrates:
 * - Kotlin + DropWizard integration
 * - Coroutines for async/concurrent operations
 * - Best practices for REST API design
 * - Proper dependency injection pattern
 */
class CoroutineSampleApplication : Application<CoroutineSampleConfiguration>() {

    private val logger = LoggerFactory.getLogger(CoroutineSampleApplication::class.java)

    override fun getName(): String = "Coroutine Sample"

    override fun initialize(bootstrap: Bootstrap<CoroutineSampleConfiguration>) {
        // Register Jackson Kotlin Module for proper Kotlin serialization
        bootstrap.objectMapper.registerModule(KotlinModule.Builder().build())
        logger.info("Registered Jackson Kotlin Module")
    }

    override fun run(
        configuration: CoroutineSampleConfiguration,
        environment: Environment
    ) {
        logger.info("Starting Coroutine Sample Application")

        // Initialize Jdbi
        val jdbi = Jdbi.create(configuration.getDataSourceFactory().build(environment.metrics(), "mysql"))
        logger.info("Initialized Jdbi with MySQL datasource")

        // Initialize repositories
        val participantRepository = ParticipantRepository(jdbi)
        val contributionRepository = ContributionRepository(jdbi)
        val fundReturnRateRepository = FundReturnRateRepository(jdbi)
        val pensionRulesRepository = PensionRulesRepository(jdbi)

        // Initialize services
        val simulationService = BenefitSimulationService(
            participantRepository = participantRepository,
            contributionRepository = contributionRepository,
            fundReturnRateRepository = fundReturnRateRepository,
            pensionRulesRepository = pensionRulesRepository,
            timeoutMillis = configuration.getCoroutineConfig().timeoutMillis
        )

        // Register resources
        environment.jersey().register(BenefitSimulationResource(simulationService))
        environment.jersey().register(HealthCheckResource())
        logger.info("Registered REST resources")

        // Register health checks
        environment.healthChecks().register("coroutine", CoroutineHealthCheck())
        logger.info("Registered health checks")

        logger.info("Coroutine Sample Application started successfully")
    }
}

/**
 * Main entry point for the application.
 */
fun main(args: Array<String>) {
    CoroutineSampleApplication().run(*args)
}
