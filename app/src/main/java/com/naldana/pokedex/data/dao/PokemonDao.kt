package com.naldana.pokedex.data.dao

import androidx.lifecycle.LiveData
import androidx.paging.PagingSource
import androidx.room.*
import com.naldana.pokedex.data.entity.FavoritePokemon
import com.naldana.pokedex.data.entity.Pokemon
import com.naldana.pokedex.data.entity.PokemonType
import com.naldana.pokedex.data.entity.PokemonWithType

@Dao
interface PokemonDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(pokemon: Pokemon)

    @Update
    suspend fun update(pokemon: Pokemon)

    @Query("SELECT * FROM pokemons WHERE id = :key or name = :key")
    suspend fun search(key: String): Pokemon?

    @Query("SELECT * FROM pokemons")
    fun findAll(): LiveData<List<Pokemon>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTypesPokemon(types: List<PokemonType>)

    @Transaction
    suspend fun insertPokemonWithType(pokemon: Pokemon){
        insert(pokemon)
        val pokemonTypes =
            pokemon.types.map{
                it.idPokemon = pokemon.id
                it
            }
        insertTypesPokemon(pokemonTypes)
    }

    @Transaction
    @Query("SELECT * FROM pokemons")
    fun getPokemonsWithType(): LiveData<List<PokemonWithType>>

    @Transaction
    @Query("SELECT * FROM favorite_pokemons")
    fun getFavoritePokemons(): LiveData<List<FavoritePokemon>>

    @Query("SELECT EXISTS (SELECT 1 FROM favorite_pokemons WHERE id=:id)")
    fun searchFavorite(id: Int): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFavoritePokemon(favoritePokemon: FavoritePokemon)

    @Transaction
    suspend fun updateFavorite(id: Int){
        if(searchFavorite(id) == 1){
            deleteFavoritePokemon(FavoritePokemon(id))
        }else{
            insertFavoritePokemon(FavoritePokemon(id))
        }
    }

    @Delete
    suspend fun deleteFavoritePokemon(favoritePokemon: FavoritePokemon)

    @Transaction
    @Query("SELECT * FROM pokemons")
    fun getPokemonPagingSourceWithType(): PagingSource<Int, PokemonWithType>

    @Query("DELETE FROM pokemons")
    fun clearAll()
    @Delete
    suspend fun delete(pokemon: Pokemon)









}