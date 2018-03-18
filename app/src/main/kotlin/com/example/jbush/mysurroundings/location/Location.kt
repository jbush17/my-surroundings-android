package com.example.jbush.mysurroundings.location

import android.Manifest
import android.app.Activity
import android.content.IntentSender
import android.content.pm.PackageManager
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.util.Log
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*
import java.util.concurrent.atomic.AtomicLong

data class LocationWrapper (val l: android.location.Location, val id: String) {
}

object LocationManager : LocationCallback() {

    const val PERMISSION_REQUEST_CODE = 12345
    const val REQUEST_CHECK_SETTINGS = 2345
    const val MAX_LIST_SIZE = 200

    private var _locationsList = mutableListOf<LocationWrapper>()
    val locations : List<LocationWrapper>
        get () = _locationsList

    private var mFusedLocationClient : FusedLocationProviderClient? = null
    private var mLocationRequest : LocationRequest? = null
    private val locationIdCouner = AtomicLong ()

    var activity : LocationAwareActivity? = null

    /**
     * Called when a new locaton is available
     *
     */
    override fun onLocationResult (locationResult: LocationResult){
        super.onLocationResult(locationResult)
        Log.v(this::class.simpleName, "Received location update ${locationResult.lastLocation.latitude}, ${locationResult.lastLocation.longitude}")
        val lw = LocationWrapper(locationResult.lastLocation, locationIdCouner.incrementAndGet().toString())
        _locationsList.add(lw)
        if (_locationsList.size > MAX_LIST_SIZE) _locationsList.removeAll(_locationsList.subList(0, _locationsList.size - MAX_LIST_SIZE))

        activity?.onLocationUpdate(lw)
    }

    /**
     * onRequestPermissionsResult
     *
     * Called any time a permission was requested, once the user has accepted/denied the permission
     *
     */
    fun onRequestPermissionsResult(activity: LocationAwareActivity, requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        when (requestCode) {
            LocationManager.PERMISSION_REQUEST_CODE -> {
                if (grantResults.isNotEmpty() && grantResults.first() == PackageManager.PERMISSION_GRANTED){
                    // permissions were granted for location
                    Log.v(this::class.simpleName, "Permissions granted for GPS")
                    LocationManager.startLocationUpdates(activity)
                } else {
                    // permissions weren't granted
                    Log.v(this::class.simpleName, "Permissions weren't granted for GPS")
                }
                return
            }
            else -> {
                // ignored
            }
        }
    }

    /**
     * checkGPSPermissions
     *
     * Returns true if we had to prompt the User for permissions and false if permissions were acceptable
     *
     */
    fun checkGPSPermissions (activity : LocationAwareActivity) : Boolean {
        Log.v(this::class.simpleName, "Checking permissions")
        var promptedUser = false
        if (ContextCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED) {
            Log.v(this::class.simpleName, "Requesting appropriate permissions from user")
            ActivityCompat.requestPermissions(activity, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), PERMISSION_REQUEST_CODE)
            promptedUser = true
        } else {
            Log.v(this::class.simpleName, "Permissions were acceptable")
        }
        return promptedUser
    }

    /**
     * configureLocationSettings
     *
     * Configures the device's location settings
     *
     */
    fun configureLocationSettings(activity: LocationAwareActivity) {
        val builder = LocationSettingsRequest.Builder()
                .addLocationRequest(mLocationRequest!!)
        val client = LocationServices.getSettingsClient(activity)
        val task = client.checkLocationSettings(builder.build())

        task.addOnSuccessListener(activity) {
            // All location settings are satisfied. The client can initialize
            // location requests here.
        }

        task.addOnFailureListener(activity) { e ->
            if (e is ResolvableApiException) {
                // Location settings are not satisfied, but this can be fixed
                // by showing the user a dialog.
                try {
                    // Show the dialog by calling startResolutionForResult(),
                    // and check the result in onActivityResult().
                    e.startResolutionForResult(activity,
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
    fun startLocationUpdates (activity : LocationAwareActivity) {
        initialize (activity)
        this.activity = activity

        val promptedUser = checkGPSPermissions(activity)
        if (!promptedUser) {
            Log.v(this::class.simpleName, "Starting location updates")
            configureLocationSettings(activity)
            mFusedLocationClient?.requestLocationUpdates(mLocationRequest, this, null)
        } else {
            Log.v(this::class.simpleName, "Had to prompt the User for permissions for location updates.  Not starting now")
        }
    }

    /**
     * Stops location updates
     *
     */
    fun stopLocationUpdates () {
        Log.v(this::class.simpleName, "Stopping location updates")
        mFusedLocationClient?.removeLocationUpdates(this)
        this?.activity = null
    }

    fun initialize (activity: LocationAwareActivity) {
        if (mFusedLocationClient != null) return

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(activity.applicationContext);
        mLocationRequest = LocationRequest()
        mLocationRequest?.interval = 1000
        mLocationRequest?.fastestInterval = 1000
        mLocationRequest?.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
    }
}

