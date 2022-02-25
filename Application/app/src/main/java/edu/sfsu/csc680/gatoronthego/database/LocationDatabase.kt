package edu.sfsu.csc680.gatoronthego.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import edu.sfsu.csc680.gatoronthego.model.Location

@Database(entities = [Location::class], version = 1, exportSchema = true)
abstract class LocationDatabase : RoomDatabase() {

    abstract fun locationDao(): LocationDao

    companion object {
        // Singleton prevents multiple instances of database opening at the
        // same time.
        @Volatile
        private var INSTANCE: LocationDatabase? = null

        fun getDatabase(
            context: Context,
        ): LocationDatabase {
            // if the INSTANCE is not null, then return it,
            // if it is, then create the database
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    LocationDatabase::class.java,
                    "locations_db"
                )
                    .createFromAsset("database/locations.db")
                    .build()
                INSTANCE = instance
                // return instance
                instance
            }
        }
    }
}