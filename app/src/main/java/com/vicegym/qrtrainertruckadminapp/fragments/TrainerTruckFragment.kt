package com.vicegym.qrtrainertruckadminapp.fragments

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

class TrainerTruckFragment : Fragment(), OnMapReadyCallback {

    private lateinit var binding: FragmentTrainerTruckBinding
    private lateinit var mapView: MapView
    private lateinit var gMap: GoogleMap
    private var marker: Marker? = null

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
    }

    private fun init(savedInstanceState: Bundle?) {
        mapInit(savedInstanceState)
        binding.btnStartLocationTracking.setOnClickListener { }
    }

    companion object {
        @JvmStatic
        fun newInstance() =
            TrainerTruckFragment()
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