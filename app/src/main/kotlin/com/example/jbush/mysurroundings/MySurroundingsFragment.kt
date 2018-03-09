package com.example.jbush.mysurroundings

import android.content.Context
import android.location.Location
import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TableRow
import android.widget.TextView
import com.google.android.gms.location.LocationResult
import kotlinx.android.synthetic.main.fragment_mysurroundings.*


/**
 * A simple [Fragment] subclass.
 * Activities that contain this fragment must implement the
 * [MySurroundingsFragment.OnFragmentInteractionListener] interface
 * to handle interaction events.
 * Use the [MySurroundingsFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class MySurroundingsFragment : Fragment() {

    private val LOGTAG = MySurroundingsFragment::class.simpleName
    private val MAX_LOCATIONS_IN_TABLE = 200

    private var lastLocations : MutableList<Location>  = mutableListOf()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_mysurroundings, container, false)
    }

    fun onLocationUpdate (locationResult: LocationResult) {
        Log.v(LOGTAG, "Received location update " +
                "${locationResult.lastLocation.latitude}," +
                "${locationResult.lastLocation.longitude}," +
                "${locationResult.lastLocation.bearing}" )

        lastLocations.add(0, locationResult.lastLocation)
        if (lastLocations.size > MAX_LOCATIONS_IN_TABLE) lastLocations.dropLast(lastLocations.size - MAX_LOCATIONS_IN_TABLE)
        if (locationsTable.childCount > 1) locationsTable.removeViews(1, locationsTable.childCount - 1)
        lastLocations.map { it.convertToTableRow() }.forEach { locationsTable.addView (it) }
    }

    private fun Location.convertToTableRow () : TableRow {
        val tr = TableRow (this@MySurroundingsFragment.context)
        tr.layoutParams = TableRow.LayoutParams (TableRow.LayoutParams.WRAP_CONTENT,
                TableRow.LayoutParams.WRAP_CONTENT)

        val latTv = TextView (this@MySurroundingsFragment.context)
        tr.addView(latTv)
        val lonTv = TextView (this@MySurroundingsFragment.context)
        tr.addView(lonTv)
        val speedTv = TextView (this@MySurroundingsFragment.context)
        tr.addView(speedTv)
        val accuracyTv = TextView (this@MySurroundingsFragment.context)
        tr.addView(accuracyTv)

        latTv.text = this.latitude.toString()
        lonTv.text = this.longitude.toString()
        speedTv.text = this.speed.toString()
        accuracyTv.text = this.accuracy.toString()

        listOf<View>(latTv,lonTv,speedTv,accuracyTv).forEach { it.setPadding(0,0,30,0) }

        return tr
    }
}
