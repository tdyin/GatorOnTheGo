package edu.sfsu.csc680.gatoronthego.database

import androidx.room.*
import edu.sfsu.csc680.gatoronthego.model.Location
import kotlinx.coroutines.flow.Flow

@Dao
interface LocationDao {
    @Insert
    suspend fun insert(location: Location)

    @Update
    suspend fun update(location: Location)

    @Delete
    suspend fun delete(location: Location)

    @Query("SELECT * FROM locations_table ORDER BY name")
    fun getAll(): Flow<List<Location>>
}