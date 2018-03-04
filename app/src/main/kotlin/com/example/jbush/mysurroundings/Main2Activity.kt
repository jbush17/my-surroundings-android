package com.example.jbush.mysurroundings

import android.Manifest
import android.content.pm.PackageManager
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.design.widget.NavigationView
import android.support.v4.app.ActivityCompat
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.support.v4.view.GravityCompat
import android.view.MenuItem
import kotlinx.android.synthetic.main.activity_main2.*
import android.content.IntentSender
import android.util.Log
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*





class Main2Activity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    private val PERMISSION_REQUEST_CODE = 12345
    private val REQUEST_CHECK_SETTINGS = 2345

    private var surroundingsFragment = MySurroundingsFragment ()
    private var settingsFragment = SettingsFragment ()

    private var mFusedLocationClient : FusedLocationProviderClient? = null
    private var mLocationRequest : LocationRequest? = null
    private var mLocationCallback = MyLocationCallback ()


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

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        mLocationRequest = LocationRequest()
        mLocationRequest?.interval = 1000
        mLocationRequest?.fastestInterval = 1000
        mLocationRequest?.priority = LocationRequest.PRIORITY_HIGH_ACCURACY

        Log.v(Main2Activity::class.simpleName, "Exiting onCreate()")
    }

    /**
     * onResume
     *
     */
    override fun onResume() {
        super.onResume()
        Log.v(Main2Activity::class.simpleName, "Enterring onResume()")

        // CHeck permissions for GPS
        startLocationUpdates()

        Log.v(Main2Activity::class.simpleName, "Exiting onResume()")
    }

    /**
     * onPause
     *
     */
    override fun onPause() {
        super.onPause()
        Log.v(Main2Activity::class.simpleName, "Enterring onPause()")
        stopLocationUpdates()
        Log.v(Main2Activity::class.simpleName, "Exiting onPause()")
    }

    /**
     * checkGPSPermissions
     *
     * Returns true if we had to prompt the User for permissions and false if permissions were acceptable
     *
     */
    fun checkGPSPermissions () : Boolean {
        Log.v(Main2Activity::class.simpleName, "Checking permissions")
        var promptedUser = false
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED) {
            Log.v(Main2Activity::class.simpleName, "Requesting appropriate permissions from user")
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), PERMISSION_REQUEST_CODE)
            promptedUser = true
        } else {
            Log.v(Main2Activity::class.simpleName, "Permissions were acceptable")
        }
        return promptedUser
    }

    /**
     * configureLocationSettings
     *
     * Configures the device's location settings
     *
     */
    fun configureLocationSettings() {
        val builder = LocationSettingsRequest.Builder()
                .addLocationRequest(mLocationRequest!!)
        val client = LocationServices.getSettingsClient(this)
        val task = client.checkLocationSettings(builder.build())

        task.addOnSuccessListener(this) {
            // All location settings are satisfied. The client can initialize
            // location requests here.
        }

        task.addOnFailureListener(this) { e ->
            if (e is ResolvableApiException) {
                // Location settings are not satisfied, but this can be fixed
                // by showing the user a dialog.
                try {
                    // Show the dialog by calling startResolutionForResult(),
                    // and check the result in onActivityResult().
                    e.startResolutionForResult(this@Main2Activity,
                            REQUEST_CHECK_SETTINGS)
                } catch (sendEx: IntentSender.SendIntentException) {
                    // Ignore the error.
                }

            }
        }
    }

    /**
     * startLocationUpdtes
     *
     * Starts location updates if permissions are gratned appropriately
     *
     */
    fun startLocationUpdates () {
        val promptedUser = checkGPSPermissions()
        if (!promptedUser) {
            Log.v(Main2Activity::class.simpleName, "Starting location updates")
            configureLocationSettings()
            mFusedLocationClient?.requestLocationUpdates(mLocationRequest, mLocationCallback, null)
        } else {
            Log.v(Main2Activity::class.simpleName, "Had to prompt the User for permissions for location updates.  Not starting now")
        }
    }

    /**
     * Stops location updates
     *
     */
    fun stopLocationUpdates () {
        if (mLocationCallback != null) {
            Log.v(Main2Activity::class.simpleName, "Stopping location updates")
            mFusedLocationClient?.removeLocationUpdates(mLocationCallback)
        }
    }


    /**
     * onRequestPermissionsResult
     *
     * Called any time a permission was requested, once the user has accepted/denied the permission
     *
     */
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            PERMISSION_REQUEST_CODE -> {
                if (grantResults.isNotEmpty() && grantResults.first() == PackageManager.PERMISSION_GRANTED){
                    // permissions were granted for location
                    Log.v(Main2Activity::class.simpleName, "Permissions granted for GPS")
                    startLocationUpdates()
                } else {
                    // permissions weren't granted
                    Log.v(Main2Activity::class.simpleName, "Permissions weren't granted for GPS")
                }
                return
            }
            else -> {
                // ignored
            }
        }
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
     * onLocationResult
     *
     */
    fun onLocationResult (locationResult: LocationResult) {
        surroundingsFragment.onLocationUpdate(locationResult)
    }

    /**
     * onLocationAvailability
     *
     */
    fun onLocationAvailability (locationAvailability: LocationAvailability?) {

    }

    /**
     * Location Callback class.  Delegates back to Main2Activity
     *
     */
    inner class MyLocationCallback : LocationCallback () {
        override fun onLocationResult (locationResult: LocationResult){
            super.onLocationResult(locationResult)
            this@Main2Activity.onLocationResult(locationResult)
        }

        override fun onLocationAvailability(locationAvailability: LocationAvailability?) {
            super.onLocationAvailability(locationAvailability)
            this@Main2Activity.onLocationAvailability(locationAvailability)
        }
    }

}
