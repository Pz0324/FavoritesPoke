package com.naldana.pokedex.ui.pokedex

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.naldana.pokedex.R
import com.naldana.pokedex.data.entity.Pokemon
import com.naldana.pokedex.data.entity.PokemonType
import com.naldana.pokedex.data.entity.PokemonWithType
import com.naldana.pokedex.repository.PokemonRepository
import java.util.*

class PokemonsRecyclerViewAdapter(
    differCallback: DiffUtil.ItemCallback<PokemonWithType>,
    val pokedexViewModel: PokedexViewModel,
    val context: Context,
    private val favClickAction: (id: Int) -> Unit) :
    PagingDataAdapter<PokemonWithType, PokemonsRecyclerViewAdapter.PokemonViewHolder>(differCallback){

    //MODIFICAR LA CONSULTA PARA QUE RECIBA SI ES FAV O NEL, EN LA LINE 25 DESPUES DE POKEMONWITHTYPE
    private var pokemons: List<PokemonWithType>? = null
    var color: Int = Color.RED
    var flag : Boolean = false


    inner class PokemonViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bind(data: PokemonWithType, favClickAction: (id: Int) -> Unit ) {
            val idTextView = itemView.findViewById<TextView>(R.id.id_pokemon)
            val nameTextView = itemView.findViewById<TextView>(R.id.name_pokemon)
            val imageView = itemView.findViewById<ImageView>(R.id.image_pokemon)
            val favButton = itemView.findViewById<ImageView>(R.id.fav_button)
           setTypes(data.types)

                idTextView.text = data.pokemon.id.toString()
                nameTextView.text = data.pokemon.name.capitalize(Locale.ROOT)
                if(data.pokemon.favorite){
                    favButton.setColorFilter(Color.RED)
                }else{
                    favButton.setColorFilter(Color.GRAY)
                }

                favButton.setOnClickListener {
                    favClickAction(data.pokemon.id)
                    if(!data.pokemon.favorite){
                        favButton.setColorFilter(Color.RED)
                        data.pokemon.favorite = true
                    }else{
                        favButton.setColorFilter(Color.GRAY)
                        data.pokemon.favorite = false
                    }
                }
                Glide.with(itemView)
                    .load(data.pokemon.sprites.frontDefault)
                    .centerCrop()
                    .placeholder(R.drawable.pokebola)
                    .into(imageView)


        }

        private fun setTypes(types: List<PokemonType>){
            val chips = itemView.findViewById<ChipGroup>(R.id.pokemon_types)
            chips.removeAllViews()
            types.forEach {
                chips.addView(Chip(itemView.context).apply{
                    text = it.type.name.capitalize(Locale.getDefault())
                })
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PokemonViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_pokemon, parent, false)
        return PokemonViewHolder(view)
    }

    override fun onBindViewHolder(holder: PokemonViewHolder, position: Int) {
        /*val data = pokemons ?: return
        val pokemon = data[position]
        holder.bind(pokemon)*/

        val item = getItem(position)
        item?.let { holder.bind(it, favClickAction)}

    }
}
