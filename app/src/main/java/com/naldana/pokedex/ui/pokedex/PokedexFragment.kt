package com.naldana.pokedex.ui.pokedex

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.naldana.pokedex.PokedexApplication
import com.naldana.pokedex.R
import com.naldana.pokedex.data.entity.Pokemon
import com.naldana.pokedex.data.entity.PokemonWithType
import com.naldana.pokedex.databinding.FragmentPokedexBinding
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

/**
 * Pantalla de busqueda de pokemons
 * Use the [PokedexFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class PokedexFragment : Fragment() {

    /*private val application by lazy{
        requireActivity().application as PokedexApplication
    }*/

    private val pokedexFactory by lazy {
        val app = requireActivity().application as PokedexApplication
        PokedexViewModelFactory(app.pokemonRepository, app.database)
    }

    private val pokedexViewModel: PokedexViewModel by viewModels {
        pokedexFactory
    }

    private var _binding: FragmentPokedexBinding? = null
    private val binding get() = _binding!!


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentPokedexBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.viewModel = pokedexViewModel
        binding.lifecycleOwner = viewLifecycleOwner

        val app = requireActivity().application as PokedexApplication

        val rvAdpater = PokemonsRecyclerViewAdapter(PokemonComparator, pokedexViewModel, requireContext()){
            Log.d("Pokemon_Fragment", "Agregando o modificando el pokemon ${it} a favorito")
            pokedexViewModel.updateFavorite(it)
        }

        val rv = binding.pokemonsRecyclerView.apply {
            adapter = rvAdpater
            layoutManager = LinearLayoutManager(requireContext())
        }

        /*pokedexViewModel.pokemons.observe(viewLifecycleOwner) {
            rvAdpater.setData(it)
        }*/

        lifecycleScope.launch{
            pokedexViewModel.pokemons.flow.collectLatest {
                rvAdpater.submitData(it)
            }
        }

    }

    object PokemonComparator : DiffUtil.ItemCallback<PokemonWithType>(){
        override fun areItemsTheSame(oldItem: PokemonWithType, newItem: PokemonWithType): Boolean {
            return oldItem.pokemon.id == newItem.pokemon.id
        }

        override fun areContentsTheSame(
            oldItem: PokemonWithType,
            newItem: PokemonWithType
        ): Boolean {
            return oldItem == newItem
        }
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @return A new instance of fragment PokedexFragment.
         */
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            PokedexFragment().apply {
                arguments = Bundle().apply {
                }
            }
    }
}