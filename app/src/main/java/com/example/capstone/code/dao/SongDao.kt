package com.example.capstone.code.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.example.capstone.code.model.Song

@Dao
interface SongDao {

    @Query("SELECT * FROM song_table")
    fun getAllSongs(): LiveData<List<Song>>

    @Insert
    suspend fun insertSong(song: Song)

    @Delete
    suspend fun deleteSong(song: Song)

    @Query("DELETE FROM song_table")
    suspend fun deleteAllSongs()

}