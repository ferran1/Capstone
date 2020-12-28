package com.example.capstone.code.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.example.capstone.R
import com.example.capstone.code.model.Song
import com.example.capstone.databinding.ItemSongBinding

class SongBacklogAdapter(private val songList: List<Song>, val clickListener: (Song) -> Unit)
    : RecyclerView.Adapter<SongBacklogAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SongBacklogAdapter.ViewHolder {
        return ViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.item_song, parent, false)
        )
    }

    override fun onBindViewHolder(holder: SongBacklogAdapter.ViewHolder, position: Int) {
        holder.bind(songList[position], clickListener)
    }

    override fun getItemCount(): Int = songList.size

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(song: Song, clickListener: (Song) -> Unit) {
            itemView.findViewById<TextView>(R.id.tv_name).text = song.name
            itemView.findViewById<TextView>(R.id.tv_artist).text = song.artist
            itemView.findViewById<TextView>(R.id.tv_platform).text = song.platform
            itemView.findViewById<CardView>(R.id.cardView).setOnClickListener {
                    clickListener(song)
            }

            if (song.platform == "Youtube"){
                itemView.findViewById<Button>(R.id.btn_play_song).isVisible = false
            }
        }
    }
}
