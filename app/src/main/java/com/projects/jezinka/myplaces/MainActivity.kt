package com.projects.jezinka.myplaces

import android.content.ContentValues
import android.content.Context
import android.database.DatabaseUtils.queryNumEntries
import android.location.Criteria
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_save_note_dialog.view.*


class MainActivity : AppCompatActivity(), LocationListener {

    var mDbHelper: MyPlacesDBHelper = MyPlacesDBHelper(this)

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
            longtitude.text = location.longitude.toString()
            latitude.text = location.latitude.toString()
        }
    }

    fun savePlace(view: View) {
        val builder = AlertDialog.Builder(this)
        val dialog_view = layoutInflater.inflate(R.layout.fragment_save_note_dialog, null)
        builder.setMessage("Wpisz notatkę")
                .setView(dialog_view)
                .setTitle("Zapisz miejsce")
                .setPositiveButton(R.string.save, { dialogInterface, _ ->
                    Toast.makeText(view.context, "Zapisuję!", Toast.LENGTH_SHORT).show()

                    val long_coord = dialog_view.longtitude_note.text as String
                    val lat_coord = dialog_view.latitude_note.text as String
                    val note = dialog_view.note.text.toString()

                    insertPlace(note, long_coord, lat_coord)
                    dialogInterface.dismiss()
                })
                .setNegativeButton(android.R.string.cancel, { dialogInterface, _ ->
                    dialogInterface.dismiss()
                })
                .create()
                .show()

        if (latitude?.text != null && longtitude?.text != null) {
            dialog_view.latitude_note.text = latitude.text
            dialog_view.longtitude_note.text = longtitude.text
        }
    }

    private fun insertPlace(note: String, longtitude: String, latitude: String) {

        val db = mDbHelper.writableDatabase

        val values = ContentValues()
        values.put(MyPlacesContract.MyPlaceEntry.COLUMN_NAME_NOTE, note)
        values.put(MyPlacesContract.MyPlaceEntry.COLUMN_NAME_LONGTITUDE, longtitude)
        values.put(MyPlacesContract.MyPlaceEntry.COLUMN_NAME_LATITUDE, latitude)
        values.put(MyPlacesContract.MyPlaceEntry.COLUMN_NAME_ORDER, getNextOrder())

        val newRowId = db.insert(MyPlacesContract.MyPlaceEntry.TABLE_NAME, null, values)
    }

    fun getNextOrder(): Long {
        val db_read = mDbHelper.readableDatabase
        return queryNumEntries(db_read, MyPlacesContract.MyPlaceEntry.TABLE_NAME) + 1
    }
}