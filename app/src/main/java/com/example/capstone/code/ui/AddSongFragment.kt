package com.example.capstone.code.ui

import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.android.volley.*
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.example.capstone.BuildConfig
import com.example.capstone.R
import com.example.capstone.code.viewmodel.SongViewModel
import com.example.capstone.databinding.FragmentAddSongBinding
import org.json.JSONException

/**
 * A simple [Fragment] subclass as the second destination in the navigation.
 */
class AddSongFragment : Fragment() {

    private lateinit var binding: FragmentAddSongBinding

    private val viewModel: SongViewModel by viewModels()

    private lateinit var youtubeSongUrl: String

    // Api key for the Youtube api, key is configured in app/build.gradle -> buildtypes { debug {} }
    private val YOUTUBE_API_KEY = BuildConfig.ApiKey

    private val BASE_URL = "https://youtube.googleapis.com/youtube/v3/videos?part=snippet&id="

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
        menu.findItem(R.id.action_delete_songs).isVisible = false

        activity?.findViewById<Toolbar>(R.id.toolbar)!!.setNavigationOnClickListener {
            findNavController().popBackStack()
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.fabSaveSong.setOnClickListener {
            addSong()
        }
    }

    /**
     * looks at which platform the song belongs to
     */
    private fun addSong() {

        val songUrl: String = binding.etUrl.text.toString()

        // Check from which platform
        when {
            songUrl.contains("youtube", ignoreCase = true) -> {
                youtubeSongUrl =
                    songUrl // Save the url in a class variable because we want to later retrieve it in addYoutubeSong()
                getYoutubeSongInfo(songUrl)
//                YoutubeAsyncTask().execute(songUrl)
            }
            songUrl.contains("spotify", ignoreCase = true) -> {
                getSongDataFromPlatform(songUrl, "spotify")
            }
            songUrl.contains("soundcloud", ignoreCase = true) -> {
                getSongDataFromPlatform(songUrl, "soundcloud")
            }
            else -> {
                Toast.makeText(context, getString(R.string.incorrect_url_msg), Toast.LENGTH_LONG)
                    .show()
            }
        }

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

    /**
     * Get the Youtube song name and artist by executing a GET request to the Youtube Data API
     */
    private fun getYoutubeSongInfo(url: String) {

        val videoId = url.substringAfter("=")

        val APIRequestURL =
            "$BASE_URL$videoId&key=$YOUTUBE_API_KEY"

        // Instantiate the RequestQueue.
        val queue = Volley.newRequestQueue(requireActivity())

        val request = JsonObjectRequest(Request.Method.GET, APIRequestURL, null, { response ->
            try {

                // Parse the JSON to get the title from the youtube video
                val jsonArray = response.getJSONArray("items")

                val jsonArraySnippet = jsonArray.getJSONObject(0).getJSONObject("snippet")

                val songNameAndArtist = jsonArraySnippet.getString("title")

                addYoutubeSong(songNameAndArtist)

            } catch (e: JSONException) {
                e.printStackTrace()
            }
        }, { error ->
            error.printStackTrace()
        })

        queue?.add(request)

    }

    /**
     * @param songNameAndArtist is the song name and artist name parsed from the youtube video
     * Inserts the youtube song into the database
     */
    private fun addYoutubeSong(songNameAndArtist: String) {

        val nameAndArtist = songNameAndArtist.split("-")

        val artist = nameAndArtist[0]

        // Remove unnecessary info from title: (Official Music video)
        val songName = nameAndArtist[1].substringBefore("(")

        this.viewModel.insertSong(
            youtubeSongUrl,
            songName,
            artist,
            "Youtube"
        )

        findNavController().navigate(R.id.action_addSongFragment_to_songBacklogFragment)

    }

//    @SuppressLint("StaticFieldLeak")
//    inner class YoutubeAsyncTask : AsyncTask<String, Void?, String>() {
//
//        override fun doInBackground(vararg params: String): String {
//
//            val url = params[0]
//
//            val crawledNameAndArtist = videoTitle
//
//            val crawledNameAndArtist = songNameAndArtist
//
//            return crawledNameAndArtist
//        }
//
//        override fun onPostExecute(crawledNameAndArtist: String) {
//            super.onPostExecute(crawledNameAndArtist)
//
//            Toast.makeText(
//                context,
//                binding.textView.text.toString(),
//                Toast.LENGTH_LONG
//            )
//                .show()
//            addYoutubeSong(crawledNameAndArtist)
//        }
//    }

}
