package com.example.jbush.mysurroundings

import android.graphics.Color
import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TableRow
import android.widget.TextView
import com.example.jbush.mysurroundings.location.LocationManager
import com.example.jbush.mysurroundings.location.LocationWrapper
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

    private val MAX_LOCATIONS_IN_TABLE = 200

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_mysurroundings, container, false)
    }

    fun onLocationUpdate (locationWrapper: LocationWrapper) {

        // add new table row and remove any that are too old
        val rowForLocation = locationWrapper.convertToTableRow()
        locationsTable.addView( rowForLocation, 1)
        if (locationsTable.childCount > MAX_LOCATIONS_IN_TABLE) locationsTable.removeViews( MAX_LOCATIONS_IN_TABLE, MAX_LOCATIONS_IN_TABLE - locationsTable.childCount)
    }

    private fun LocationWrapper.convertToTableRow () : TableRow {
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
        tr.setTag(locationsTable.id, id)
        tr.setOnClickListener(TableRowClickListener())

        idTv.text = id
        latTv.text = this.l.latitude.toString()
        lonTv.text = this.l.longitude.toString()
        speedTv.text = this.l.speed.toString()
        accuracyTv.text = this.l.accuracy.toString()

        listOf<View>(idTv, latTv,lonTv,speedTv,accuracyTv).forEach { it.setPadding(0,0,30,0) }

        return tr
    }

    private inner class TableRowClickListener : View.OnClickListener {
        override fun onClick(v: View?) {
            locationsTable.viewsExceptFirst.forEach { it.setBackgroundColor(Color.TRANSPARENT) }
            v?.setBackgroundColor( context?.getColor(R.color.colorAccent)!! )

            val location = LocationManager.locations.find { it.id == v?.getTag(locationsTable.id) }

            Log.v(this@MySurroundingsFragment::class.simpleName, "Location was clicked: ${location?.id} ${location?.l?.latitude}, ${location?.l?.longitude}" )
        }
    }
}
