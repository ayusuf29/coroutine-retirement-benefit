package net.research.kt.coroutine.kafka

import com.fasterxml.jackson.databind.ObjectMapper
import net.research.kt.coroutine.config.KafkaConfiguration
import org.apache.kafka.clients.producer.KafkaProducer
import org.apache.kafka.clients.producer.ProducerConfig
import org.apache.kafka.common.serialization.StringSerializer
import org.slf4j.LoggerFactory
import java.util.*

/**
 * Factory for creating Kafka producers with coroutine-friendly configuration.
 */
class KafkaProducerFactory(
    private val kafkaConfig: KafkaConfiguration,
    private val objectMapper: ObjectMapper
) {
    
    private val logger = LoggerFactory.getLogger(KafkaProducerFactory::class.java)

    fun createProducer(): KafkaProducer<String, String> {
        val props = Properties().apply {
            // Connection settings
            put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaConfig.bootstrapServers)
            put(ProducerConfig.CLIENT_ID_CONFIG, kafkaConfig.clientId)

            // Serialization
            put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer::class.java.name)
            put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer::class.java.name)

            // Reliability settings
            put(ProducerConfig.ACKS_CONFIG, kafkaConfig.acks)
            put(ProducerConfig.RETRIES_CONFIG, kafkaConfig.retries)
            put(ProducerConfig.ENABLE_IDEMPOTENCE_CONFIG, kafkaConfig.enableIdempotence)

            // Performance settings
            put(ProducerConfig.COMPRESSION_TYPE_CONFIG, kafkaConfig.compressionType)
            put(ProducerConfig.LINGER_MS_CONFIG, 10)  // Small delay for batching
            put(ProducerConfig.BATCH_SIZE_CONFIG, 16384)  // 16KB batch size

            // Timeout settings
            put(ProducerConfig.MAX_BLOCK_MS_CONFIG, 60000)  // 60 seconds
            put(ProducerConfig.REQUEST_TIMEOUT_MS_CONFIG, 30000)  // 30 seconds
        }

        logger.info("Creating Kafka producer with bootstrap servers: ${kafkaConfig.bootstrapServers}")
        return KafkaProducer(props)
    }
}
