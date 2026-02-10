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

    @JsonProperty("kafka")
    private val kafkaConfig: KafkaConfiguration = KafkaConfiguration()

    fun getDataSourceFactory(): DataSourceFactory = dataSourceFactory

    fun getCoroutineConfig(): CoroutineConfiguration = coroutineConfig

    fun getKafkaConfig(): KafkaConfiguration = kafkaConfig
}

data class CoroutineConfiguration(
    @JsonProperty("parallelism")
    val parallelism: Int = Runtime.getRuntime().availableProcessors(),

    @JsonProperty("timeout")
    val timeoutMillis: Long = 30000,

    @JsonProperty("enableLogging")
    val enableLogging: Boolean = true
)

data class KafkaConfiguration(
    @JsonProperty("bootstrapServers")
    val bootstrapServers: String = "localhost:9092",

    @JsonProperty("topic")
    val topic: String = "pension-simulation-events",

    @JsonProperty("clientId")
    val clientId: String = "pension-simulation-producer",

    @JsonProperty("retries")
    val retries: Int = 3,

    @JsonProperty("acks")
    val acks: String = "all",

    @JsonProperty("enableIdempotence")
    val enableIdempotence: Boolean = true,

    @JsonProperty("compressionType")
    val compressionType: String = "snappy"
)
