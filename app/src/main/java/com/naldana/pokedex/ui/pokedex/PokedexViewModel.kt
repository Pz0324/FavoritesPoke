package com.naldana.pokedex.ui.pokedex

import android.view.View
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import com.naldana.pokedex.R
import com.naldana.pokedex.data.PokedexDatabase
import com.naldana.pokedex.data.entity.FavoritePokemon
import com.naldana.pokedex.data.entity.Pokemon
import com.naldana.pokedex.network.PokeAPI
import com.naldana.pokedex.network.PokemonRemoteMediator
import com.naldana.pokedex.repository.PokemonRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.HttpException

@OptIn(ExperimentalPagingApi::class)
class PokedexViewModel(private val repository: PokemonRepository,
                       database:PokedexDatabase) : ViewModel() {

    var key = MutableLiveData("")
    var pokemon = MutableLiveData<Pokemon>()
    private var _loading = MutableLiveData(View.GONE)
    val loading: LiveData<Int> get() = _loading
    private var _error = MutableLiveData<Int?>(null)
    private var _pokemonEncontrado = MutableLiveData(0)
    val pokemonEncontrado: LiveData<Int> get() = _pokemonEncontrado

    val error: LiveData<Int?> get() = _error
    val pokemonDao = database.getPokemonDao()
    val pokemons = Pager(
        config = PagingConfig(pageSize = 20),
        remoteMediator = PokemonRemoteMediator(database, PokeAPI.service)
    ){
        pokemonDao.getPokemonPagingSourceWithType()
    }

    /**
     * Busca un pokemon
     */
    fun search() {
        _loading.value = View.VISIBLE
        _error.value = null
        viewModelScope.launch {
            try {
                val key = key.value
                if (key.isNullOrEmpty()) {
                    _error.value = R.string.error_pokemon_empty
                } else {
                    pokemon.value = repository.getPokemon(key)
                }

            } catch (e: HttpException) {
                _error.value = R.string.error_not_found
            } finally {
                _loading.value = View.GONE
            }
        }
    }

    fun delete(id : Int) = viewModelScope.launch(Dispatchers.IO) {
      repository.deleteFavoritePokemon(id)
    }

    fun insert(id: Int) = viewModelScope.launch(Dispatchers.IO) {
        repository.insertFavoritePokemon(id)
    }

    fun updateFavorite(id : Int) = viewModelScope.launch(Dispatchers.IO) {
        repository.updateFavorite(id)
    }

    fun searchFavorite(id : Int) = viewModelScope.launch(Dispatchers.IO){
        _pokemonEncontrado.postValue(repository.searchFavoritePokemon(id))
    }
}