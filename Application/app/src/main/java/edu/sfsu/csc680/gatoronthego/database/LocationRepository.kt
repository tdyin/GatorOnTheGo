package edu.sfsu.csc680.gatoronthego.database

import edu.sfsu.csc680.gatoronthego.model.Location
import kotlinx.coroutines.flow.Flow

class LocationRepository(private val locationDao: LocationDao) {

    val allLocations: Flow<List<Location>> = locationDao.getAll()

    suspend fun insert(location: Location) {
        locationDao.insert(location)
    }

    suspend fun update(location: Location) {
        locationDao.update(location)
    }

    suspend fun delete(location: Location) {
        locationDao.delete(location)
    }
}