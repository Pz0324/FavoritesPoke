package com.naldana.pokedex

import android.app.Application
import com.naldana.pokedex.data.PokedexDatabase
import com.naldana.pokedex.network.PokeAPI
import com.naldana.pokedex.repository.PokemonRepository

class PokedexApplication : Application() {
    private val _database by lazy { PokedexDatabase.getDatabase(this) }

    val database:PokedexDatabase
        get() = _database

    val pokemonRepository by lazy {
        val pokemonDao = _database.getPokemonDao()
        PokemonRepository(PokeAPI, pokemonDao)
    }
}