package com.capside.pokemondemo;


import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.PostConstruct;
import lombok.SneakyThrows;
import org.springframework.stereotype.Repository;

/**
 *
 * @author ciber
 */
@Repository
public class PokemonRepository {
    
    private final List<Pokemon> pokemons = new ArrayList<>();
    
    public Pokemon getRandomPokemon() {
        int pokIdx = (int) (Math.random() * pokemons.size());
        return pokemons.get(pokIdx);
    }

    @PostConstruct
    @SneakyThrows
    public void init() {
        ClassLoader cl = PokemonApplication.class.getClassLoader();
        try(BufferedReader in = new BufferedReader(
                new InputStreamReader(cl.getResourceAsStream("pokemons.json")))) {
            JsonParser parser = new JsonFactory().createParser(in);
            ObjectMapper mapper = new ObjectMapper();
            List<Pokemon> tmp = mapper.readValue(in, new TypeReference<List<Pokemon>>(){});
            for (Pokemon pokemon : tmp) {
                String url = "images/" + pokemon.getId() + ".png";
                InputStream pokemonImageStream = cl.getResourceAsStream("static/" + url);
                if (pokemonImageStream != null) {
                    pokemon.setUrl(url);
                    pokemons.add(pokemon);
                    pokemonImageStream.close();
                }
            }
        }

    }

}
