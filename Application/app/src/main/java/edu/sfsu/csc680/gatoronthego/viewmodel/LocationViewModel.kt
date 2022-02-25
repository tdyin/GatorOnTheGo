package edu.sfsu.csc680.gatoronthego.viewmodel

import androidx.lifecycle.*
import edu.sfsu.csc680.gatoronthego.model.Location
import edu.sfsu.csc680.gatoronthego.database.LocationRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class LocationViewModel(
    private val repository: LocationRepository
) : ViewModel() {

    // For interacting with the map
    private val _location = MutableLiveData<Location>()
    val location: LiveData<Location> = _location

    // Database
    // For retrieving all locations from database
    val allLocations: LiveData<List<Location>> = repository.allLocations.asLiveData()

    fun insert(location: Location) = viewModelScope.launch {
        repository.insert(location)
    }

    fun update(location: Location) = viewModelScope.launch {
        repository.update(location)
    }

    fun delete(location: Location) = viewModelScope.launch {
        repository.delete(location)
    }
}

class LocationViewModelFactory(private val repository: LocationRepository): ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(LocationViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return LocationViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }

}
