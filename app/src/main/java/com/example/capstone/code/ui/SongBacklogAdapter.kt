package com.example.capstone.code.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.capstone.R
import com.example.capstone.code.model.Song

class SongBacklogAdapter(private val songList: List<Song>) :
    RecyclerView.Adapter<SongBacklogAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder =
        ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_song, parent, false))

    override fun onBindViewHolder(holder: ViewHolder, position: Int) =
        holder.bind(songList[position])

    override fun getItemCount(): Int = songList.size

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(song: Song) {
            itemView.findViewById<TextView>(R.id.tv_name).text = song.name
            itemView.findViewById<TextView>(R.id.tv_artist).text = song.artist
            itemView.findViewById<TextView>(R.id.tv_platform).text = song.platform
        }
    }

}
