package com.example.capstone.code.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import com.example.capstone.code.model.Song
import com.example.capstone.code.repository.SongRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SongViewModel(application: Application) : AndroidViewModel(application) {
    
    private val repository = SongRepository(application.applicationContext)

    private val mainScope = CoroutineScope(Dispatchers.Main)

    val songList: LiveData<List<Song>> = repository.getSongList()

    fun insertSong(url: String, name: String, artist: String, platform: String): Boolean {

        mainScope.launch {
            // TODO: Try to display a toast message if song already exists
            // Check if song already exists
            if (repository.getCount(url) > 0) {
                Log.d("Exists", "Song already exists in the database")
                //Toast.makeText(context, getString(R.string.song_exists_msg), Toast.LENGTH_LONG)
                //.show()
            } else {
                val song = Song(
                    url = url,
                    name = name,
                    artist = artist,
                    platform = platform
                )
                withContext(Dispatchers.IO) {
                    repository.insertSong(song)
                }
            }
        }
        return true
    }

    fun deleteAllSongs() {
        mainScope.launch {
            withContext(Dispatchers.IO) {
                repository.deleteAllSongs()
            }
        }
    }

    fun deleteSong(song: Song) {
        mainScope.launch {
            withContext(Dispatchers.IO) {
                repository.deleteSong(song)
            }
        }
    }

}