package com.example.jbush.mysurroundings.location

import android.support.v7.app.AppCompatActivity

abstract class LocationAwareActivity : AppCompatActivity() {

    override fun onResume() {
        super.onResume()
        LocationManager.startLocationUpdates(this)

    }

    override fun onPause() {
        super.onPause()
        LocationManager.stopLocationUpdates()
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        LocationManager.onRequestPermissionsResult(this, requestCode, permissions, grantResults)
    }

    abstract fun onLocationUpdate (location : LocationWrapper)
}