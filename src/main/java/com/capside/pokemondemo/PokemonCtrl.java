/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.capside.pokemondemo;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Map;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 *
 * @author ciber
 */
@Controller
@Scope(scopeName = "singleton")
public class PokemonCtrl {

    private final ObjectMapper mapper;
    private final PokemonRepository repository;
    private final Pokemon pokemon;

    @Autowired
    public PokemonCtrl(ObjectMapper mapper, PokemonRepository repository) {
        this.mapper = mapper;
        this.repository = repository;
        this.pokemon = repository.getRandomPokemon();
    }

    @RequestMapping("/")
    String index(Map<String, Object> model) {
        String hostname = System.getenv("HOSTNAME") == null ? "" : System.getenv("HOSTNAME");
        model.put("container", hostname);
        model.put("pokemon", pokemon);

        return "index";
    }


}
