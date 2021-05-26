package com.example.kafkastreamsdsldeduplication.service;

import org.apache.kafka.streams.state.KeyValueStore;

public interface DeduplicationService<T> {

    public boolean filterByStateStore(T object, KeyValueStore<String, String> keyValueStore);

}
