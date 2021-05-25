package com.example.kafkastreamsdsldeduplication.kafka;

import com.example.kafkastreamsdsldeduplication.model.TransformationMessages;
import com.example.kafkastreamsdsldeduplication.model.source.InvalidSource;
import com.example.kafkastreamsdsldeduplication.model.source.SourceData;
import com.example.kafkastreamsdsldeduplication.service.TransformerService;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.kstream.Transformer;
import org.apache.kafka.streams.processor.ProcessorContext;
import org.apache.kafka.streams.state.KeyValueStore;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

import static com.example.kafkastreamsdsldeduplication.config.StateStoreConfig.STATE_STORE_NAME;


@Component
@Slf4j
public class DataTransformer implements Transformer<String, SourceData, Iterable<KeyValue<String, TransformationMessages>>>  {

    private final TransformerService transformerService;

    private KeyValueStore<String, String> ediTransformationMessagesStateStore;

    public DataTransformer(TransformerService transformerService) {
        this.transformerService = transformerService;
    }

    @Override
    public void init(ProcessorContext context) {
        @SuppressWarnings("unchecked")
        KeyValueStore<String, String> cast = KeyValueStore.class.cast(context.getStateStore(STATE_STORE_NAME));
        ediTransformationMessagesStateStore = cast;
    }

    @Override
    public Iterable<KeyValue<String, TransformationMessages>> transform(String s, SourceData sourceData) {
        List<KeyValue<String, TransformationMessages>> kafkaMessages = new ArrayList<>();

        try {
            kafkaMessages = transformerService.processJson(sourceData.getSourceMetadata(), sourceData.getBody(), ediTransformationMessagesStateStore);
        } catch (Exception e) {
            log.warn("Source could not be processed, report file into error topic!");
            log.warn(e.getMessage());
            InvalidSource invalidSource = new InvalidSource();
            invalidSource.setMetadata(sourceData.getSourceMetadata());
            invalidSource.setError(e.getMessage());
            invalidSource.setBody(sourceData.getBody());
            invalidSource.setIsException(true);

            kafkaMessages.add(new KeyValue<>(null, new TransformationMessages(invalidSource)));
        }

        return kafkaMessages;
    }

    @Override
    public void close() {

    }

}
