package com.example.capstone.code.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "song_table")
class Song (

        @ColumnInfo(name = "url") val url : String,
        @ColumnInfo(name = "name") val name: String,
        @ColumnInfo(name = "artist") val artist: String,
        @ColumnInfo(name = "platform") val platform: String,

        @PrimaryKey(autoGenerate = true)
        val id: Long? = null
)