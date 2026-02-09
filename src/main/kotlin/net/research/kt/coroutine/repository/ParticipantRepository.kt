package net.research.kt.coroutine.repository

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import net.research.kt.coroutine.dao.ParticipantDao
import net.research.kt.coroutine.domain.ParticipantProfile
import org.jdbi.v3.core.Jdbi
import org.slf4j.LoggerFactory

/**
 * Repository for fetching participant profile information from MySQL database.
 */
class ParticipantRepository(private val jdbi: Jdbi) {

    private val logger = LoggerFactory.getLogger(ParticipantRepository::class.java)

    suspend fun findById(participantId: String): ParticipantProfile? = withContext(Dispatchers.IO) {
        logger.info("Fetching participant profile for: $participantId")
        // Simulate network/processing delay
        delay(150)
        jdbi.withExtension<ParticipantProfile?, ParticipantDao, Exception>(ParticipantDao::class.java) { dao ->
            dao.findById(participantId)
        }
    }

    suspend fun findAll(): List<ParticipantProfile> = withContext(Dispatchers.IO) {
        logger.info("Fetching all participants")
        delay(200)
        jdbi.withExtension<List<ParticipantProfile>, ParticipantDao, Exception>(ParticipantDao::class.java) { dao ->
            dao.findAll()
        }
    }
}
