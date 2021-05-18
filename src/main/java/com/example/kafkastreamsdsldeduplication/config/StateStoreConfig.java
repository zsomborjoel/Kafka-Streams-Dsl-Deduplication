package com.example.kafkastreamsdsldeduplication.config;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

@Configuration
public class StateStoreConfig {

    private static final Logger LOGGER = LoggerFactory.getLogger(StateStoreConfig.class);

    public static final String STATE_STORE_NAME = "deduplicateData";

    public static final String STATE_STORE_EDI_CLEANUP_POLICY = "compact";

    @Value("${kafka.retention.ms:7776000000}")
    private String retentionMs;

    @Value("${kafka.delete.retention.ms:600000}")
    private String deleteRetentionMs;

    @Bean
    public StoreBuilder<KeyValueStore<String, String>> getStateStore() {
        LOGGER.info("Initialize state store for incoming data");
        LOGGER.info("delete.retention.ms: {}", deleteRetentionMs);

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
