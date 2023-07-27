package com.chamber.airquality

import android.content.Context
import android.location.Location
import android.location.LocationManager

class LocationProvider(val context: Context) {
    private var location : Location? =null
    private var locationManager: LocationManager? =null

    init {
        getLotion()
    }

    private fun getLotion() : Location? {
        try {
            locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager

            var gpsLocation : Location? = null
            var networkLocation : Location? = null

            // GPS 또는 Network가 활성화가 되었는지 확인

            val isGPSEnabled = locationManager!!.isProviderEnabled(LocationManager.GPS_PROVIDER)
            val isNetworkEnabled = locationManager!!.isProviderEnabled(LocationManager.NETWORK_PROVIDER)


        }
    }

}