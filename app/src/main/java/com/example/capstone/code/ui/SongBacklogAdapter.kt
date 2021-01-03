package com.example.capstone.code.ui

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.example.capstone.BuildConfig
import com.example.capstone.R
import com.example.capstone.code.model.Song
import com.spotify.android.appremote.api.ConnectionParams
import com.spotify.android.appremote.api.Connector
import com.spotify.android.appremote.api.SpotifyAppRemote
import java.net.ConnectException

class SongBacklogAdapter(
    val context: Context,
    private val songList: List<Song>,
    val clickListener: (Song) -> Unit
) :
    RecyclerView.Adapter<SongBacklogAdapter.ViewHolder>() {

    private val SPOTIFY_CLIENT_ID = BuildConfig.SpotifyClientId
    private lateinit var mSpotifyAppRemote: SpotifyAppRemote

    private lateinit var mContext: Context

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

            // Play the Spotify song once the "Play song" button has been clicked
            itemView.findViewById<Button>(R.id.btn_play_song).setOnClickListener { v ->
                val redirectURI = "http://localhost:8080/callback"

                val connectionParams = ConnectionParams.Builder(SPOTIFY_CLIENT_ID)
                    .setRedirectUri(redirectURI)
                    .showAuthView(true)
                    .build()

                mContext = context

                // Connect to Spotify
                SpotifyAppRemote.connect(mContext, connectionParams,
                    object : Connector.ConnectionListener {

                        override fun onConnected(spotifyAppRemote: SpotifyAppRemote?) {
                            if (spotifyAppRemote != null) {
                                mSpotifyAppRemote = spotifyAppRemote
                            }

                            Log.d("SPOTIFY", "Spotify is connected")

                            val songId = song.url.substring(31, 53)

                            val uri = "spotify:track:$songId"

                            // Start interacting with app remote
                            connected(uri)
                        }

                        override fun onFailure(throwable: Throwable) {
                            Log.e("MainActivity", throwable.message, throwable)
                        }
                    });
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

        /**
         * Plays the Spotify song using the SDK
         * @param spotifyID is the song URI that needs to be played
         */
        private fun connected(uri: String) {
            mSpotifyAppRemote.playerApi.play(uri)
        }
    }
}



