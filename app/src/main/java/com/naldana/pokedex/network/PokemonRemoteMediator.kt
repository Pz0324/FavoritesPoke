package com.naldana.pokedex.network

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import com.naldana.pokedex.data.PokedexDatabase
import com.naldana.pokedex.data.entity.PokemonWithType
import com.naldana.pokedex.data.entity.RemoteKey
import com.naldana.pokedex.repository.PokemonRepository
import retrofit2.HttpException
import java.io.IOException

const val REMOTE_KEY = "Pokemon"
@OptIn(ExperimentalPagingApi::class)
class PokemonRemoteMediator(
    val database: PokedexDatabase,
    val pokeAPI: PokeService
): RemoteMediator<Int, PokemonWithType>() {

    val pokemonDao = database.getPokemonDao()
    val remoteDao = database.getRemoteKeyDao()

    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, PokemonWithType>
    ): MediatorResult {
        return try{
            val loadKey = when(loadType){
                LoadType.PREPEND -> return MediatorResult.Success(endOfPaginationReached = true)
                    LoadType.REFRESH -> null
                    LoadType.APPEND -> {
                        val remoteKey = remoteDao.remoteKeyByQuery(REMOTE_KEY)
                        if (remoteKey.nextKey == null) {
                            return MediatorResult.Success(endOfPaginationReached = true)
                        }
                        remoteKey.nextKey
                    }
                }

                val  response = if (loadKey == null) {
                    pokeAPI.getPokemonPage(20)
                }else{
                    pokeAPI.getPokemonPageByURL(loadKey)
                }

                database.withTransaction {
                        if(loadKey == null){
                            pokemonDao.clearAll()
                            remoteDao.deleteByQuery(REMOTE_KEY)
                        }

                        response.results.forEach {
                            val pokemon = pokeAPI.getPokemonByURL(it.url)
                            if(pokemonDao.searchFavorite(pokemon.id) == 1){
                                pokemon.favorite = true
                            }
                            pokemonDao.insertPokemonWithType(pokemon)
                    }

                    if(response.next != null){
                        remoteDao.insertOrReplace(RemoteKey(REMOTE_KEY, response.next!!))
                    }

                }

            MediatorResult.Success(endOfPaginationReached = response.next == null)
        }catch(e: IOException){
            MediatorResult.Error(e)
        }catch (e: HttpException){
            MediatorResult.Error(e)
        }
    }
}