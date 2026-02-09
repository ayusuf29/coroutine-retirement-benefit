package net.research.kt.coroutine.dao

import net.research.kt.coroutine.domain.Contribution
import org.jdbi.v3.sqlobject.customizer.Bind
import org.jdbi.v3.sqlobject.statement.SqlQuery
import org.jdbi.v3.sqlobject.kotlin.RegisterKotlinMapper

@RegisterKotlinMapper(Contribution::class)
interface ContributionDao {

    @SqlQuery("""
        SELECT month,
               employee_contribution as employeeContribution,
               employer_contribution as employerContribution,
               salary_base as salaryBase
        FROM contributions
        WHERE participant_id = :participantId
        ORDER BY month ASC
    """)
    fun findByParticipantId(@Bind("participantId") participantId: String): List<Contribution>

    @SqlQuery("""
        SELECT COUNT(*) as count
        FROM contributions
        WHERE participant_id = :participantId
    """)
    fun countByParticipantId(@Bind("participantId") participantId: String): Int
}
