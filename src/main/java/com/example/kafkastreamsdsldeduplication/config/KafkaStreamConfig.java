package com.example.kafkastreamsdsldeduplication.config;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.CommonClientConfigs;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.config.SaslConfigs;
import org.apache.kafka.common.config.SslConfigs;
import org.apache.kafka.streams.StreamsConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

@Configuration
@Slf4j
public class KafkaStreamConfig {

    @Value("${kafka.bootstrap.server}")
    private String kafkaBootstrapServer;

    @Value("${kafka.auth.type:none}")
    private String kafkaAuthenticationType;

    // SSL config variables
    @Value("${kafka.auth.ssl.truststoreLocation:}")
    private String kafkaTruststoreLocation;

    @Value("${kafka.auth.ssl.truststorePassword:}")
    private String kafkaTruststorePassword;

    // SASL config variables
    @Value("${kafka.auth.sasl.jaas.config:}")
    private String kafkaSASLJaasConfig;

    @Value("${kafka.auth.sasl.mechanism:}")
    private String saslMechanism;

    @Value("${kafka.auth.sasl.ssl.endpoint.identification.algorithm:}")
    private String sslAlgorithm;

    @Value("${kafka.auth.sasl.security.protocol:}")
    private String securityProtocol;

    // Other configs
    @Value("${kafka.clientId}")
    private String kafkaClientId;

    @Value("${kafka.statestore.replicationFactor}")
    private String kafkaReplicationFactor;

    @Value("${kafka.statestore.location}")
    private String kafkaStateStoreLocation;

    public static final String SSL = "SSL";
    public static final String SASL = "SASL";

    public Properties getConfiguration() {

        Properties config = new Properties();

        config.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaBootstrapServer);

        config.put(StreamsConfig.APPLICATION_ID_CONFIG, kafkaClientId);
        config.put(ProducerConfig.CLIENT_ID_CONFIG, kafkaClientId);
        config.put(ConsumerConfig.GROUP_ID_CONFIG, kafkaClientId);

        config.put(StreamsConfig.producerPrefix(ProducerConfig.ACKS_CONFIG), "all");
        config.put(StreamsConfig.REPLICATION_FACTOR_CONFIG, kafkaReplicationFactor);

        log.info("Authentication type for Kafka connection: {}", kafkaAuthenticationType);
        if (kafkaAuthenticationType.equalsIgnoreCase(SSL)) {
            config.put(StreamsConfig.SECURITY_PROTOCOL_CONFIG, SSL);
            config.put(SslConfigs.SSL_TRUSTSTORE_LOCATION_CONFIG, kafkaTruststoreLocation);

            if (!"".equals(kafkaTruststorePassword)) {
                config.put(SslConfigs.SSL_TRUSTSTORE_PASSWORD_CONFIG, kafkaTruststorePassword);
            }
        } else if (kafkaAuthenticationType.equalsIgnoreCase(SASL)) {
            config.put(SaslConfigs.SASL_JAAS_CONFIG, kafkaSASLJaasConfig);
            config.put(SaslConfigs.SASL_MECHANISM, saslMechanism);
            config.put(SslConfigs.SSL_ENDPOINT_IDENTIFICATION_ALGORITHM_CONFIG, sslAlgorithm);
            config.put(CommonClientConfigs.SECURITY_PROTOCOL_CONFIG, securityProtocol);
        }

        config.put(StreamsConfig.STATE_DIR_CONFIG, kafkaStateStoreLocation);
        config.put(StreamsConfig.PROCESSING_GUARANTEE_CONFIG, StreamsConfig.EXACTLY_ONCE);
        config.put(ProducerConfig.MAX_REQUEST_SIZE_CONFIG, 20971520);

        return config;
    }



}
