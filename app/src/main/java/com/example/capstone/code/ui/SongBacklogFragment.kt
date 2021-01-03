package com.example.capstone.code.ui

import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import androidx.browser.customtabs.CustomTabsIntent
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.capstone.R
import com.example.capstone.code.model.Song
import com.example.capstone.code.viewmodel.SongViewModel
import com.example.capstone.databinding.FragmentSongBacklogBinding
import com.google.android.material.snackbar.Snackbar
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.YouTubePlayerCallback
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.YouTubePlayerListener
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView

/**
 * A simple [Fragment] subclass as the default destination in the navigation.
 */
class SongBacklogFragment : Fragment() {

    private lateinit var binding: FragmentSongBacklogBinding

    private val viewModel: SongViewModel by viewModels()
    private val songList = arrayListOf<Song>()
//    private val songBacklogAdapter = SongBacklogAdapter(songList)

    private lateinit var songBacklogAdapter : SongBacklogAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        setHasOptionsMenu(true)

        container?.removeAllViews()

        binding = FragmentSongBacklogBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        requireActivity().title = getString(R.string.app_name)

        songBacklogAdapter = SongBacklogAdapter(requireActivity(), songList, ::songItemClicked)

//        val fragment = AddSongFragment()
//        supportFragmentManager.commit {
//            setCustomAnimations(
//                    enter = R.anim.enter_from_right,
//                    exit = R.anim.exit_to_right,
//            )
//            replace(R.id.fragment_container, fragment)
//            addToBackStack(null)
//        }

        binding.fabAddSong.setOnClickListener {
            findNavController().navigate(R.id.action_songBacklogFragment_to_addSongFragment)
        }

        binding.btnHowItWorks.setOnClickListener {
            findNavController().navigate(R.id.action_songBacklogFragment_to_howItWorksFragment)
        }

//        val youtubePlayerView: YouTubePlayerView = binding.youtubePlayerView
//        lifecycle.addObserver(youtubePlayerView)
//
//        youtubePlayerView.addYouTubePlayerListener(object: AbstractYouTubePlayerListener() {
//
//            override fun onReady(youTubePlayer: YouTubePlayer) {
//                super.onReady(youTubePlayer)
//
//                val id = "y3bW3H95NMw"
//
//                Log.d("ID IS ", id)
//                youTubePlayer.loadVideo(id, 0F)
//
//            }
//        })

//        youtubePlayerView.addYoutubePlayerListener(object:AbstractYoutubePlayerListener()) {
//
//        })
//        youTubePlayerView.initialize(listener: YouTubePlayerListener)


        initializeRecyclerView()

        (activity as AppCompatActivity).supportActionBar!!.setDisplayHomeAsUpEnabled(false)

        observeGameList()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        menu.findItem(R.id.action_delete_songs).isVisible = true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_delete_songs -> {
                viewModel.deleteAllSongs()
                return true
            }
            else -> {
                super.onOptionsItemSelected(item)
            }
        }
    }

    /**
     * Observe the song list and when it changes, the list will be cleared, all the songs will be added and the songs will get sorted by the platform
     */
    private fun observeGameList() {
        viewModel.songList.observe(viewLifecycleOwner, { gamesList ->
            this@SongBacklogFragment.songList.clear()
            this@SongBacklogFragment.songList.addAll(gamesList)
//            this@SongBacklogFragment.songList.sortByDescending { it.releaseDate }
            songBacklogAdapter.notifyDataSetChanged()
        })
    }

    private fun initializeRecyclerView() {

        val viewManager = LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false)

        binding.rvSongs.apply {
            setHasFixedSize(true)
            layoutManager = viewManager
            adapter = songBacklogAdapter
        }

//        binding.rvSongs.

        createItemTouchHelperSwipe().attachToRecyclerView(binding.rvSongs)
    }

    private fun createItemTouchHelperSwipe(): ItemTouchHelper {

        // Callback which is used to create the ItemTouch helper. Only enables left swipe.
        // Use ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) to also enable right swipe.
        val callback = object :
            ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {

            // Enables or Disables the ability to move items up and down.
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return false
            }

            // Callback triggered when a user swiped an item.
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.adapterPosition
                val songToDelete = songList[position]
                viewModel.deleteSong(songToDelete)
                Snackbar.make(
                    view!!,
                    getString(R.string.successfully_deleted_song),
                    Snackbar.LENGTH_LONG
                )
                    .setAction(getString(R.string.undo)) {
                        viewModel.insertSong(
                            songToDelete.url,
                            songToDelete.name,
                            songToDelete.artist,
                            songToDelete.platform,
                        )
                    }
                    .show()
            }
        }
        return ItemTouchHelper(callback)
    }

    /**
     * This method gets called when a song gets clicked to open the URL belonging to the Song entity
     */
    private fun songItemClicked(songItem: Song) {

        val builder = CustomTabsIntent.Builder()
        // modify toolbar color
        builder.setToolbarColor(ContextCompat.getColor(requireActivity(), R.color.black))
        // add share button to overflow men
        builder.addDefaultShareMenuItem()
        // show website title
        builder.setShowTitle(true)
        builder.setExitAnimations(
            requireActivity(),
            android.R.anim.fade_in,
            android.R.anim.fade_out
        )

        val customTabsIntent = builder.build()

        customTabsIntent.launchUrl(requireActivity(), Uri.parse(songItem.url))
    }


}