package net.research.kt.coroutine.dao

import net.research.kt.coroutine.domain.PensionRules
import org.jdbi.v3.sqlobject.statement.SqlQuery
import org.jdbi.v3.sqlobject.kotlin.RegisterKotlinMapper

@RegisterKotlinMapper(PensionRules::class)
interface PensionRulesDao {

    @SqlQuery("""
        SELECT normal_retirement_age as normalRetirementAge,
               early_retirement_age as earlyRetirementAge,
               minimum_years_of_service as minimumYearsOfService,
               early_retirement_penalty_rate as earlyRetirementPenaltyRate,
               monthly_benefit_divisor as monthlyBenefitDivisor
        FROM pension_rules
        WHERE id = 1
    """)
    fun getCurrentRules(): PensionRules?
}
