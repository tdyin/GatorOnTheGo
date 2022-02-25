package edu.sfsu.csc680.gatoronthego.adapter

import android.annotation.SuppressLint
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.*
import edu.sfsu.csc680.gatoronthego.model.Location
import edu.sfsu.csc680.gatoronthego.databinding.FragmentLocationsItemBinding
import edu.sfsu.csc680.gatoronthego.viewmodel.LocationViewModel
import java.util.*
import kotlin.collections.ArrayList

class LocationsAdapter(
    private val locationsList: List<Location>,
    private val viewModel: LocationViewModel,
    private val onClickListener: OnClickListener,
) : RecyclerView.Adapter<LocationsAdapter.ViewHolder>(),
    Filterable {

    // Implement click functions in fragment
    interface OnClickListener {
        fun onNameClick(location: Location)
        fun onNameLongClick(location: Location)
    }

    private var filterList = locationsList

    override fun getItemCount(): Int = filterList.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            FragmentLocationsItemBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, index: Int) {
        val location = filterList[index]
        // Set view
        holder.name.text = location.name
        holder.toggle.isChecked = location.isFav
        // Set name click listener
        holder.name.setOnClickListener {
            onClickListener.onNameClick(location)
        }
        holder.name.setOnLongClickListener {
            onClickListener.onNameLongClick(location)
            true
        }
        // Set toggle button listener
        holder.toggle.setOnCheckedChangeListener { _, _ ->
            if (holder.toggle.isPressed) {
                location.isFav = !location.isFav
                viewModel.update(location)
                notifyItemChanged(index)
            }
        }
    }

    inner class ViewHolder(binding: FragmentLocationsItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        val name: TextView = binding.name
        val toggle: ToggleButton = binding.toggle
    }

    // Search view filter logic
    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val charSearch = constraint.toString()
                filterList = if (charSearch.isEmpty()) {
                    locationsList
                } else {
                    val resultList = ArrayList<Location>()
                    for (row in locationsList) {
                        if (row.name.lowercase(Locale.ROOT)
                                .contains(charSearch.lowercase(Locale.ROOT))
                        ) {
                            resultList.add(row)
                        }
                    }
                    resultList
                }
                val filterResults = FilterResults()
                filterResults.values = filterList
                return filterResults
            }

            @SuppressLint("NotifyDataSetChanged")
            @Suppress("UNCHECKED_CAST")
            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                filterList = results?.values as List<Location>
                notifyDataSetChanged()
            }
        }
    }
}