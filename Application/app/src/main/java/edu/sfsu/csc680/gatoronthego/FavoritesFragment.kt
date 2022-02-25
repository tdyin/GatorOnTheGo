package edu.sfsu.csc680.gatoronthego

import android.app.AlertDialog
import android.content.DialogInterface
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.activityViewModels
import androidx.navigation.findNavController
import com.google.android.material.snackbar.Snackbar
import edu.sfsu.csc680.gatoronthego.adapter.LocationsAdapter
import edu.sfsu.csc680.gatoronthego.database.LocationApplication
import edu.sfsu.csc680.gatoronthego.databinding.FragmentLocationsListBinding
import edu.sfsu.csc680.gatoronthego.model.Location
import edu.sfsu.csc680.gatoronthego.viewmodel.LocationViewModel
import edu.sfsu.csc680.gatoronthego.viewmodel.LocationViewModelFactory

class FavoritesFragment : Fragment() {


    private lateinit var adapter: LocationsAdapter
    private lateinit var binding: FragmentLocationsListBinding
    private val viewModel: LocationViewModel by activityViewModels {
        LocationViewModelFactory((requireActivity().application as LocationApplication).repository)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentLocationsListBinding.inflate(inflater, container, false)
        setUpSearchView(binding.swLocations)
        setUpAdapter()
        return binding.root
    }

    private var clickListenerImpl = object : LocationsAdapter.OnClickListener {
        override fun onNameClick(location: Location) {
            val lat = location.lat.toFloat()
            val lng = location.lng.toFloat()
            val name = location.name
            requireView().findNavController().navigate(
                FavoritesFragmentDirections.actionFavoritesToMap()
                    .setLat(lat)
                    .setLng(lng)
                    .setName(name)
            )
        }

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
                    Snackbar.make(requireView(), "Delete success.", Snackbar.LENGTH_SHORT).show()
                    adapter.notifyItemRemoved(location.id)
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

    private fun changeTextViewVisibility(size: Int) {
        when (size) {
            0 -> binding.reminder.visibility = View.VISIBLE
            else -> binding.reminder.visibility = View.GONE
        }
    }

    private fun setUpAdapter() {
        viewModel.allLocations.observe(viewLifecycleOwner, {
            val favLocations = it.filter { location -> location.isFav }
            adapter = LocationsAdapter(favLocations, viewModel, clickListenerImpl)
            binding.rvLocations.adapter = adapter
            changeTextViewVisibility(favLocations.size)
        })
    }

}