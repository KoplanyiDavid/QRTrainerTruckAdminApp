package com.vicegym.qrtrainertruckadminapp.fragments

import TrackingService
import android.Manifest
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.android.gms.common.GooglePlayServicesNotAvailableException
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.vicegym.qrtrainertruckadminapp.R
import com.vicegym.qrtrainertruckadminapp.databinding.FragmentTrainerTruckBinding
import androidx.core.app.ActivityCompat

import android.content.pm.PackageManager

import androidx.core.content.ContextCompat

import android.location.LocationManager

import android.content.Context.LOCATION_SERVICE

import androidx.core.content.ContextCompat.getSystemService
import android.content.Intent





class TrainerTruckFragment : Fragment(), OnMapReadyCallback {

    private lateinit var binding: FragmentTrainerTruckBinding
    private lateinit var mapView: MapView
    private lateinit var gMap: GoogleMap
    private var marker: Marker? = null

    companion object {
        private const val PERMISSION_LOCATION = 101

        @JvmStatic
        fun newInstance() =
            TrainerTruckFragment()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentTrainerTruckBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init(savedInstanceState)
        locationTrackingInit()
    }

    private fun locationTrackingInit() {
        //Check whether GPS tracking is enabled//

        //Check whether GPS tracking is enabled//
        val lm = context?.getSystemService(LOCATION_SERVICE) as LocationManager?
        if (!lm!!.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            activity?.finish()
        }

//Check whether this app has access to the location permission//
        val permission = ContextCompat.checkSelfPermission(
            requireContext(),
            Manifest.permission.ACCESS_FINE_LOCATION
        )

//If the location permission has been granted, then start the TrackerService//


//If the location permission has been granted, then start the TrackerService//
        if (permission == PackageManager.PERMISSION_GRANTED) {
            startTrackerService()
        } else {

//If the app doesn’t currently have access to the user’s location, then request access//
            ActivityCompat.requestPermissions(
                requireActivity(), arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                PERMISSION_LOCATION
            )
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String?>?, grantResults: IntArray) {

//If the permission has been granted...//
        if (requestCode == PERMISSION_LOCATION && grantResults.size == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

//...then start the GPS tracking service//
            startTrackerService()
        } else {

//If the user denies the permission request, then display a toast with some more information//
            Toast.makeText(
                requireContext(),
                "Please enable location services to allow GPS tracking",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun startTrackerService() {
        context?.startService(Intent(requireContext(), TrackingService::class.java))

//Notify the user that tracking has been enabled//
        Toast.makeText(requireContext(), "GPS tracking enabled", Toast.LENGTH_SHORT).show()

//Close MainActivity//
        activity?.finish()
    }

    private fun init(savedInstanceState: Bundle?) {
        mapInit(savedInstanceState)
        binding.btnStartLocationTracking.setOnClickListener { }
    }

    override fun onStart() {
        super.onStart()
        mapView.onStart()
    }

    override fun onResume() {
        super.onResume()
        mapView.onResume()
    }

    override fun onPause() {
        super.onPause()
        mapView.onPause()
    }

    override fun onStop() {
        super.onStop()
        mapView.onStop()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        mapView.onSaveInstanceState(outState)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        mapView.onDestroy()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        mapView.onLowMemory()
    }

    /* Google MapView */
    private fun mapInit(savedInstanceState: Bundle?) {
        mapView = binding.map
        mapView.onCreate(savedInstanceState)
        mapView.getMapAsync(this)
        try {
            MapsInitializer.initialize(requireActivity())
        } catch (e: GooglePlayServicesNotAvailableException) {
            e.printStackTrace()
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        gMap = googleMap
        locationUpdate()
    }

    private fun locationUpdate() {
        /*--DB URL megadása kötelező, különben US centralt keres!--*/
        val database =
            FirebaseDatabase.getInstance("https://qrtrainertruck-default-rtdb.europe-west1.firebasedatabase.app").reference.child(
                "TrainerTruckLocation"
            )
        database.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                marker?.remove()
                updateMap(dataSnapshot)
                Log.d("lU", "OK")
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.e("LU", "Failed to read value.")
            }
        })
    }

    private fun updateMap(dataSnapshot: DataSnapshot) {
        val lat: Double = dataSnapshot.child("latitude").value as Double
        val lng: Double = dataSnapshot.child("longitude").value as Double

        val truckLatLng = LatLng(lat, lng)
        val markerIcon = BitmapFactory.decodeResource(context?.resources, R.mipmap.truckvektor)

        marker = gMap.addMarker(
            MarkerOptions().position(truckLatLng).title("Trainer Truck")
                .icon(BitmapDescriptorFactory.fromBitmap(Bitmap.createScaledBitmap(markerIcon, 80, 60, false)))
        )
        gMap.animateCamera(CameraUpdateFactory.newLatLngZoom(truckLatLng, 16f))
        Toast.makeText(requireContext(), truckLatLng.toString(), Toast.LENGTH_SHORT).show()
    }
}