package com.example.capstone.code.ui

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.capstone.R
import com.example.capstone.databinding.FragmentSongBacklogBinding

/**
 * A simple [Fragment] subclass as the default destination in the navigation.
 */
class SongBacklogFragment : Fragment() {

    private lateinit var binding: FragmentSongBacklogBinding

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        setHasOptionsMenu(true)

        binding = FragmentSongBacklogBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        requireActivity().title = getString(R.string.app_name)

        binding.fabAddSong.setOnClickListener {
            findNavController().navigate(R.id.action_songBacklogFragment_to_addSongFragment)
        }

    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
//        super.onCreateOptionsMenu(menu, inflater)
//        menu.findItem(R.id.action_delete_games).isVisible = true
    }

}