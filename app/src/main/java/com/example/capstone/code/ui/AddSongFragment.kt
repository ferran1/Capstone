package com.example.capstone.code.ui

import android.os.Bundle
import android.util.Base64
import android.view.*
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.android.volley.AuthFailureError
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.capstone.BuildConfig
import com.example.capstone.R
import com.example.capstone.code.IVolley
import com.example.capstone.code.repository.SongRepository
import com.example.capstone.code.viewmodel.SongViewModel
import com.example.capstone.databinding.FragmentAddSongBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import org.json.JSONException
import org.json.JSONObject

/**
 * A simple [Fragment] subclass as the second destination in the navigation.
 */
class AddSongFragment : Fragment() {

    private lateinit var binding: FragmentAddSongBinding
    private val viewModel: SongViewModel by viewModels()
    private lateinit var songRepository: SongRepository

    private val iVolley: IVolley? = null

    private val mainScope = CoroutineScope(Dispatchers.Main)

    private lateinit var songUrl: String

    // Api key for the Youtube api, key is configured in app/build.gradle -> buildtypes { debug {} }
    private val YOUTUBE_API_KEY = BuildConfig.ApiKey
    private val YOUTUBE_BASE_URL = "https://youtube.googleapis.com/youtube/v3/videos?part=snippet&id="

    private val SPOTIFY_CLIENT_ID = BuildConfig.SpotifyClientId
    private val SPOTIFY_CLIENT_SECRET = BuildConfig.SpotifyClientSecret

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

        this.songUrl = songUrl // Save the url in a class variable because we want to later access it in other methods

        when {
            songUrl.contains("youtube", ignoreCase = true) -> {
                getSongInfo(songUrl, "Youtube")
//                YoutubeAsyncTask().execute(songUrl)
            }
            songUrl.contains("spotify", ignoreCase = true) -> {
                this.songUrl = songUrl
                getSongInfo(songUrl, "Spotify")
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
    URI:
    spotify:track:3dp4LGJNWdMGqF2aPeyiwl

    https://open.spotify.com/track/3dp4LGJNWdMGqF2aPeyiwl?si=yT66QQwbTxqZJ_poAsj4pQ

    Soundcloud:
    https://soundcloud.com/theweeknd/save-your-tears

    */


    /**
     * Get the song name and artist by executing a GET request to the API depending on the platform of the song
     */
    private fun getSongInfo(url: String, platform: String) {

        when (platform) {
            "Youtube" -> {

                val videoId = url.substringAfter("=")
                val APIRequestURL =
                    "$YOUTUBE_BASE_URL$videoId&key=$YOUTUBE_API_KEY"

                // Instantiate the RequestQueue.
                val queue = Volley.newRequestQueue(requireActivity())
                val request = JsonObjectRequest(
                    Request.Method.GET,
                    APIRequestURL,
                    null,
                    { response ->
                        try {

                            // Parse the JSON to get the title from the youtube video
                            val jsonArray = response.getJSONArray("items")

                            val jsonArraySnippet =
                                jsonArray.getJSONObject(0).getJSONObject("snippet")

                            val songNameAndArtist = jsonArraySnippet.getString("title")

                            addYoutubeSong(songNameAndArtist)

                        } catch (e: JSONException) {
                            e.printStackTrace()
                        }
                    },
                    { error ->
                        error.printStackTrace()
                    })
                queue?.add(request)

            }
            "Spotify" -> {

                // Obtain an access token to get authorized for the Spotify Web API
                val APIRequestURL = "https://accounts.spotify.com/api/token"

                val queue = Volley.newRequestQueue(requireActivity())

                val postRequest = object : StringRequest(
                    Method.POST,
                    APIRequestURL,
                    Response.Listener { response ->

                        // Get access token from the JSON response
                        val accessToken = response.toString().substring(17, 100)

                        // Execute a GET request to retrieve the song name and artist from the API
                        val songId = songUrl.substring(31, 53)
                        val GETrequestURL = "https://api.spotify.com/v1/tracks/$songId"
                        val secondQueue = Volley.newRequestQueue(requireActivity())

                        val getRequest = object :
                            StringRequest( // Use StringRequest because getheaders() is unavailable with JsonObjectRequest
                                Method.GET,
                                GETrequestURL,
                                Response.Listener { response ->

                                    var obj = JSONObject(response)
                                    obj = obj.getJSONObject("album")

                                    val artistsArray = obj.getJSONArray("artists")
                                    val artist = artistsArray.getJSONObject(0).getString("name")

                                    val songName = obj.getString("name")

                                    addSpotifySong(songName, artist)

                                    iVolley?.onResponse(response.toString())
                                },
                                Response.ErrorListener { error -> iVolley!!.onResponse((error.message!!)) }) {

                            @Throws(AuthFailureError::class)
                            override fun getHeaders(): Map<String, String> {
                                val headers: MutableMap<String, String> = HashMap()
                                headers["Authorization"] =
                                    "Bearer $accessToken" // Header authorization parameter
                                return headers
                            }
                        }

                        secondQueue?.add(getRequest)

                        iVolley?.onResponse(response.toString())
                    },
                    Response.ErrorListener { error -> iVolley!!.onResponse((error.message!!)) }) {

                    override fun getBodyContentType(): String {
                        return "application/x-www-form-urlencoded; charset=UTF-8"
                    }

                    @Throws(AuthFailureError::class)
                    override fun getHeaders(): Map<String, String> {
                        val credentials = "$SPOTIFY_CLIENT_ID:$SPOTIFY_CLIENT_SECRET"
                        val base64EncodedCredentials: String =
                            Base64.encodeToString(credentials.toByteArray(), Base64.NO_WRAP)

                        val headers: MutableMap<String, String> = HashMap()
                        headers["Authorization"] =
                            "Basic $base64EncodedCredentials" // Header authorization parameter
                        return headers
                    }

                    override fun getParams(): MutableMap<String, String> {
                        val params = HashMap<String, String>()
                        params["grant_type"] = "client_credentials" // Request body parameter
                        return params
                    }
                }

                queue?.add(postRequest)

            }
            "Soundcloud" -> {

            }
        }
    }

    /**
     * @param songNameAndArtist is the song name and artist name parsed from the youtube video
     * Inserts the youtube song into the database
     */
    private fun addYoutubeSong(songNameAndArtist: String) {

                // TODO: error handling, check if youtube song has a "-" in the title
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

    private fun addSpotifySong(songName: String, artist: String) {
        this.viewModel.insertSong(
            songUrl,
            songName,
            artist,
            "Spotify"
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
