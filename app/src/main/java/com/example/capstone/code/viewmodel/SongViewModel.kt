package com.example.capstone.code.viewmodel

import android.app.Application
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

    fun insertSong(name: String,  artist: String, platform: String) {
        mainScope.launch {
            val song = Song(
                name = name,
                artist = artist,
                platform = platform
            )
            withContext(Dispatchers.IO) {
                repository.insertSong(song)
            }
        }
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