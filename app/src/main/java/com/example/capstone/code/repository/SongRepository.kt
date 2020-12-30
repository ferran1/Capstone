package com.example.capstone.code.repository

import android.content.Context
import androidx.lifecycle.LiveData
import com.example.capstone.code.dao.SongDao
import com.example.capstone.code.db.SongRoomDatabase
import com.example.capstone.code.model.Song

class SongRepository(context: Context) {

    private val songDao: SongDao

    init {
        val database = SongRoomDatabase.getDatabase(context)
        songDao = database!!.songDao()
    }

    fun getSongList(): LiveData<List<Song>> {
        return songDao.getAllSongs()
    }

    suspend fun getCount(url: String): Int {
        return songDao.count(url)
    }

    suspend fun insertSong(song: Song) {
        songDao.insertSong(song)
    }

    suspend fun deleteSong(song: Song) {
        songDao.deleteSong(song)
    }

    suspend fun deleteAllSongs() {
        songDao.deleteAllSongs()
    }

}
