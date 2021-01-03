package com.example.capstone.code.ui

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context.CLIPBOARD_SERVICE
import android.os.Bundle
import android.util.Base64
import android.view.*
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat.getSystemService
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

    private lateinit var songUrl: String

    // API key for the Youtube API, key is configured in app/build.gradle -> buildtypes { debug {} }
    private val YOUTUBE_API_KEY = BuildConfig.ApiKey
    private val YOUTUBE_BASE_URL = "https://youtube.googleapis.com/youtube/v3/videos?part=snippet&id="

    private val SPOTIFY_CLIENT_ID = BuildConfig.SpotifyClientId
    private val SPOTIFY_CLIENT_SECRET = BuildConfig.SpotifyClientSecret

    private val ACCESS_TOKEN_START_INDEX = 17
    private val ACCESS_TOKEN_END_INDEX = 100
    private val SONG_ID_START_INDEX = 31
    private val SONG_ID_END_INDEX = 53

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

        // Paste clipboard when paste button has been clicked
        binding.imgBtnPasteClipboard.setOnClickListener {

            val clipBoardManager = requireContext().getSystemService(CLIPBOARD_SERVICE) as ClipboardManager

            val clipData = clipBoardManager.primaryClip
            val item : ClipData.Item? = clipData?.getItemAt(0)

            if (item != null) {
                binding.etUrl.setText(item.text.toString())
            }

            Toast.makeText(
                context,
                getString(R.string.pasted),
                Toast.LENGTH_LONG
            )
                .show()
        }
    }

    /**
     * looks at which platform the song belongs to
     */
    private fun checkSongUrl() {

        val songUrl: String = binding.etUrl.text.toString()

        this.songUrl = songUrl

        when {
            songUrl.contains("youtube", ignoreCase = true) -> {
                getSongInfo(songUrl, "Youtube")
            }
            songUrl.contains("spotify", ignoreCase = true) -> {
                this.songUrl = songUrl
                getSongInfo(songUrl, "Spotify")
            }
            songUrl.contains("soundcloud", ignoreCase = true) -> {

                val songName = binding.etName.text.toString()
                val artist = binding.etArtist.text.toString()

                if (songName.isNotEmpty() && artist.isNotEmpty()) {
                    this.viewModel.insertSong(songUrl, songName, artist, "Soundcloud")
                    findNavController().navigate(R.id.action_addSongFragment_to_songBacklogFragment)
                } else {
                    Toast.makeText(
                        context,
                        getString(R.string.add_soundcloud_song_error),
                        Toast.LENGTH_LONG
                    )
                        .show()
                }
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
                        val accessToken = response.toString().substring(ACCESS_TOKEN_START_INDEX, ACCESS_TOKEN_END_INDEX)

                        // Execute a GET request to retrieve the song name and artist from the API
                        val songId = songUrl.substring(SONG_ID_START_INDEX, SONG_ID_END_INDEX)
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
        }
    }

    /**
     * @param songNameAndArtist is the song name and artist name parsed from the youtube video
     * Inserts the youtube song into the database
     */
    private fun addYoutubeSong(songNameAndArtist: String) {

                if (songNameAndArtist.contains("-")) {
                    val nameAndArtist = songNameAndArtist.split("-")

                    val artist = nameAndArtist[0]

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
                } else if (songNameAndArtist.contains("~")){
                    val nameAndArtist = songNameAndArtist.split("~")

                    val artist = nameAndArtist[0]

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

}
