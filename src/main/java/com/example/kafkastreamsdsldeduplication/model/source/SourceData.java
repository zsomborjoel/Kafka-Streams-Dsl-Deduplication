package com.example.kafkastreamsdsldeduplication.model.source;

import lombok.Data;

@Data
public class SourceData {

    private SourceMetadata sourceMetadata;
    private String body;

}
