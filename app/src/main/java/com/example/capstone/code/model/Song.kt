package com.example.capstone.code.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "song_table")
class Song (

        val name: String,
        val artist: String,
        val platform: String,

        @PrimaryKey(autoGenerate = true)
        val id: Long? = null
)