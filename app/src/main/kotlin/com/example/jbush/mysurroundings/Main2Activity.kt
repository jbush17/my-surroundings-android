package com.example.jbush.mysurroundings

import android.os.Bundle
import android.support.design.widget.NavigationView
import android.support.v4.app.Fragment
import android.support.v4.view.GravityCompat
import android.view.MenuItem
import kotlinx.android.synthetic.main.activity_main2.*
import android.util.Log
import com.example.jbush.mysurroundings.location.LocationAwareActivity
import com.example.jbush.mysurroundings.location.LocationWrapper



class Main2Activity : LocationAwareActivity(), NavigationView.OnNavigationItemSelectedListener {

    private var surroundingsFragment = MySurroundingsFragment ()
    private var settingsFragment = SettingsFragment ()

    /**
     * onCreate
     *
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.v(Main2Activity::class.simpleName, "Enterring onCreate()" )
        setContentView(R.layout.activity_main2)

        setSupportActionBar(toolbar)
        val actionBar = supportActionBar
        actionBar?.setDisplayHomeAsUpEnabled( true )
        actionBar?.setHomeAsUpIndicator(R.drawable.ic_menu_black_24dp)

        nav_view.setNavigationItemSelectedListener ( this )

        supportFragmentManager.beginTransaction().add(main_frame.id, surroundingsFragment)
                .addToBackStack(null)
                .commit()

        Log.v(Main2Activity::class.simpleName, "Exiting onCreate()")
    }

    /**
     * onResume
     *
     */
    override fun onResume() {
        super.onResume()
        Log.v(Main2Activity::class.simpleName, "Enterring onResume()")

        Log.v(Main2Activity::class.simpleName, "Exiting onResume()")
    }

    /**
     * onPause
     *
     */
    override fun onPause() {
        super.onPause()
        Log.v(Main2Activity::class.simpleName, "Enterring onPause()")

        Log.v(Main2Activity::class.simpleName, "Exiting onPause()")
    }

    /**
     * onNavigationItemSelected
     *
     * Called when User clicks an item in the navication drawer
     */
    override fun onNavigationItemSelected (menuItem : MenuItem) : Boolean {
        menuItem.isChecked = true
        drawer_layout.closeDrawers()
        when (menuItem.itemId) {
            (R.id.nav_surroundings) -> swapMainFragment( surroundingsFragment )
            (R.id.nav_settings) -> swapMainFragment( settingsFragment )
        }
        return true
    }

    /**
     * onOptionsItemSelected
     *
     * Called when User clicks an options item
     *
     */
    override fun onOptionsItemSelected(menuItem: MenuItem?): Boolean {
        when (menuItem?.itemId) {
            (android.R.id.home) -> drawer_layout.openDrawer(GravityCompat.START) // "Home" options item opens the navigation drawer
            else -> super.onOptionsItemSelected(menuItem)
        }
        return return true
    }

    /**
     * swapMainFragment
     *
     * Swaps the main fragment
     *
     */
    fun swapMainFragment (fragment : Fragment) {
        supportFragmentManager.beginTransaction().replace(main_frame.id, fragment)
                .addToBackStack(null)
                .commit()
    }

    /**
     * onLocationUpdate
     *
     */
    override fun onLocationUpdate (locationWrapper: LocationWrapper) {
        surroundingsFragment.onLocationUpdate(locationWrapper)
    }

}
