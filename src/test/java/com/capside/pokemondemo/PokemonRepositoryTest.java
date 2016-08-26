package com.capside.pokemondemo;

import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author User
 */
public class PokemonRepositoryTest {

    public PokemonRepositoryTest() {
    }

    @Test
    public void testInit() {
        PokemonRepository repo = new PokemonRepository();
        repo.init();
        Pokemon pokemon = repo.getRandomPokemon();
        assertNotNull("The pokémon cannot be null.", pokemon);
    }

}
