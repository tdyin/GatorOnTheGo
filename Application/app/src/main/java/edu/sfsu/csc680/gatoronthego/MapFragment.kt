package edu.sfsu.csc680.gatoronthego

import android.Manifest
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.DialogInterface
import android.content.pm.PackageManager
import androidx.fragment.app.Fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.navArgs
import androidx.preference.PreferenceManager
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.*
import com.google.android.material.snackbar.Snackbar

import edu.sfsu.csc680.gatoronthego.database.LocationApplication
import edu.sfsu.csc680.gatoronthego.model.Location
import edu.sfsu.csc680.gatoronthego.viewmodel.LocationViewModel
import edu.sfsu.csc680.gatoronthego.viewmodel.LocationViewModelFactory


class MapFragment : Fragment() {

    private val viewModel: LocationViewModel by activityViewModels {
        LocationViewModelFactory((requireActivity().application as LocationApplication).repository)
    }

    private val args: MapFragmentArgs by navArgs()
    private val sfsu = LatLng(37.72367, -122.47899)
    private val schoolBounds = LatLngBounds(
        LatLng(37.72012, -122.48561),  // SW bounds
        LatLng(37.72661, -122.47477) // NE bounds
    )

    private val callback = OnMapReadyCallback { map ->

        // ----------------------------------
        // This part is for setting the map
        // ----------------------------------

        // Request and enable user's location
        enableMyLocation(map)

        val preferences = PreferenceManager.getDefaultSharedPreferences(requireContext())
        val mapStyle = Integer.parseInt(preferences.getString("map_style", "0"))

        // Custom map style
        map.setMapStyle(
            MapStyleOptions.loadRawResourceStyle(requireContext(), mapStyles[mapStyle])
        )

        // Enable zoom buttons
        map.uiSettings.isZoomControlsEnabled = true

        // Set the camera zoom within a range
        map.setMinZoomPreference(16.0f)
        map.setMaxZoomPreference(20.0f)

        // Constrain the camera target to the school bounds.
        map.setLatLngBoundsForCameraTarget(schoolBounds)

        // Center the the camera in the bound
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(sfsu, 16f))


        // ---------------------------------------
        // This part is for interact with the map
        // ---------------------------------------


        // When args are not default, put a maker and move the camera
        if (args.lat != 0.0f) {
            val latlng = LatLng(args.lat.toDouble(), args.lng.toDouble())
            map.addMarker(
                MarkerOptions()
                    .position(latlng)
                    .title(args.name)
            )
            map.animateCamera(CameraUpdateFactory.newLatLngZoom(latlng, 17.5f))
        }

        // Long click to create a location
        map.setOnMapLongClickListener { latlng ->
            showCreateDialog(latlng, map)
        }

        // Click title to add location to database
        map.setOnInfoWindowClickListener { marker ->
            showAddDialog(markerToLocation(marker))
        }

    }

    private fun showCreateDialog(latLng: LatLng, map: GoogleMap) {
        val createLocationView =
            LayoutInflater.from(requireContext())
                .inflate(R.layout.form_create_location, null)
        val dialog =
            AlertDialog.Builder(requireContext())
                .setTitle("Create My Location")
                .setView(createLocationView)
                .setNegativeButton("Cancel", null)
                .setPositiveButton("Ok", null)
                .show()
        dialog.getButton(DialogInterface.BUTTON_POSITIVE).setOnClickListener {
            val name = createLocationView.findViewById<EditText>(R.id.location_name).text.toString()
            if (name.trim().isEmpty()) {
                Snackbar.make(requireView(), "Please give it a name.", Snackbar.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            map.addMarker(
                MarkerOptions()
                    .position(latLng)
                    .title(name)
                    .draggable(true)
            )
            dialog.dismiss()
        }
    }

    private fun showAddDialog(location: Location) {
        val dialog =
            AlertDialog.Builder(requireContext())
                .setTitle("Add this location to Locations?")
                .setNegativeButton("Cancel", null)
                .setPositiveButton("Yes", null)
                .show()
        dialog.getButton(DialogInterface.BUTTON_POSITIVE).setOnClickListener {
            viewModel.insert(location)
            Snackbar.make(requireView(), "Added to locations.", Snackbar.LENGTH_SHORT).show()
            dialog.dismiss()
        }
    }

    private fun markerToLocation(marker: Marker): Location {
        return Location(
            name = marker.title!!,
            lat = marker.position.latitude,
            lng = marker.position.longitude
        )
    }


    private fun isPermissionGranted(): Boolean {
        return ContextCompat.checkSelfPermission(
            this.requireContext(),
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    }

    @SuppressLint("MissingPermission")
    private fun enableMyLocation(map: GoogleMap) {
        if (isPermissionGranted()) {
            map.isMyLocationEnabled = true
        } else {
            ActivityCompat.requestPermissions(
                this.requireActivity(),
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                LOCATION_PERMISSION_REQUEST_CODE
            )
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_map, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment?.getMapAsync(callback)
    }

    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1
        val mapStyles = arrayOf(
            R.raw.map_style_default,
            R.raw.map_style_night,
            R.raw.map_style_retr,
            R.raw.map_style_pale
        )
    }

}

