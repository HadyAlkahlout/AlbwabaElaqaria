package com.raiyansoft.alpwapaelaqaria.ui.activity.function

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Looper
import android.widget.Button
import android.widget.Toast
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.raiyansoft.alpwapaelaqaria.R
import com.raiyansoft.alpwapaelaqaria.util.Common
import kotlinx.android.synthetic.main.activity_pick_location.*

class PickLocationActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var send: Button
    private lateinit var mMap: GoogleMap
    private var mFusedLocationClient: FusedLocationProviderClient? = null
    var mLocation: Location? = null
    var lat = 0.0
    var lng = 0.0

    var propLat = 0.0
    var propLng = 0.0

    var propertyLat = 0.0
    var propertyLng = 0.0

    private var mLocationCallback: LocationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            for (location in locationResult.locations) {
                mLocation = location
            }
            lat = mLocation!!.latitude
            lng = mLocation!!.longitude

            if (propertyLat != 0.0 && propertyLng != 0.0){
                val loc = LatLng(propertyLat, propertyLng)
                val marker = MarkerOptions()
                marker.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_mark))
                mMap.addMarker(
                    marker.position(loc)
                )
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(loc, 5f))
            }else{
                val turkeyLocation = LatLng(38.500208, 34.919394)
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(turkeyLocation, 5f))
            }
            if (mFusedLocationClient != null) {
                mFusedLocationClient!!.removeLocationUpdates(this)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pick_location)

        val shared = getSharedPreferences("shared", Context.MODE_PRIVATE)
        val lang = shared.getString("lang", "ar")!!
        Common.setLocale(lang, this)

        if (intent != null){
            propertyLat = intent.getDoubleExtra("lat", 0.0)
            propertyLng = intent.getDoubleExtra("lng", 0.0)
        }

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            mFusedLocationClient!!.requestLocationUpdates(
                getLocationRequest(),
                mLocationCallback,
                Looper.myLooper()
            )
        }

        send = btnConfirm

        send.setOnClickListener {
            if (propLat == 0.0 || propLng == 0.0){
                Toast.makeText(this, getString(R.string.pick_loc), Toast.LENGTH_SHORT).show()
            }else {
                val i = Intent(this, AddPropertyActivity::class.java)
                i.putExtra("lat", propLat)
                i.putExtra("lng", propLng)
                setResult(RESULT_OK, i)
                finish()
            }
        }

    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        mMap.setOnMapClickListener {latLng ->
            mMap.clear()
            val marker = MarkerOptions()
            marker.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_mark))
            mMap.addMarker(
                marker.position(latLng)
            )
            propLat = latLng.latitude
            propLng = latLng.longitude
        }
    }

    private fun getLocationRequest(): LocationRequest? {
        val locationRequest = LocationRequest.create()
        locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        locationRequest.interval = 3000
        return locationRequest
    }
}