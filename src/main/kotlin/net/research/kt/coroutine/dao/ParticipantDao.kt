package net.research.kt.coroutine.dao

import net.research.kt.coroutine.domain.ParticipantProfile
import org.jdbi.v3.sqlobject.customizer.Bind
import org.jdbi.v3.sqlobject.statement.SqlQuery
import org.jdbi.v3.sqlobject.kotlin.RegisterKotlinMapper
import java.time.LocalDate

@RegisterKotlinMapper(ParticipantProfile::class)
interface ParticipantDao {

    @SqlQuery("""
        SELECT participant_id as participantId,
               name,
               birth_date as birthDate,
               registration_date as registrationDate,
               employer_name as employerName,
               current_salary as currentSalary
        FROM participants
        WHERE participant_id = :participantId
    """)
    fun findById(@Bind("participantId") participantId: String): ParticipantProfile?

    @SqlQuery("""
        SELECT participant_id as participantId,
               name,
               birth_date as birthDate,
               registration_date as registrationDate,
               employer_name as employerName,
               current_salary as currentSalary
        FROM participants
    """)
    fun findAll(): List<ParticipantProfile>
}
