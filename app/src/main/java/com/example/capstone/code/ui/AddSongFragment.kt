package com.example.capstone.code.ui

import android.annotation.SuppressLint
import android.os.AsyncTask
import android.os.Bundle
import android.util.Log
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

    private lateinit var youtubeSongUrl: String

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
        val songUrl: String = binding.etUrl.text.toString()

        // Check from which platform
        when {
            songUrl.contains("youtube", ignoreCase = true) -> {
                // If the URL is from Youtube, crawl the song name and artist from the Youtube in a background AsyncTask
                youtubeSongUrl = songUrl // Save the url in a class variable because we want to later retrieve it in addYoutubeSong()
                YoutubeAsyncTaskCrawler().execute(songUrl)
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


//        val songName = "Save Your Tears"
//        val artist = "The Weeknd"
//        val platform = "Youtube"
//
//        this.viewModel.insertSong(songUrl, songName, artist, platform)
//        findNavController().navigate(R.id.action_addSongFragment_to_songBacklogFragment)


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

    // Get the song name and artist from async task and add the Youtube song into the database
    private fun addYoutubeSong(crawledNameAndArtist: String) {

        // TODO:
        // Split the crawdledNameAndArtist String into 2 strings, the artist and the song

        this.viewModel.insertSong(youtubeSongUrl, crawledNameAndArtist, crawledNameAndArtist, "Youtube")

        findNavController().navigate(R.id.action_addSongFragment_to_songBacklogFragment)

//        Toast.makeText(
//            context,
//            "$crawledNameAndArtist Has successfully been scraped",
//            Toast.LENGTH_LONG
//        )
//            .show()

    }

    @SuppressLint("StaticFieldLeak")
    inner class YoutubeAsyncTaskCrawler : AsyncTask<String, Void?, String>() {

        override fun doInBackground(vararg params: String): String {

            val url = params[0]
//            val doc: Document = Jsoup.connect(params[0]).get()

//            Log.d("test", url)

            val crawledNameAndArtist = "Jason Derulo - Swag"

            return crawledNameAndArtist
        }


        override fun onPostExecute(crawledNameAndArtist: String) {
            super.onPostExecute(crawledNameAndArtist)
            addYoutubeSong(crawledNameAndArtist)
        }
    }
}

// Web scraping example:

//try {
//    val url = "https://www.cinemaqatar.com/"
//    val doc: Document = Jsoup.connect(url).get()
//    val data: Elements = doc.select("span.thumbnail")
//    val size: Int = data.size()
//    Log.d("doc", "doc: $doc")
//    Log.d("data", "data: $data")
//    Log.d("size", "" + size)
//    for (i in 0 until size) {
//        val imgUrl: String = data.select("span.thumbnail")
//            .select("img")
//            .eq(i)
//            .attr("src")
//        val title: String = data.select("h4.gridminfotitle")
//            .select("span")
//            .eq(i)
//            .text()
//        val detailUrl: String = data.select("h4.gridminfotitle")
//            .select("a")
//            .eq(i)
//            .attr("href")
//        parseItems.add(ParseItem(imgUrl, title, detailUrl))
//        Log.d("items", "img: $imgUrl . title: $title")
//    }
//} catch (e: IOException) {
//    e.printStackTrace()
//}