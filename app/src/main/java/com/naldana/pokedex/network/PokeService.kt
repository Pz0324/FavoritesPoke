package com.naldana.pokedex.network

import androidx.lifecycle.LiveData
import androidx.room.Transaction
import com.bumptech.glide.annotation.GlideType
import com.naldana.pokedex.data.entity.Pokemon
import com.naldana.pokedex.data.entity.PokemonWithType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Url
import retrofit2.http.Query


interface PokeService {
    @GET("pokemon/{key}")
    suspend fun getPokemon(@Path("key")key: String): Pokemon?

    @GET("pokemon")
    suspend fun getPokemonPage(@Query("limit")limit: Int): PokemonPageResponse

    @GET
    suspend fun getPokemonPageByURL(@Url url: String): PokemonPageResponse

    @GET
    suspend fun getPokemonByURL(@Url url: String) : Pokemon

}