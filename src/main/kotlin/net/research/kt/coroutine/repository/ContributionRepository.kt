package net.research.kt.coroutine.repository

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import net.research.kt.coroutine.dao.ContributionDao
import net.research.kt.coroutine.domain.ContributionHistory
import org.jdbi.v3.core.Jdbi
import org.slf4j.LoggerFactory

/**
 * Repository for fetching contribution history from MySQL database.
 */
class ContributionRepository(private val jdbi: Jdbi) {

    private val logger = LoggerFactory.getLogger(ContributionRepository::class.java)

    suspend fun findByParticipantId(participantId: String): ContributionHistory? = withContext(Dispatchers.IO) {
        logger.info("Fetching contribution history for: $participantId")
        // Simulate network/processing delay - contributions are typically large datasets
        delay(300)

        jdbi.withExtension<ContributionHistory?, ContributionDao, Exception>(ContributionDao::class.java) { dao ->
            val contributions = dao.findByParticipantId(participantId)
            if (contributions.isEmpty()) {
                null
            } else {
                ContributionHistory(
                    participantId = participantId,
                    contributions = contributions
                )
            }
        }
    }
}
