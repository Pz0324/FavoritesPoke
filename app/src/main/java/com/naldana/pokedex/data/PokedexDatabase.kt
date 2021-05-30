package com.naldana.pokedex.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.naldana.pokedex.data.dao.PokemonDao
import com.naldana.pokedex.data.dao.RemoteKeyDao
import com.naldana.pokedex.data.entity.FavoritePokemon
import com.naldana.pokedex.data.entity.Pokemon
import com.naldana.pokedex.data.entity.PokemonType
import com.naldana.pokedex.data.entity.RemoteKey
import kotlinx.coroutines.CoroutineScope

@Database(
    entities = [Pokemon::class, PokemonType::class, RemoteKey::class, FavoritePokemon::class],
    version = 3,
    exportSchema = true
)

abstract class PokedexDatabase : RoomDatabase() {
    abstract fun getPokemonDao(): PokemonDao
    abstract fun getRemoteKeyDao(): RemoteKeyDao

    companion object {
        @Volatile
        private var INSTANCE: PokedexDatabase? = null

        fun getDatabase(context: Context): PokedexDatabase {
            return INSTANCE?: synchronized(this){
                val instance = Room.databaseBuilder(
                    context,
                    PokedexDatabase::class.java, "pokedexDb"
                ).fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}