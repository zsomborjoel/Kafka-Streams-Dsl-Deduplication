package com.example.kafkastreamsdsldeduplication.kafka;

import com.example.kafkastreamsdsldeduplication.config.KafkaStreamConfig;
import com.example.kafkastreamsdsldeduplication.model.TransformationMessages;
import com.example.kafkastreamsdsldeduplication.model.source.SourceData;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.kstream.*;
import org.apache.kafka.streams.state.KeyValueStore;
import org.apache.kafka.streams.state.StoreBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

import static com.example.kafkastreamsdsldeduplication.config.StateStoreConfig.STATE_STORE_NAME;


@Component
@Slf4j
public class DataTransformerKafkaStream {

    @Value("${kafka.topic.source}")
    private String sourceTopic;

    @Value("${kafka.topic.employee}")
    private String employeeTopic;

    @Value("${kafka.topic.dlq}")
    private String dlqTopic;

    @Autowired
    private StoreBuilder<KeyValueStore<String, String>> stateStore;

    @Autowired
    private KafkaStreamConfig kafkaStreamConfig;

    @Autowired
    private DataTransformerSupplier dataTransformerSupplier;

    @Autowired
    private DataMapper dataMapper;

    @SuppressWarnings("resource")
    @PostConstruct
    public void processKafkaMessages() {
        final StreamsBuilder streamsBuilder = defineStreamBuilder();

        final KafkaStreams kafkaStreams = new KafkaStreams(streamsBuilder.build(), kafkaStreamConfig.getConfiguration());
        kafkaStreams.cleanUp();
        kafkaStreams.start();

        Runtime.getRuntime().addShutdownHook(new Thread(kafkaStreams::close));
    }

    private StreamsBuilder defineStreamBuilder() {
        log.info("Build Kafka Streams process");
        StreamsBuilder streamsBuilder = new StreamsBuilder();
        streamsBuilder.addStateStore(stateStore);

        Serde<String> stringSerde = Serdes.String();

        KStream<String, String> stream = streamsBuilder.stream(sourceTopic, Consumed.with(stringSerde, stringSerde));
        @SuppressWarnings("unchecked")
        KStream<String, TransformationMessages> transformedEdiStream = stream.flatTransform(dataTransformerSupplier, STATE_STORE_NAME);

        transformedEdiStream.filter((k, v) -> v.getEmployee() != null)
                .mapValues(dataMapper.getEmployeeMapper())
                .to(employeeTopic, Produced.with(Serdes.String(), Serdes.String()));

        transformedEdiStream.filter((k, v) -> v.getInvalidSource() != null && BooleanUtils.isTrue(v.getInvalidSource().getIsException()))
                .mapValues(dataMapper.getInvalidSourceMapper())
                .to(dlqTopic, Produced.with(Serdes.String(), Serdes.String()));

        return streamsBuilder;
    }

}
