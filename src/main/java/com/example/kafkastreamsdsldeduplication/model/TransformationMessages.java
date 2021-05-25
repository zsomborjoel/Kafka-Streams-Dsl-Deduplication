package com.example.kafkastreamsdsldeduplication.model;

import com.example.kafkastreamsdsldeduplication.model.source.InvalidSource;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TransformationMessages {

    private Employee employee;

    private InvalidSource invalidSource;

    public TransformationMessages(InvalidSource invalidSource) {
        this.invalidSource = invalidSource;
    }

}
