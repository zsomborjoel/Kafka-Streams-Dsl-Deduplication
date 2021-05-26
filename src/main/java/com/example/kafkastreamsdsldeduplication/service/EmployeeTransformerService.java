package com.example.kafkastreamsdsldeduplication.service;

import com.example.kafkastreamsdsldeduplication.model.Employee;
import com.example.kafkastreamsdsldeduplication.model.TransformationMessages;
import com.example.kafkastreamsdsldeduplication.model.source.SourceMetadata;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.state.KeyValueStore;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;

import static com.example.kafkastreamsdsldeduplication.constants.DeduplicationConstants.TRUE;


public class EmployeeTransformerService implements TransformerService, DeduplicationService<Employee> {

    @Autowired
    private ObjectMapper objectMapper;

    @Override
    public List<KeyValue<String, TransformationMessages>> processJson(SourceMetadata metadata, String sourceJson, KeyValueStore<String, String> keyValueStore) throws JsonProcessingException {

        List<KeyValue<String, TransformationMessages>> keyValueList = new ArrayList<>();

        if (sourceJson != null) {
            Employee[] employees = objectMapper.readValue(sourceJson, Employee[].class);
            for (Employee employee : employees) {
                if (isValidUser(employee.getEmail()) && filterByStateStore(employee, keyValueStore)) {
                    TransformationMessages transformationMessages = new TransformationMessages();
                    transformationMessages.setEmployee(employee);
                    keyValueList.add(new KeyValue<>(employee.getName(), transformationMessages));
                }
            }

        }

        return keyValueList;
    }

    private boolean isValidUser(String email) {
        return email != null && !"".equals(email.trim());
    }

    @Override
    public boolean filterByStateStore(Employee object, KeyValueStore<String, String> keyValueStore) {
        String storeKey = String.format("%s|%s", object.getName(), object.getAge());
        return TRUE.equals(keyValueStore.get(storeKey));
    }

}
