package com.example.musicalgames.games;

import android.content.Context
import androidx.room.Database;
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.musicalgames.games.flappy.level_list.DatabaseLevel
import com.example.musicalgames.games.flappy.level_list.Level
import com.example.musicalgames.games.flappy.level_list.LevelDao

@Database(entities = [DatabaseLevel::class, HighScore::class], version = 1, exportSchema = false)
abstract class GameDatabase : RoomDatabase() {
    abstract fun highScoreDao(): HighScoreDao
    abstract fun levelDao(): LevelDao

    companion object {
        @Volatile
        private var INSTANCE: GameDatabase? = null

        fun getDatabase(context: Context): GameDatabase {
            return INSTANCE?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    GameDatabase::class.java,
                    "game_database"
                ).build()
                INSTANCE=instance
                instance
            }
        }
    }

}