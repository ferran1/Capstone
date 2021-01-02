package com.example.capstone.code.ui

import android.os.Bundle
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.capstone.R
import com.example.capstone.databinding.FragmentPlaybackBinding

class PlaybackFragment: Fragment() {

    private lateinit var binding: FragmentPlaybackBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        setHasOptionsMenu(true)

        container?.removeAllViews()

        binding = FragmentPlaybackBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        menu.findItem(R.id.action_delete_songs).isVisible = false
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        requireActivity().title = getString(R.string.play_song)

        (activity as AppCompatActivity).supportActionBar!!.setDisplayHomeAsUpEnabled(false)

        // Switch back to songBacklog fragment
        binding.button.setOnClickListener { v ->
            val activity = v!!.context as AppCompatActivity
            val songBacklogFragment = SongBacklogFragment()
            activity.supportFragmentManager.beginTransaction()
                .replace(R.id.playbackFragment, songBacklogFragment).addToBackStack(null)
                .commit()
        }

    }


}