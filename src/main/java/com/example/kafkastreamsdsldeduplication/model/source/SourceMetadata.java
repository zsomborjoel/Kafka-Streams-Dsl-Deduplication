package com.example.kafkastreamsdsldeduplication.model.source;

import lombok.Data;

@Data
public class SourceMetadata {

    private java.lang.String filename;
    private java.lang.String feedSource;
    private java.lang.String feedType;
    private java.lang.Long timestamp;

}
