package com.example.kafkastreamsdsldeduplication.kafka;

import com.example.kafkastreamsdsldeduplication.model.TransformationMessages;
import com.example.kafkastreamsdsldeduplication.model.source.SourceData;
import com.example.kafkastreamsdsldeduplication.service.TransformerService;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.kstream.Transformer;
import org.apache.kafka.streams.kstream.TransformerSupplier;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class DataTransformerSupplier implements TransformerSupplier<String, SourceData, Iterable<KeyValue<String, TransformationMessages>>> {

    @Autowired
    private TransformerService transformerService;

    @Override
    public Transformer<String, SourceData, Iterable<KeyValue<String, TransformationMessages>>> get() {
        return new DataTransformer(transformerService);
    }

}
