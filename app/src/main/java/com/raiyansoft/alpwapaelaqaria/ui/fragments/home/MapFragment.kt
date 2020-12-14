package com.raiyansoft.alpwapaelaqaria.ui.fragments.home

import android.Manifest
import android.app.Activity
import android.app.Dialog
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.content.IntentSender
import android.content.pm.PackageManager
import android.location.Criteria
import android.location.Location
import android.location.LocationManager
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.Nullable
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.common.api.PendingResult
import com.google.android.gms.common.api.ResultCallback
import com.google.android.gms.common.api.Status
import com.google.android.gms.location.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import com.raiyansoft.alpwapaelaqaria.R
import com.raiyansoft.alpwapaelaqaria.ui.activity.search.SearchActivity
import com.raiyansoft.alpwapaelaqaria.ui.viewmodel.MainViewModel
import kotlinx.android.synthetic.main.custom_dialog.*
import kotlinx.android.synthetic.main.fragment_map.view.*

class MapFragment : Fragment(), GoogleApiClient.ConnectionCallbacks,
    GoogleApiClient.OnConnectionFailedListener {

    private lateinit var mMap: GoogleMap
    private var mFusedLocationClient: FusedLocationProviderClient? = null
    var mLocationRequest: LocationRequest? = null
    var mGoogleApiClient: GoogleApiClient? = null
    val REQUEST_LOCATION = 199
    var result: PendingResult<LocationSettingsResult>? = null
    private var locationManager: LocationManager? = null
    private var handler: Handler? = null
    var mLocation: Location? = null
    var lat = 0.0
    var lng = 0.0

    private var currentLang = ""
    private lateinit var token: String

    private lateinit var dialog: Dialog
    private lateinit var title: TextView
    private lateinit var details: TextView
    private lateinit var ok: Button

    private lateinit var loading: ProgressDialog

    private val viewModel by lazy {
        ViewModelProvider(requireActivity())[MainViewModel::class.java]
    }

    private lateinit var myLocation: LatLng

    private val callback = OnMapReadyCallback { googleMap ->
        mMap = googleMap

        val turkeyLocation = LatLng(38.500208, 34.919394)

        viewModel.getDataMap(lat, lng, 360)
        viewModel.mapLiveData.observe(viewLifecycleOwner, Observer { data ->
            for (i in data.data!!.data!!) {
                val lat = java.lang.Double.parseDouble(i.lat!!)
                val lng = java.lang.Double.parseDouble(i.lng!!)
                val loc = LatLng(lat, lng)
                val markerOptions = MarkerOptions()
                markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_mark))
                mMap.addMarker(
                    markerOptions.position(loc).title(i.title!!)
                )
            }
            mMap.animateCamera(
                CameraUpdateFactory.newLatLngZoom(
                    turkeyLocation,
                    5f
                )
            )
        })

        if (ActivityCompat.checkSelfPermission(
                activity!!,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                activity!!,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            if (ActivityCompat.checkSelfPermission(
                    activity!!,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                    activity!!,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                requestStoragePermission()
            } else {
                mMap.isMyLocationEnabled = true
                mMap.uiSettings.isMyLocationButtonEnabled = true
            }
        }
    }

    private var mLocationCallback: LocationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            for (location in locationResult.locations) {
                mLocation = location
            }
            lat = mLocation!!.latitude
            lng = mLocation!!.longitude

            myLocation = LatLng(lat, lng)
            if (mFusedLocationClient != null) {
                mFusedLocationClient!!.removeLocationUpdates(this)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        handler = Handler()
        requestStoragePermission()
    }

    private fun requestStoragePermission() {
        Dexter.withContext(activity)
            .withPermissions(
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.INTERNET
            )
            .withListener(object : MultiplePermissionsListener {
                override fun onPermissionsChecked(report: MultiplePermissionsReport?) {
                    if (report!!.areAllPermissionsGranted()) {
                        mFusedLocationClient =
                            LocationServices.getFusedLocationProviderClient(activity!!)
                        mLocationRequest = LocationRequest.create()
                        mLocationRequest!!.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
                        if (isLocationServiceEnabled(activity!!)) {
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                if (ContextCompat.checkSelfPermission(
                                        activity!!,
                                        Manifest.permission.ACCESS_FINE_LOCATION
                                    )
                                    == PackageManager.PERMISSION_GRANTED
                                ) {
                                    mFusedLocationClient!!.requestLocationUpdates(
                                        mLocationRequest,
                                        mLocationCallback,
                                        Looper.myLooper()
                                    )
                                } //Request Location Permission
                            } else {
                                mFusedLocationClient!!.requestLocationUpdates(
                                    mLocationRequest,
                                    mLocationCallback,
                                    Looper.myLooper()
                                )
                            }
                        } else {
                            mGoogleApiClient = GoogleApiClient.Builder(activity!!)
                                .addConnectionCallbacks(this@MapFragment)
                                .addOnConnectionFailedListener(this@MapFragment)
                                .addApi(LocationServices.API)
                                .build()
                            mGoogleApiClient!!.connect()
                        }
                    } else {
                        showSettingsDialog()
                    }
                }

                override fun onPermissionRationaleShouldBeShown(
                    p0: MutableList<PermissionRequest>?,
                    p1: PermissionToken?
                ) {
                    p1!!.continuePermissionRequest()
                }
            })
            .check()
    }

    private fun showSettingsDialog() {

        val alertDialog = AlertDialog.Builder(activity!!)
        alertDialog.setTitle(getString(R.string.attention))
        alertDialog.setMessage(getString(R.string.permition_error))
        alertDialog.setCancelable(false)
        alertDialog.setIcon(R.drawable.ic_warning)

        alertDialog.setPositiveButton(getString(R.string.reask)) { _, _ ->
            requestStoragePermission()
        }

        alertDialog.setNegativeButton(getString(R.string.yes)) { dialogInterface, _ ->
            dialogInterface.cancel()
        }

        alertDialog.create().show()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_map, container, false)

        dialog = Dialog(activity!!)
        dialog.setContentView(R.layout.custom_dialog)
        title = dialog.tvDialogTitle
        details = dialog.tvDialogText
        ok = dialog.btnOk
        dialog.setCancelable(false)
        ok.setOnClickListener {
            dialog.cancel()
        }

        loading = ProgressDialog(activity)
        loading.setCancelable(false)
        loading.setMessage(getString(R.string.loading_data))

        val sharedPreferences = activity!!.getSharedPreferences("shared", Context.MODE_PRIVATE)
        token = sharedPreferences.getString("userToken", "")!!
        currentLang = sharedPreferences.getString("lang", "ar")!!

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(activity!!)

        if (ActivityCompat.checkSelfPermission(
                activity!!,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                activity!!,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            mFusedLocationClient!!.requestLocationUpdates(
                getLocationRequest(),
                mLocationCallback,
                Looper.myLooper()
            )
        }

        root.imgMapSearch.setOnClickListener {
            serchClick()
        }

        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment?.getMapAsync(callback)

    }

    private fun getLocationRequest(): LocationRequest? {
        val locationRequest = LocationRequest.create()
        locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        locationRequest.interval = 3000
        return locationRequest
    }

    private fun serchClick() {
        val intent = Intent(activity, SearchActivity::class.java)
        intent.putExtra("open", 3)
        activity!!.startActivity(intent)
    }

    fun isLocationServiceEnabled(context: Context): Boolean {
        var gps_enabled = false
        var network_enabled = false
        try {
            locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager?
            val criteria = Criteria()
            criteria.powerRequirement =
                Criteria.POWER_MEDIUM // Chose your desired power consumption level.
            criteria.accuracy = Criteria.ACCURACY_MEDIUM // Choose your accuracy requirement.
            criteria.isSpeedRequired = true // Chose if speed for first location fix is required.
            criteria.isAltitudeRequired = true // Choose if you use altitude.
            criteria.isBearingRequired = true // Choose if you use bearing.
            criteria.isCostAllowed = true // Choose if this provider can waste money :-)
            locationManager!!.getBestProvider(criteria, true)
            gps_enabled = locationManager!!.isProviderEnabled(LocationManager.GPS_PROVIDER)
            network_enabled = locationManager!!.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
        } catch (ex: IndexOutOfBoundsException) {
            ex.message
        }
        return gps_enabled || network_enabled
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        //val states = LocationSettingsStates.fromIntent(data);
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            REQUEST_LOCATION ->
                when (resultCode) {
                    Activity.RESULT_OK -> requestStoragePermission()
                    Activity.RESULT_CANCELED -> activity!!.finish()
                }
        }
    }

    override fun onConnected(@Nullable bundle: Bundle?) {
        mLocationRequest = LocationRequest.create()
            .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
            .setInterval(3 * 1000)
            .setFastestInterval(1000)
        val builder: LocationSettingsRequest.Builder = LocationSettingsRequest.Builder()
            .addLocationRequest(mLocationRequest!!)
        builder.setAlwaysShow(true)
        result =
            LocationServices.SettingsApi.checkLocationSettings(mGoogleApiClient, builder.build())
        result!!.setResultCallback(
            ResultCallback { result: LocationSettingsResult ->
                val status: Status = result.status
                when (status.statusCode) {
                    LocationSettingsStatusCodes.SUCCESS -> {
                    }
                    LocationSettingsStatusCodes.RESOLUTION_REQUIRED -> try {
                        status.startResolutionForResult(activity!!, REQUEST_LOCATION)
                    } catch (e: IntentSender.SendIntentException) {
                        // Ignore the error.
                    }
                }
            }
        )
    }


    override fun onConnectionSuspended(i: Int) {}


    override fun onStart() {
        super.onStart()
        if (!isLocationServiceEnabled(activity!!)) if (mGoogleApiClient != null) {
            mGoogleApiClient!!.connect()
        }
    }

    override fun onStop() {
        //TODO this added
        if (mGoogleApiClient != null) {
            mGoogleApiClient!!.disconnect()
        }
        if (mFusedLocationClient != null) {
            mFusedLocationClient!!.removeLocationUpdates(mLocationCallback)
        }
        super.onStop()
    }

    override fun onDestroy() {
        if (mGoogleApiClient != null) mGoogleApiClient!!.disconnect()
        handler!!.removeCallbacksAndMessages(null)
        if (mFusedLocationClient != null) {
            mFusedLocationClient!!.removeLocationUpdates(mLocationCallback)
        }
        super.onDestroy()
    }

    override fun onPause() {
        super.onPause()
        if (mGoogleApiClient != null) mGoogleApiClient!!.disconnect()
        if (mFusedLocationClient != null) {
            mFusedLocationClient!!.removeLocationUpdates(mLocationCallback)
        }
    }

    override fun onConnectionFailed(p0: ConnectionResult) {
        Toast.makeText(activity, getString(R.string.somthing_wrong), Toast.LENGTH_SHORT).show()
    }
}