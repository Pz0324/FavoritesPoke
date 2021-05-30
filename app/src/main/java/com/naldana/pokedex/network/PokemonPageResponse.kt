package com.naldana.pokedex.network

import com.naldana.pokedex.data.entity.NamedAPIResource

data class PokemonPageResponse(
    var count: Int,
    var next: String?,
    var previous: String?,
    var results: List<NamedAPIResource>
    )