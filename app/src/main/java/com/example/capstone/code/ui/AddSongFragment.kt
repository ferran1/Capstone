package com.example.capstone.code.ui

import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.capstone.R
import com.example.capstone.code.viewmodel.SongViewModel
import com.example.capstone.databinding.FragmentAddSongBinding


/**
 * A simple [Fragment] subclass as the second destination in the navigation.
 */
class AddSongFragment : Fragment() {

    private lateinit var binding: FragmentAddSongBinding

    private val viewModel: SongViewModel by viewModels()

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        setHasOptionsMenu(true)

        // Setup view binding to get the elements from the layout
        binding = FragmentAddSongBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        requireActivity().title = getString(R.string.add_song)

        activity?.findViewById<Toolbar>(R.id.toolbar)!!.setNavigationOnClickListener {
            findNavController().popBackStack()
        }
    }

    // Setup the layout (view)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.fabSaveSong.setOnClickListener {
            addSong()
        }

    }

    // Insert the song into the database
    private fun addSong() {
        val songUrl = binding.etUrl.text.toString()

        val songName = "Save Your Tears"
        val artist = "The Weeknd"
        val platform = "Youtube"

        this.viewModel.insertSong(songUrl, songName, artist, platform)
        findNavController().navigate(R.id.action_addSongFragment_to_songBacklogFragment)

//        println(songUrl)
//        Log.d("EA", songUrl)


            // Check from which platform
//            if (songUrl.contains("youtube", ignoreCase = true)){
//                getSongDataFromPlatform(songUrl, "youtube")
//            } else if (songUrl.contains("spotify", ignoreCase = true)) {
//                getSongDataFromPlatform(songUrl, "spotify")
//            } else if (songUrl.contains("soundcloud", ignoreCase = true)){
//                getSongDataFromPlatform(songUrl, "soundcloud")
//            } else {
//                Toast.makeText(context, getString(R.string.incorrect_url_msg), Toast.LENGTH_LONG)
//                        .show()
//            }

// ------------------------------------------------------- song link examples:
        /*
        Youtube:
        https://www.youtube.com/watch?v=u6lihZAcy4s

        Spotify:
        URI (Not sure if URI or link is needed for the Spotify API:
        spotify:track:5QO79kh1waicV47BqGRL3
        Song link example:
        https://open.spotify.com/track/5QO79kh1waicV47BqGRL3g?si=8hB3eXJoTbalyA3yK9gl-A

        Soundcloud:
        https://soundcloud.com/theweeknd/save-your-tears

        */
    }

    private fun getSongDataFromPlatform(url: String, platform: String) {



    }
}