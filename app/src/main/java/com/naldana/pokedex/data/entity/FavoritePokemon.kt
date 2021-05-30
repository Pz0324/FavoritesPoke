package com.naldana.pokedex.data.entity

import androidx.room.*
import com.google.gson.annotations.SerializedName

@Entity(tableName = "favorite_pokemons")
data class FavoritePokemon (
    @PrimaryKey @ColumnInfo(name = "id") val id : Int
)

