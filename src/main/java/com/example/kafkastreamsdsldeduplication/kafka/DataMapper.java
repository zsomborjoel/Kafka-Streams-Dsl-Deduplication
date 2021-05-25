package com.example.kafkastreamsdsldeduplication.kafka;

import com.example.kafkastreamsdsldeduplication.model.TransformationMessages;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.streams.kstream.ValueMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class DataMapper {

    @Autowired
    private ObjectMapper objectMapper;

    public ValueMapper<TransformationMessages, String> getInvalidSourceMapper() {
        return transformationMessages -> {
            try {
                return returnAsString(transformationMessages.getInvalidSource());
            } catch (JsonProcessingException e) {
                log.error("Invalid object: ", e);
            }
            return null;
        };
    }

    public ValueMapper<TransformationMessages, String> getEmployeeMapper() {
        return transformationMessages -> {
            try {
                return returnAsString(transformationMessages.getEmployee());
            } catch (JsonProcessingException e) {
                log.error("Invalid object: ", e);
            }
            return null;
        };
    }

    private String returnAsString(Object object) throws JsonProcessingException {
        return objectMapper.writeValueAsString(object);
    }

}
