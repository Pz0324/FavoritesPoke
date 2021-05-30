package com.naldana.pokedex.repository

import androidx.lifecycle.LiveData
import com.naldana.pokedex.data.dao.PokemonDao
import com.naldana.pokedex.data.entity.FavoritePokemon
import com.naldana.pokedex.network.PokeAPI
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class PokemonRepository(
    private val API: PokeAPI,
    private val pokemonDao: PokemonDao,
    ) {
    /**
     * Busca un pokemon
     * @param key [String] id
     * @return [String] JSON con la información de pokemon
     */


    suspend fun getPokemon(key: String) = withContext(Dispatchers.IO){
        var pokemon = pokemonDao.search(key)
        if (pokemon == null) {
            pokemon = API.service.getPokemon(key)
            if (pokemon != null) {
                pokemonDao.insertPokemonWithType(pokemon)
            }
        }
        pokemon
    }
    fun findAll() = pokemonDao.getPokemonsWithType()

    suspend fun insertFavoritePokemon(id: Int) {
        return pokemonDao.insertFavoritePokemon(FavoritePokemon(id))
    }

    suspend fun updateFavorite(id: Int) {
        return pokemonDao.updateFavorite(id)
    }

    suspend fun searchFavoritePokemon (id: Int) = withContext(Dispatchers.IO){
        val searchPokemon =  pokemonDao.searchFavorite(id)
        searchPokemon
    }

    suspend fun deleteFavoritePokemon(id: Int) {
       pokemonDao.deleteFavoritePokemon(FavoritePokemon(id))
    }
}