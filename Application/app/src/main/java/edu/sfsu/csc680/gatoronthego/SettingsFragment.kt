package edu.sfsu.csc680.gatoronthego

import android.app.AlertDialog
import android.content.DialogInterface
import android.os.Bundle
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.activityViewModels
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.PreferenceManager
import androidx.preference.SwitchPreference
import com.google.android.material.snackbar.Snackbar
import edu.sfsu.csc680.gatoronthego.R
import edu.sfsu.csc680.gatoronthego.database.LocationApplication
import edu.sfsu.csc680.gatoronthego.viewmodel.LocationViewModel
import edu.sfsu.csc680.gatoronthego.viewmodel.LocationViewModelFactory

class SettingsFragment : PreferenceFragmentCompat() {

    private val viewModel: LocationViewModel by activityViewModels {
        LocationViewModelFactory((requireActivity().application as LocationApplication).repository)
    }

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.root_preferences, rootKey)
    }

    override fun onPreferenceTreeClick(preference: Preference): Boolean {
        when (preference.key) {
            "dark_mode" -> {
                val sp = PreferenceManager.getDefaultSharedPreferences(requireContext())
                val isDarkModeOn = sp.getBoolean("dark_mode", false)
                if (isDarkModeOn) {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                } else {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
                }
            }
            "reset_favorites" -> {
                val dialog = createDialog()
                dialog.getButton(DialogInterface.BUTTON_POSITIVE).setOnClickListener {
                    viewModel.allLocations.observe(viewLifecycleOwner, {
                        for (item in it) {
                            if (item.isFav)
                                item.isFav = false
                            viewModel.update(item)
                        }
                        Snackbar.make(requireView(), "Reset complete!", Snackbar.LENGTH_SHORT).show()
                    })
                    dialog.dismiss()
                }
                return true
            }
            "reset_locations" -> {
                val dialog = createDialog()
                dialog.getButton(DialogInterface.BUTTON_POSITIVE).setOnClickListener {
                    viewModel.allLocations.observe(viewLifecycleOwner, {
                        for (item in it) {
                            if (item.id > 68) {
                                viewModel.delete(item)
                            }
                        }
                    })
                    Snackbar.make(requireView(), "Reset complete!", Snackbar.LENGTH_SHORT).show()
                    dialog.dismiss()
                }
                return true
            }

        }
        return false
    }

    private fun createDialog() = AlertDialog.Builder(requireContext())
        .setTitle("Are you sure? (Cannot undo)")
        .setNegativeButton("Cancel", null)
        .setPositiveButton("Yes", null)
        .show()
}