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
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.capstone.BuildConfig
import com.example.capstone.R
import com.example.capstone.code.dao.SongDao
import com.example.capstone.code.model.Song
import com.example.capstone.code.repository.SongRepository
import com.example.capstone.code.viewmodel.SongViewModel
import com.example.capstone.databinding.FragmentAddSongBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONException

/**
 * A simple [Fragment] subclass as the second destination in the navigation.
 */
class AddSongFragment : Fragment() {

    private lateinit var binding: FragmentAddSongBinding
    private val viewModel: SongViewModel by viewModels()
    private lateinit var songRepository: SongRepository

    private val mainScope = CoroutineScope(Dispatchers.Main)

    private lateinit var songUrl: String

    // Api key for the Youtube api, key is configured in app/build.gradle -> buildtypes { debug {} }
    private val YOUTUBE_API_KEY = BuildConfig.ApiKey
    private val YOUTUBE_BASE_URL =
        "https://youtube.googleapis.com/youtube/v3/videos?part=snippet&id="

    private val SPOTIFY_BASE64_CLIENTDETAILS = BuildConfig.SpotifyClientDetails

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

        songRepository = SongRepository(requireContext())

        binding.fabSaveSong.setOnClickListener {
            checkSongUrl()
        }
    }

    /**
     * looks at which platform the song belongs to
     */
    private fun checkSongUrl() {

        val songUrl: String = binding.etUrl.text.toString()

        mainScope.launch {
            withContext(Dispatchers.IO) {
                // Song URL already exists if getCount returns an Int higher than 0
                if (songRepository.getCount(songUrl) > 0) {
//                    Toast.makeText(context, getString(R.string.song_exists_msg), Toast.LENGTH_LONG)
//                        .show()
                    Log.d("Exists", "Song already exists in the database")
                }
            }
        }

            when {
                songUrl.contains("youtube", ignoreCase = true) -> {
                    this.songUrl =
                        songUrl // Save the url in a class variable because we want to later access it in addSong()
                    getSongInfo(songUrl, "youtube")
//                YoutubeAsyncTask().execute(songUrl)
                }
                songUrl.contains("spotify", ignoreCase = true) -> {
                    this.songUrl = songUrl
                    getSongInfo(songUrl, "spotify")
                }
                songUrl.contains("soundcloud", ignoreCase = true) -> {

                    val songName = binding.etName.text.toString()
                    val artist = binding.etArtist.text.toString()

                    this.viewModel.insertSong(songUrl, songName, artist, "Soundcloud")

                    findNavController().navigate(R.id.action_addSongFragment_to_songBacklogFragment)

                }
                else -> {
                    Toast.makeText(
                        context,
                        getString(R.string.incorrect_url_msg),
                        Toast.LENGTH_LONG
                    )
                        .show()
                }
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


    /**
     * Get the song name and artist by executing a GET request to the API depending on the platform of the song
     */
    private fun getSongInfo(url: String, platform: String) {

        if (platform == "youtube") {

            val videoId = url.substringAfter("=")
            val APIRequestURL =
                "$YOUTUBE_BASE_URL$videoId&key=$YOUTUBE_API_KEY"

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

        } else if (platform == "spotify") {

            // Obtain an access token, POST request to:
            val APIRequestURL = "https://accounts.spotify.com/api/token"

            // Body must be encoded in application/x-www-form-urlencoded

            // Request body parameter:
            // grant type
            // Value:
            // Set to client_credentials

            // Header parameter
            // Authorization
            // Base 64 encoded string that contains the client ID and client secret key. The field must have the format: Authorization: Basic <base64 encoded client_id:client_secret>

            //curl -X "POST" -H "Authorization: Basic ZjM4ZjAw...WY0MzE=" -d grant_type=client_credentials https://accounts.spotify.com/api/token

            // Header parameter of the POST request must contain this ( -H Authorization: " ) :
            val authorizationHeader = "Basic $SPOTIFY_BASE64_CLIENTDETAILS"

            Log.d("Auth header", authorizationHeader)

            val queue = Volley.newRequestQueue(requireActivity())

            val params = HashMap<String, String>()
            params["Authorization"] = authorizationHeader // Header parameter
            params["grant-type"] = "client_credentials" // Request body parameter

            val stringRequest = StringRequest(Request.Method.POST, url,
                { response ->
                    val accessToken = response
                },
                { Log.d("Error", "Something went wrong trying to obtain an access token") })

            queue?.add(stringRequest)


//
//            val stringRequest = StringRequest(Request.Method.POST, APIRequestURL, null, object:
//                Response.Listener<String>,
//                Response.ErrorListener {
//
//                override fun onResponse(response:String) {
//                    Log.d("Access token:", response)
//                }
//
//                override fun onErrorResponse(error: VolleyError?) {
//                    Log.d("Something went wrong", error.toString())
//                }
//
//            })


        } else { // Soundcloud

        }

    }

    /**
     * @param songNameAndArtist is the song name and artist name parsed from the youtube video
     * Inserts the youtube song into the database
     */
    private fun addYoutubeSong(songNameAndArtist: String) {

        val nameAndArtist = songNameAndArtist.split("-")

        val artist = nameAndArtist[0]

        // Remove unnecessary info from title, for example: (Official Music video)
        if (nameAndArtist[1].contains("(")) {
            val songName = nameAndArtist[1].substringBefore("(")
            this.viewModel.insertSong(
                songUrl,
                songName,
                artist,
                "Youtube"
            )
        } else {
            this.viewModel.insertSong(
                songUrl,
                nameAndArtist[1],
                artist,
                "Youtube"
            )
        }

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
