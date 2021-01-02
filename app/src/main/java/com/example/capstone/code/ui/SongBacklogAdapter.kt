package com.example.capstone.code.ui

import android.annotation.SuppressLint
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.view.isVisible
import androidx.navigation.fragment.NavHostFragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.example.capstone.R
import com.example.capstone.code.model.Song

class SongBacklogAdapter(private val songList: List<Song>, val clickListener: (Song) -> Unit) :
    RecyclerView.Adapter<SongBacklogAdapter.ViewHolder>() {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): SongBacklogAdapter.ViewHolder {
        return ViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.item_song, parent, false)
        )
    }

    override fun onBindViewHolder(holder: SongBacklogAdapter.ViewHolder, position: Int) {
        holder.bind(songList[position], clickListener)
    }

    override fun getItemCount(): Int = songList.size

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        @SuppressLint("CutPasteId")
        fun bind(song: Song, clickListener: (Song) -> Unit) {
            itemView.findViewById<TextView>(R.id.tv_name).text = song.name
            itemView.findViewById<TextView>(R.id.tv_artist).text = song.artist
            itemView.findViewById<TextView>(R.id.tv_platform).text = song.platform
            itemView.findViewById<CardView>(R.id.cardView).setOnClickListener {
                clickListener(song)
            }

//                itemView.findViewById<Button>(R.id.btn_play_song).setOnClickListener {
//                    findNavController(AddSongFragment).navigate(R.id.action_addSongFragment_to_songBacklogFragment)
//                }

            // Switch to playback fragment when the play button has been clicked
            itemView.findViewById<Button>(R.id.btn_play_song).setOnClickListener { v ->
                val activity = v!!.context as AppCompatActivity
                val playbackFragment = PlaybackFragment()
                activity.supportFragmentManager.beginTransaction()
                    .replace(R.id.songBacklogFragment, playbackFragment).addToBackStack(null)
                    .commit()
            }

            when (song.platform) {
                "Youtube" -> {
                    itemView.findViewById<TextView>(R.id.tv_platform)
                        .setCompoundDrawablesRelativeWithIntrinsicBounds(
                            0,
                            0,
                            R.drawable.ic_youtube,
                            0
                        )
                    itemView.findViewById<Button>(R.id.btn_play_song).text = "Play video"
                }
                "Soundcloud" -> {
                    itemView.findViewById<TextView>(R.id.tv_platform)
                        .setCompoundDrawablesRelativeWithIntrinsicBounds(
                            0,
                            0,
                            R.drawable.ic_soundcloud,
                            0
                        )
                    itemView.findViewById<Button>(R.id.btn_play_song).isVisible = false
                }
                else -> {
                    itemView.findViewById<TextView>(R.id.tv_platform)
                        .setCompoundDrawablesRelativeWithIntrinsicBounds(
                            0,
                            0,
                            R.drawable.ic_spotify,
                            0
                        )
                }
            }
                }
            }
        }


