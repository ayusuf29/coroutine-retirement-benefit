package net.research.kt.coroutine.repository

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import net.research.kt.coroutine.dao.PensionRulesDao
import net.research.kt.coroutine.domain.PensionRules
import org.jdbi.v3.core.Jdbi
import org.slf4j.LoggerFactory

/**
 * Repository for fetching pension rules from MySQL database.
 */
class PensionRulesRepository(private val jdbi: Jdbi) {

    private val logger = LoggerFactory.getLogger(PensionRulesRepository::class.java)

    suspend fun getCurrentRules(): PensionRules = withContext(Dispatchers.IO) {
        logger.info("Fetching current pension rules")
        // Simulate configuration service delay
        delay(80)

        jdbi.withExtension<PensionRules, PensionRulesDao, Exception>(PensionRulesDao::class.java) { dao ->
            dao.getCurrentRules() ?: PensionRules()
        }
    }
}
