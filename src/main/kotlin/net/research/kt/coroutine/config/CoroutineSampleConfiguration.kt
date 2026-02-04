package net.research.kt.coroutine.config

import com.fasterxml.jackson.annotation.JsonProperty
import io.dropwizard.core.Configuration
import io.dropwizard.db.DataSourceFactory
import jakarta.validation.Valid
import jakarta.validation.constraints.NotNull

class CoroutineSampleConfiguration : Configuration() {

    @Valid
    @NotNull
    @JsonProperty("database")
    private val dataSourceFactory: DataSourceFactory = DataSourceFactory()

    @JsonProperty("coroutines")
    private val coroutineConfig: CoroutineConfiguration = CoroutineConfiguration()

    fun getDataSourceFactory(): DataSourceFactory = dataSourceFactory

    fun getCoroutineConfig(): CoroutineConfiguration = coroutineConfig
}

data class CoroutineConfiguration(
    @JsonProperty("parallelism")
    val parallelism: Int = Runtime.getRuntime().availableProcessors(),

    @JsonProperty("timeout")
    val timeoutMillis: Long = 30000,

    @JsonProperty("enableLogging")
    val enableLogging: Boolean = true
)
