package net.research.kt.coroutine.repository

import kotlinx.coroutines.delay
import net.research.kt.coroutine.domain.ParticipantProfile
import org.slf4j.LoggerFactory
import java.time.LocalDate

/**
 * Repository for fetching participant profile information.
 * Simulates database/external service calls with delay.
 */
class ParticipantRepository {

    private val logger = LoggerFactory.getLogger(ParticipantRepository::class.java)

    // In-memory data for demonstration
    private val participants = mapOf(
        "P001" to ParticipantProfile(
            participantId = "P001",
            name = "Budi Santoso",
            birthDate = LocalDate.of(1980, 5, 15),
            registrationDate = LocalDate.of(2005, 3, 1),
            employerName = "PT Maju Jaya",
            currentSalary = 15_000_000.0
        ),
        "P002" to ParticipantProfile(
            participantId = "P002",
            name = "Siti Nurhaliza",
            birthDate = LocalDate.of(1985, 8, 22),
            registrationDate = LocalDate.of(2010, 6, 15),
            employerName = "PT Sejahtera Mandiri",
            currentSalary = 12_000_000.0
        ),
        "P003" to ParticipantProfile(
            participantId = "P003",
            name = "Ahmad Dhani",
            birthDate = LocalDate.of(1975, 3, 10),
            registrationDate = LocalDate.of(2000, 1, 5),
            employerName = "PT Karya Abadi",
            currentSalary = 20_000_000.0
        )
    )

    suspend fun findById(participantId: String): ParticipantProfile? {
        logger.info("Fetching participant profile for: $participantId")
        // Simulate database call delay
        delay(150)
        return participants[participantId]
    }

    suspend fun findAll(): List<ParticipantProfile> {
        logger.info("Fetching all participants")
        delay(200)
        return participants.values.toList()
    }
}
