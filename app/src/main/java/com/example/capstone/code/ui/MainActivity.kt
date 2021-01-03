package com.example.capstone.code.ui

import android.os.Bundle
import android.util.Log
import android.view.Menu
import androidx.appcompat.app.AppCompatActivity
import com.example.capstone.BuildConfig
import com.example.capstone.R
import com.spotify.android.appremote.api.ConnectionParams
import com.spotify.android.appremote.api.Connector
import com.spotify.android.appremote.api.SpotifyAppRemote

class MainActivity : AppCompatActivity()  {

    private val SPOTIFY_CLIENT_ID = BuildConfig.SpotifyClientId
    private val SPOTIFY_CLIENT_SECRET = BuildConfig.SpotifyClientSecret

    private lateinit var mSpotifyAppRemote: SpotifyAppRemote

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(findViewById(R.id.toolbar))
    }

    override fun onStart() {
        super.onStart()

        val redirectURI = "http://localhost:8080/callback"

        // Setup the connection parameters
        val connectionParams = ConnectionParams.Builder(SPOTIFY_CLIENT_ID)
            .setRedirectUri(redirectURI)
            .showAuthView(true)
            .build()

        // Connect to Spotify
        SpotifyAppRemote.connect(this, connectionParams,
           object:Connector.ConnectionListener {

                override fun onConnected(spotifyAppRemote: SpotifyAppRemote?) {
                    if (spotifyAppRemote != null) {
                        mSpotifyAppRemote = spotifyAppRemote
                    }

                    Log.d("Main activity", "Spotify is connected")

                    // Start interacting with app remote
                    connected()
                }

               override fun onFailure(throwable: Throwable) {
                   Log.e("MainActivity", throwable.message, throwable)
               }
           });
    }

    override fun onStop() {
        super.onStop()

        SpotifyAppRemote.disconnect(mSpotifyAppRemote)
    }

    private fun connected() {
        // Play a playlist
//        mSpotifyAppRemote.playerApi.play("spotify:playlist:37i9dQZF1DX2sUQwD7tbmL")
        mSpotifyAppRemote.playerApi.play("spotify:track:3dp4LGJNWdMGqF2aPeyiwl")
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

}