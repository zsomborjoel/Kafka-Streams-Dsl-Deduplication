package com.example.kafkastreamsdsldeduplication.model;

import lombok.Data;

@Data
public class Employee {

    private String name;
    private Long age;
    private String email;
    private Boolean valid;
    
}
