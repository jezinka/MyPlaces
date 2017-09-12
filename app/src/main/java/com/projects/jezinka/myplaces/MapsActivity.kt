package com.projects.jezinka.myplaces

import android.os.Bundle
import android.support.v4.app.FragmentActivity
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions

class MapsActivity : FragmentActivity(), OnMapReadyCallback {

    private var mMap: GoogleMap? = null
    private var mlong: Double = -34.0
    private var mlat: Double = 151.0
    private var mTitle: String = "Tutaj"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)

        mlong = intent.getDoubleExtra("long", mlong)
        mlat = intent.getDoubleExtra("lat", mlat)
        mTitle = intent.getStringExtra("title")

        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        val myPlace = LatLng(mlat, mlong)
        mMap!!.addMarker(MarkerOptions().position(myPlace).title(mTitle))
        mMap!!.moveCamera(CameraUpdateFactory.newLatLng(myPlace))
        mMap!!.animateCamera(CameraUpdateFactory.zoomTo(15f))
    }
}
