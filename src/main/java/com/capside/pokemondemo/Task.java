package com.capside.pokemondemo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

/**
 *
 * @author ciber
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class Task {
 
    private String id;
    private String slaveId;
    private String host;
    private Pokemon pokemon;
    
}
