package com.example.capstone.code.ui

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import com.example.capstone.R

/**
 * A simple [Fragment] subclass as the default destination in the navigation.
 */
class SongBacklogFragment : Fragment() {

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_song_backlog, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
//        super.onCreateOptionsMenu(menu, inflater)
//        menu.findItem(R.id.action_delete_games).isVisible = true
    }

}