package com.projects.jezinka.myplaces

import android.content.Context
import android.location.Criteria
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.TextView
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity(), LocationListener {
    override fun onLocationChanged(location: Location?) {
        longtitude.text = location?.longitude.toString()
        latitude.text = location?.latitude.toString()
    }

    override fun onStatusChanged(p0: String?, p1: Int, p2: Bundle?) {}

    override fun onProviderEnabled(p0: String?) {}

    override fun onProviderDisabled(p0: String?) {}

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    fun findMeButtonClick(view: View) {
        val locationManager = applicationContext.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        val bestProvider = locationManager.getBestProvider(Criteria(), false)
        val location = locationManager.getLastKnownLocation(bestProvider)

        if (location != null) {
            longtitude.setText(location.longitude.toString())
            latitude.setText(location.latitude.toString())
        }
    }

    fun savePlace(view: View) {
        val builder = AlertDialog.Builder(this)
        val dialog_view = layoutInflater.inflate(R.layout.fragment_save_note_dialog, null)
        builder.setMessage("Wpisz notatkę")
                .setView(dialog_view)
                .setTitle("Zapisz miejsce")
                .create()
                .show()

        if (latitude?.text != null && longtitude?.text != null) {
            dialog_view.findViewById<TextView>(R.id.latitude_note).text = latitude.text
            dialog_view.findViewById<TextView>(R.id.longtitude_note).text = longtitude.text
        }
    }
}