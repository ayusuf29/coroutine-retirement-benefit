package net.research.kt.coroutine.dao

import net.research.kt.coroutine.domain.FundReturnRate
import org.jdbi.v3.sqlobject.statement.SqlQuery
import org.jdbi.v3.sqlobject.kotlin.RegisterKotlinMapper

@RegisterKotlinMapper(FundReturnRate::class)
interface FundReturnRateDao {

    @SqlQuery("""
        SELECT year, return_rate as returnRate
        FROM fund_return_rates
        ORDER BY year ASC
    """)
    fun findAll(): List<FundReturnRate>

    @SqlQuery("""
        SELECT year, return_rate as returnRate
        FROM fund_return_rates
        ORDER BY year DESC
        LIMIT :limit
    """)
    fun findRecent(limit: Int): List<FundReturnRate>
}
