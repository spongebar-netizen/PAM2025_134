package com.example.projekakhir.utils

import android.content.Context
import android.location.Location
import com.google.android.gms.location.LocationServices
import com.google.android.gms.tasks.Task
import android.annotation.SuppressLint

object LocationUtils {

    fun isWithinCampus(userLat: Double, userLng: Double): Boolean {
        val campusLoc = Location("Campus").apply {
            latitude = Constants.CAMPUS_LAT
            longitude = Constants.CAMPUS_LNG
        }
        val userLoc = Location("User").apply {
            latitude = userLat
            longitude = userLng
        }
        return userLoc.distanceTo(campusLoc) <= Constants.RADIUS_METERS
    }


    @SuppressLint("MissingPermission")
    fun getCurrentLocation(context: Context, onLocationResult: (Location?) -> Unit) {
        try {
            val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
            fusedLocationClient.getCurrentLocation(
                com.google.android.gms.location.Priority.PRIORITY_HIGH_ACCURACY,
                null
            ).addOnSuccessListener { location ->
                onLocationResult(location)
            }.addOnFailureListener {
                onLocationResult(null)
            }
        } catch (e: SecurityException) {
            onLocationResult(null)
        }
    }
}