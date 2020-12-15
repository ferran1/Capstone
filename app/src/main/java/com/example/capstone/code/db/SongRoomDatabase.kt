package com.example.capstone.code.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.capstone.code.dao.SongDao
import com.example.capstone.code.model.Song

@Database(entities = [Song::class], version = 1, exportSchema = false)
//@TypeConverters(Converters::class)
abstract class SongRoomDatabase : RoomDatabase() {

    abstract fun songDao(): SongDao

    companion object {
        private const val DATABASE_NAME = "SONG_DATABASE"

        @Volatile
        private var songRoomDatabaseInstance: SongRoomDatabase? = null

        fun getDatabase(context: Context): SongRoomDatabase? {
            if (songRoomDatabaseInstance == null) {
                synchronized(SongRoomDatabase::class.java) {
                    if (songRoomDatabaseInstance == null) {
                        songRoomDatabaseInstance =
                                // Create the actual database
                            Room.databaseBuilder(
                                context.applicationContext,
                                SongRoomDatabase::class.java,
                                DATABASE_NAME
                            ).build()
                    }
                }
            }
            return songRoomDatabaseInstance
        }
    }

}