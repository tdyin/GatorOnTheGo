package edu.sfsu.csc680.gatoronthego

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatDelegate
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.onNavDestinationSelected
import androidx.navigation.ui.setupWithNavController
import androidx.preference.PreferenceManager
import edu.sfsu.csc680.gatoronthego.databinding.ActivityMainBinding


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val sp = PreferenceManager.getDefaultSharedPreferences(this)
        val isDarkModeOn = sp.getBoolean("dark_mode", false)
        if (isDarkModeOn) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
        }
        setContentView(binding.root)

        // Setup toolbar
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)

        // Setup nav controller
        val host: NavHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host) as NavHostFragment? ?: return
        val navController = host.navController

        // Setup Bottom nav
        setupBottomNavMenu(navController)

    }

    // Create toolbar
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return true
    }

    // Toolbar nav controller
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return item.onNavDestinationSelected(findNavController(R.id.nav_host))
                || super.onOptionsItemSelected(item)
    }

    // Bottom nav controller
    private fun setupBottomNavMenu(navController: NavController) {
        val bottomNav = binding.bottomNavigationView
        bottomNav.setupWithNavController(navController)
    }

}