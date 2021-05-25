package com.example.kafkastreamsdsldeduplication.service;

import com.example.kafkastreamsdsldeduplication.model.TransformationMessages;
import com.example.kafkastreamsdsldeduplication.model.source.SourceMetadata;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.state.KeyValueStore;

import java.util.List;

public interface TransformerService {

    List<KeyValue<String, TransformationMessages>> processJson(SourceMetadata metadata, String sourceJson, KeyValueStore<String, String> keyValueStore)
            throws JsonProcessingException;

}
