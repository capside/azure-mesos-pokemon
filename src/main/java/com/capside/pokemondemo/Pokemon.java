package com.capside.pokemondemo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 *
 * @author ciber
 */
@Data @NoArgsConstructor @AllArgsConstructor 
@JsonIgnoreProperties
public class Pokemon {
    private int id;
    private String name;
    private String url;
}
