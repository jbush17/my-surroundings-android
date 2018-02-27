package com.example.jbush.mysurroundings

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.design.widget.NavigationView
import android.support.v4.app.Fragment
import android.support.v4.view.GravityCompat
import android.view.MenuItem
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import kotlinx.android.synthetic.main.activity_main2.*

class Main2Activity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    private var surroundingsFragment = MySurroundingsFragment ()
    private var settingsFragment = SettingsFragment ()
    private var mFusedLocationClient : FusedLocationProviderClient? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main2)

        setSupportActionBar(toolbar)
        val actionBar = supportActionBar
        actionBar?.setDisplayHomeAsUpEnabled( true )
        actionBar?.setHomeAsUpIndicator(R.drawable.ic_menu_black_24dp)

        nav_view.setNavigationItemSelectedListener ( this )

        supportFragmentManager.beginTransaction().add(main_frame.id, surroundingsFragment)
                .addToBackStack(null)
                .commit()


        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        //mFusedLocationClient.requestLocationUpdates()
    }

    override fun onNavigationItemSelected (menuItem : MenuItem) : Boolean {
        menuItem.isChecked = true
        drawer_layout.closeDrawers()
        when (menuItem.itemId) {
            (R.id.nav_surroundings) -> swapMainFragment( surroundingsFragment )
            (R.id.nav_settings) -> swapMainFragment( settingsFragment )
        }
        return true
    }

    override fun onOptionsItemSelected(menuItem: MenuItem?): Boolean {
        when (menuItem?.itemId) {
            (android.R.id.home) -> drawer_layout.openDrawer(GravityCompat.START)
            else -> super.onOptionsItemSelected(menuItem)
        }
        return return true
    }

    fun swapMainFragment (fragment : Fragment) {
        supportFragmentManager.beginTransaction().replace(main_frame.id, fragment)
                .addToBackStack(null)
                .commit()
    }

}
