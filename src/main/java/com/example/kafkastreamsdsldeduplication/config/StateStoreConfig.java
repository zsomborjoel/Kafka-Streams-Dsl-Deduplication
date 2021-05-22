package com.example.kafkastreamsdsldeduplication.config;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.state.KeyValueBytesStoreSupplier;
import org.apache.kafka.streams.state.KeyValueStore;
import org.apache.kafka.streams.state.StoreBuilder;
import org.apache.kafka.streams.state.Stores;
import org.apache.kafka.streams.state.internals.RocksDbKeyValueBytesStoreSupplier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

@Configuration
@Slf4j
public class StateStoreConfig {

    public static final String STATE_STORE_NAME = "deduplicateData";

    public static final String STATE_STORE_CLEANUP_POLICY = "compact";

    @Value("${kafka.retention.ms:7776000000}")
    private String retentionMs;

    @Value("${kafka.delete.retention.ms:600000}")
    private String deleteRetentionMs;

    @Bean
    public StoreBuilder<KeyValueStore<String, String>> getStateStore() {
        log.info("Initialize state store for incoming data");
        log.info("delete.retention.ms: {}", deleteRetentionMs);

        KeyValueBytesStoreSupplier keyValueBytesStoreSupplier = new RocksDbKeyValueBytesStoreSupplier(STATE_STORE_NAME, false);

        Map<String, String> changelogConfig = new HashMap<String, String>();
        changelogConfig.put("cleanup.policy", STATE_STORE_CLEANUP_POLICY);
        changelogConfig.put("delete.retention.ms", deleteRetentionMs);
        changelogConfig.put("min.insync.replicas", "1");

        return Stores
                .keyValueStoreBuilder(keyValueBytesStoreSupplier, Serdes.String(), Serdes.String())
                .withLoggingEnabled(changelogConfig)
                .withCachingDisabled();
    }

}
