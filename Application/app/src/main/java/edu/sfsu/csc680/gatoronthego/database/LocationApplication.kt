package edu.sfsu.csc680.gatoronthego.database

import android.app.Application

class LocationApplication: Application() {
    val database by lazy { LocationDatabase.getDatabase(this) }
    val repository by lazy { LocationRepository(database.locationDao()) }
}