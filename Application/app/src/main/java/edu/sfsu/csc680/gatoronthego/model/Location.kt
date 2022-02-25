package edu.sfsu.csc680.gatoronthego.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "locations_table")
data class Location(
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0,

    @ColumnInfo(name = "name")
    var name: String = "",

    @ColumnInfo(name = "lat")
    var lat: Double = 0.0,

    @ColumnInfo(name = "lng")
    var lng: Double = 0.0,

    @ColumnInfo(name = "isFav")
    var isFav: Boolean = false
)
