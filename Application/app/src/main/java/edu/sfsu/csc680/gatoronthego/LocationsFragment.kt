package edu.sfsu.csc680.gatoronthego

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.DialogInterface
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.activityViewModels
import androidx.navigation.findNavController
import com.google.android.material.snackbar.Snackbar
import edu.sfsu.csc680.gatoronthego.adapter.LocationsAdapter
import edu.sfsu.csc680.gatoronthego.database.LocationApplication
import edu.sfsu.csc680.gatoronthego.databinding.FragmentLocationsListBinding
import edu.sfsu.csc680.gatoronthego.model.Location
import edu.sfsu.csc680.gatoronthego.viewmodel.LocationViewModel
import edu.sfsu.csc680.gatoronthego.viewmodel.LocationViewModelFactory

class LocationsFragment : Fragment() {
    private lateinit var adapter: LocationsAdapter
    private lateinit var binding: FragmentLocationsListBinding
    private val viewModel: LocationViewModel by activityViewModels {
        LocationViewModelFactory((requireActivity().application as LocationApplication).repository)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_locations_list, container, false
        )

        // Set up search view
        setUpSearchView(binding.swLocations)

        // Set up recycler view
        setUpAdapter()

        return binding.root
    }

    private fun setUpSearchView(swLocations: SearchView) {
        swLocations.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                adapter.filter.filter(newText)
                return false
            }
        })
    }

    private var clickListenerImpl = object : LocationsAdapter.OnClickListener {
        override fun onNameClick(location: Location) {
            requireView().findNavController().navigate(
                LocationsFragmentDirections.actionLocationsToMap()
                    .setLat(location.lat.toFloat())
                    .setLng(location.lng.toFloat())
                    .setName(location.name)
            )
        }

        @SuppressLint("NotifyDataSetChanged")
        override fun onNameLongClick(location: Location) {
            val dialog =
                AlertDialog.Builder(requireContext())
                    .setTitle("Delete this location?")
                    .setNegativeButton("Cancel", null)
                    .setPositiveButton("Yes", null)
                    .show()
            dialog.getButton(DialogInterface.BUTTON_POSITIVE).setOnClickListener {
                if (location.id > 68) {
                    viewModel.delete(location)
                    adapter.notifyItemRemoved(location.id)
                    Snackbar.make(requireView(), "Delete success.", Snackbar.LENGTH_SHORT).show()
                } else {
                    Snackbar.make(
                        requireView(),
                        "Cannot delete default locations.",
                        Snackbar.LENGTH_SHORT
                    ).show()
                }
                dialog.dismiss()
            }
        }
    }

    private fun setUpAdapter() {
        viewModel.allLocations.observe(viewLifecycleOwner, {
            adapter = LocationsAdapter(it, viewModel, clickListenerImpl)
            binding.rvLocations.adapter = adapter
        })
    }
}