package com.example.kafkastreamsdsldeduplication.model.source;

import lombok.Data;

@Data
public class InvalidSource {

    private SourceMetadata metadata;
    private String error;
    private String body;
    private Boolean isException;

}
