package com.example.jbush.mysurroundings

import android.content.Context
import android.graphics.Color
import android.location.Location
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TableRow
import android.widget.TextView
import com.example.jbush.mysurroundings.location.LocationWrapper
import com.google.android.gms.location.LocationResult
import kotlinx.android.synthetic.main.fragment_mysurroundings.*
import java.util.concurrent.atomic.AtomicInteger
import java.util.concurrent.atomic.AtomicLong


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
    private val tableRowsToLocations : MutableMap<View, Location> = mutableMapOf()
    private val locationIdCouner = AtomicLong ()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_mysurroundings, container, false)
    }

    fun onLocationUpdate (locationWrapper: LocationWrapper) {
        Log.v(LOGTAG, "Received location update " +
                "${locationWrapper.l.latitude}," +
                "${locationWrapper.l.longitude}," +
                "${locationWrapper.l.bearing}" )

        // Add new entry to lastLocations and clen up the old ones
        lastLocations.add(0, locationWrapper.l)
        if (lastLocations.size > MAX_LOCATIONS_IN_TABLE) lastLocations.dropLast(lastLocations.size - MAX_LOCATIONS_IN_TABLE)

        // add new table row and remove any that are too old
        val rowForLocation = locationWrapper.l.convertToTableRow(locationIdCouner.getAndIncrement())
        tableRowsToLocations.put (rowForLocation,locationWrapper.l)
        locationsTable.addView( rowForLocation, 1)
        if (locationsTable.childCount > MAX_LOCATIONS_IN_TABLE) locationsTable.removeViews( MAX_LOCATIONS_IN_TABLE, MAX_LOCATIONS_IN_TABLE - locationsTable.childCount)
        tableRowsToLocations.entries.removeIf { !locationsTable.views.contains(it.key) }
    }

    private fun Location.convertToTableRow (id : Long) : TableRow {
        val tr = TableRow (this@MySurroundingsFragment.context)
        tr.layoutParams = TableRow.LayoutParams (TableRow.LayoutParams.WRAP_CONTENT,
                TableRow.LayoutParams.WRAP_CONTENT)

        val idTv = TextView (this@MySurroundingsFragment.context)
        tr.addView(idTv)
        val latTv = TextView (this@MySurroundingsFragment.context)
        tr.addView(latTv)
        val lonTv = TextView (this@MySurroundingsFragment.context)
        tr.addView(lonTv)
        val speedTv = TextView (this@MySurroundingsFragment.context)
        tr.addView(speedTv)
        val accuracyTv = TextView (this@MySurroundingsFragment.context)
        tr.addView(accuracyTv)
        tr.isClickable = true
        tr.setOnClickListener(TableRowClickListener())

        idTv.text = id.toString()
        latTv.text = this.latitude.toString()
        lonTv.text = this.longitude.toString()
        speedTv.text = this.speed.toString()
        accuracyTv.text = this.accuracy.toString()

        listOf<View>(idTv, latTv,lonTv,speedTv,accuracyTv).forEach { it.setPadding(0,0,30,0) }

        return tr
    }

    private inner class TableRowClickListener : View.OnClickListener {
        override fun onClick(v: View?) {
            locationsTable.viewsExceptFirst.forEach { it.setBackgroundColor(Color.TRANSPARENT) }
            v?.setBackgroundColor( context?.getColor(R.color.colorAccent)!! )

            val location = tableRowsToLocations.get(v)

            Log.v(LOGTAG, "Location was clicked: ${location?.latitude}, ${location?.longitude}" )
        }
    }
}
