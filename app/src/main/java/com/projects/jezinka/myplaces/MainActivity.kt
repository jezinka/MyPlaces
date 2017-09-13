package com.projects.jezinka.myplaces

import android.content.ContentValues
import android.content.Context
import android.content.Intent
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        listview.adapter = MyPlacesAdapter(this, R.layout.places_list_view, getSavedLocations())
    }

    fun findMeButtonClick(view: View) {
        val locationManager = applicationContext.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        val bestProvider = locationManager.getBestProvider(Criteria(), false)
        val location = locationManager.getLastKnownLocation(bestProvider)

        if (location != null) {
            longitude.text = location.longitude.toString()
            latitude.text = location.latitude.toString()
        }
    }

    fun savePlace(view: View) {
        val dialog_view = layoutInflater.inflate(R.layout.fragment_save_note_dialog, null)
        showEditDialog(dialog_view, null).create().show()
    }

    private fun showEditDialog(dialog_view: View, id: Long?): AlertDialog.Builder {
        val builder = AlertDialog.Builder(this)
        builder.setMessage(getString(R.string.put_note))
                .setView(dialog_view)
                .setTitle(getString(R.string.save_place))
                .setNegativeButton(android.R.string.cancel, { dialogInterface, _ ->
                    dialogInterface.dismiss()
                })

        if (latitude?.text != null && longitude?.text != null) {
            dialog_view.latitude_note.text = latitude.text
            dialog_view.longitude_note.text = longitude.text
        }

        if (id != null) {
            builder.setPositiveButton(R.string.save, { dialogInterface, _ ->
                Toast.makeText(this, getString(R.string.saving), Toast.LENGTH_SHORT).show()

                val note = dialog_view.note.text.toString()

                updatePlace(note, id)
                listview.adapter = MyPlacesAdapter(this, R.layout.places_list_view, getSavedLocations())
                dialogInterface.dismiss()
            })
        } else {
            builder.setPositiveButton(R.string.save, { dialogInterface, _ ->
                Toast.makeText(dialog_view.context, getString(R.string.saving), Toast.LENGTH_SHORT).show()

                val long_coord = dialog_view.longitude_note.text as String
                val lat_coord = dialog_view.latitude_note.text as String
                val note = dialog_view.note.text.toString()

                insertPlace(note, long_coord, lat_coord)
                listview.adapter = MyPlacesAdapter(this, R.layout.places_list_view, getSavedLocations())
                dialogInterface.dismiss()
            })
        }

        return builder
    }

    private fun insertPlace(note: String, longitude: String, latitude: String) {

        val db = mDbHelper.writableDatabase

        val values = ContentValues()
        values.put(MyPlacesContract.MyPlaceEntry.COLUMN_NAME_NOTE, note)
        values.put(MyPlacesContract.MyPlaceEntry.COLUMN_NAME_LONGITUDE, longitude)
        values.put(MyPlacesContract.MyPlaceEntry.COLUMN_NAME_LATITUDE, latitude)
        values.put(MyPlacesContract.MyPlaceEntry.COLUMN_NAME_ORDER, getNextOrder())

        val newRowId = db.insert(MyPlacesContract.MyPlaceEntry.TABLE_NAME, null, values)
    }

    private fun updatePlace(note: String, id: Long) {

        val db = mDbHelper.writableDatabase

        val values = ContentValues()
        values.put(MyPlacesContract.MyPlaceEntry.COLUMN_NAME_NOTE, note)

        val newRowId = db.update(MyPlacesContract.MyPlaceEntry.TABLE_NAME, values, "_id= $id", null)
    }

    fun getNextOrder(): Long {
        val db_read = mDbHelper.readableDatabase
        return queryNumEntries(db_read, MyPlacesContract.MyPlaceEntry.TABLE_NAME) + 1
    }

    fun getSavedLocations(): List<MyPlacesContract.MyPlace> {

        val array_list = mutableListOf<MyPlacesContract.MyPlace>()

        val db = mDbHelper.readableDatabase
        val res = db.rawQuery("""select ${MyPlacesContract.MyPlaceEntry._ID},
            |${MyPlacesContract.MyPlaceEntry.COLUMN_NAME_LONGITUDE},
            |${MyPlacesContract.MyPlaceEntry.COLUMN_NAME_LATITUDE},
            |${MyPlacesContract.MyPlaceEntry.COLUMN_NAME_NOTE},
            |${MyPlacesContract.MyPlaceEntry.COLUMN_NAME_ORDER}
            |from my_place order by order_no""".trimMargin(), null)
        if (res!!.getCount() > 0) {
            res.moveToFirst()

            do {
                val myPlace = MyPlacesContract.MyPlace(res.getLong(0), res.getString(1), res.getString(2), res.getString(3), res.getInt(4))
                array_list.add(myPlace)
            } while (res.moveToNext())
        }

        db.close()
        return array_list
    }

    fun deletePlace(id: Long) {
        val db = mDbHelper.writableDatabase

        val selection = "${MyPlacesContract.MyPlaceEntry._ID} = ?"
        val selectionArgs = arrayOf(id.toString())
        db.delete(MyPlacesContract.MyPlaceEntry.TABLE_NAME, selection, selectionArgs)
        listview.adapter = MyPlacesAdapter(this, R.layout.places_list_view, getSavedLocations())
    }

    fun editPlace(id: Long) {
        val dialog_view = layoutInflater.inflate(R.layout.fragment_save_note_dialog, null)
        showEditDialog(dialog_view, id).create().show()

        val db = mDbHelper.readableDatabase
        val res = db.rawQuery("""select ${MyPlacesContract.MyPlaceEntry._ID},
            |${MyPlacesContract.MyPlaceEntry.COLUMN_NAME_LONGITUDE},
            |${MyPlacesContract.MyPlaceEntry.COLUMN_NAME_LATITUDE},
            |${MyPlacesContract.MyPlaceEntry.COLUMN_NAME_NOTE},
            |${MyPlacesContract.MyPlaceEntry.COLUMN_NAME_ORDER}
            |from my_place
            |where ${MyPlacesContract.MyPlaceEntry._ID} = $id
            |order by order_no""".trimMargin(), null)
        if (res!!.count == 1) {
            res.moveToFirst()

            val myPlace = MyPlacesContract.MyPlace(res.getLong(0), res.getString(1), res.getString(2), res.getString(3), res.getInt(4))
            dialog_view.longitude_note.setText(myPlace.longitude)
            dialog_view.latitude_note.setText(myPlace.latitude)
            dialog_view.note.setText(myPlace.note)
        }

        db.close()
    }

    override fun onLocationChanged(location: Location?) {
        longitude.text = location?.longitude.toString()
        latitude.text = location?.latitude.toString()
    }

    fun showOnMap(mLong: Double, mLat: Double, mTitle: String) {
        val activityIntent = Intent(this, MapsActivity::class.java)
        activityIntent.putExtra("long", mLong)
        activityIntent.putExtra("lat", mLat)
        activityIntent.putExtra("title", mTitle)
        this.startActivity(activityIntent)
    }

    override fun onStatusChanged(p0: String?, p1: Int, p2: Bundle?) {}

    override fun onProviderEnabled(p0: String?) {}

    override fun onProviderDisabled(p0: String?) {}
}