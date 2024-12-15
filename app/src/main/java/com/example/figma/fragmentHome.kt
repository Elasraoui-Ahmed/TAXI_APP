package com.example.figma

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.os.Handler
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.google.android.gms.location.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.BitmapDescriptorFactory

class FragmentHome : Fragment(), OnMapReadyCallback {
    private lateinit var mMap: GoogleMap
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var locationRequest: LocationRequest
    private lateinit var locationCallback: LocationCallback
    private var currentLocationMarker: Marker? = null

    private lateinit var playPauseButton: Button
    private var isPlaying = false

    // Fare calculation variables
    private val baseFare: Double = 2.5
    private val farePerKm: Double = 1.5
    private val farePerMinute: Double = 0.5

    private var totalFare: Double = 0.0
    private var startLocation: Location? = null
    private var previousLocation: Location? = null
    private var elapsedTimeInMinutes: Double = 0.0
    private var startTime: Long = 0
    private var distanceTraveled: Double = 0.0
    private var handler = Handler()
    private var runnable: Runnable? = null

    private lateinit var distanceTimeInfoTextView: TextView
    private val NOTIFICATION_CHANNEL_ID = "ride_channel"
    private val NOTIFICATION_ID = 1

    private lateinit var totalFareTextView: TextView

    @SuppressLint("MissingInflatedId")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_home, container, false)

        createNotificationChannel()

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())

        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        playPauseButton = view.findViewById(R.id.playPauseButton)
        playPauseButton.setOnClickListener {
            togglePlayPause()
        }

        setupLocationRequest()

        distanceTimeInfoTextView = view.findViewById(R.id.distanceTimeInfo)

        totalFareTextView = view.findViewById(R.id.totalFare)

        return view
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        mMap.mapType = GoogleMap.MAP_TYPE_NORMAL

        if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            mMap.isMyLocationEnabled = true
        } else {
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                1
            )
        }
    }

    private fun setupLocationRequest() {
        locationRequest = LocationRequest.create().apply {
            interval = 5000 // Update location every 5 seconds
            fastestInterval = 2000 // Accept location updates as fast as every 2 seconds
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        }

        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                locationResult.lastLocation?.let { location ->
                    updateLocation(location)
                }
            }
        }
    }

    private fun getCurrentLocation() {
        if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
                if (location != null) {
                    val currentLatLng = LatLng(location.latitude, location.longitude)

                    // Move the camera and add/update the taxi marker
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 15f))
                    if (currentLocationMarker == null) {
                        currentLocationMarker = mMap.addMarker(
                            MarkerOptions()
                                .position(currentLatLng)
                                .title(getString(R.string.driver))
                                .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_taxi))  // Custom Taxi Icon
                        )
                    } else {
                        currentLocationMarker?.position = currentLatLng
                    }

                    Log.d("Location", "Latitude: ${location.latitude}, Longitude: ${location.longitude}")
                } else {
                    Toast.makeText(requireContext(), getString(R.string.unable_to_get_location), Toast.LENGTH_SHORT).show()
                }
            }
        } else {
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                1
            )
        }
    }

    private fun togglePlayPause() {
        if (isPlaying) {
            isPlaying = false
            playPauseButton.text = getString(R.string.play)
            playPauseButton.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.vertt))
            stopRide()
            totalFareTextView.text = getString(R.string.total_fare, totalFare)
            showRideDetailsNotification()
        } else {
            totalFare = 0.0
            totalFareTextView.text = getString(R.string.total_fare, totalFare)
            isPlaying = true
            playPauseButton.text = getString(R.string.pause)
            playPauseButton.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.vertt))
            startRide()
        }
    }

    private fun startRide() {
        distanceTraveled = 0.0
        elapsedTimeInMinutes = 0.0
        startTime = System.currentTimeMillis()
        startLocation = null
        previousLocation = null
        startTimer()

        if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, requireActivity().mainLooper)
        } else {
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                1
            )
        }

        Toast.makeText(requireContext(), getString(R.string.ride_started), Toast.LENGTH_SHORT).show()
    }

    private fun stopRide() {
        fusedLocationClient.removeLocationUpdates(locationCallback)
        stopTimer()
        calculateFare()
    }

    private fun updateLocation(location: Location) {
        if (isPlaying) {
            previousLocation?.let {
                val distance = it.distanceTo(location) / 1000.0
                distanceTraveled += distance
            }
            previousLocation = location
        }
        val currentLatLng = LatLng(location.latitude, location.longitude)
        currentLocationMarker?.position = currentLatLng
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 15f))
    }

    private fun calculateFare() {
        elapsedTimeInMinutes = (System.currentTimeMillis() - startTime) / 60000.0
        totalFare = baseFare + (distanceTraveled * farePerKm) + (elapsedTimeInMinutes * farePerMinute)
        Toast.makeText(requireContext(), getString(R.string.total_fare, totalFare), Toast.LENGTH_LONG).show()
    }

    private fun startTimer() {
        if (runnable == null) {
            runnable = object : Runnable {
                @SuppressLint("StringFormatMatches")
                override fun run() {
                    val elapsedMillis = System.currentTimeMillis() - startTime
                    val elapsedMinutes = elapsedMillis / 60000
                    val elapsedSeconds = (elapsedMillis % 60000) / 1000

                    val formattedTime = String.format("%02d:%02d", elapsedMinutes, elapsedSeconds)
                    val updatedText = getString(R.string.distance_time, distanceTraveled, formattedTime)

                    activity?.runOnUiThread {
                        distanceTimeInfoTextView.text = updatedText
                    }

                    handler.postDelayed(this, 1000)
                }
            }
        }
        handler.post(runnable!!)
    }

    private fun stopTimer() {
        runnable?.let {
            handler.removeCallbacks(it)
        }
        runnable = null
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = getString(R.string.ride_details)
            val descriptionText = getString(R.string.ride_details)
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(NOTIFICATION_CHANNEL_ID, name, importance).apply {
                description = descriptionText
            }
            val notificationManager: NotificationManager =
                requireContext().getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    @SuppressLint("DefaultLocale")
    private fun showRideDetailsNotification() {
        elapsedTimeInMinutes = (System.currentTimeMillis() - startTime) / 60000.0
        totalFare = baseFare + (distanceTraveled * farePerKm) + (elapsedTimeInMinutes * farePerMinute)

        val notification = NotificationCompat.Builder(requireContext(), NOTIFICATION_CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_location)
            .setContentTitle(getString(R.string.ride_details))
            .setContentText(getString(R.string.total_fare, totalFare) + "\n" + getString(R.string.time, elapsedTimeInMinutes))
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .build()

        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // Request permission if not granted
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                2
            )
            return
        }

        NotificationManagerCompat.from(requireContext()).notify(NOTIFICATION_ID, notification)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 1 && grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            getCurrentLocation()
        } else if (requestCode == 2 && grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            showRideDetailsNotification()
        }
    }
}
