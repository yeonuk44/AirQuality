package com.chamber.airquality

import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import androidx.core.app.ActivityCompat
import java.lang.Exception

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

            if(!isGPSEnabled && !isNetworkEnabled){
                return null
            }else{
                if(ActivityCompat.checkSelfPermission(
                        context,
                        android.Manifest.permission.ACCESS_FINE_LOCATION
                    ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                        context, android.Manifest.permission.ACCESS_COARSE_LOCATION
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    return null
                }

                if(isNetworkEnabled){
                    networkLocation = locationManager?.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)
                }

                if(isGPSEnabled){
                    gpsLocation = locationManager?.getLastKnownLocation(LocationManager.GPS_PROVIDER)
                }

                if(gpsLocation != null && networkLocation !=null){
                    /**
                     * INFO:
                     * accuracy function을 통해 gps와 network 중 정확도 포인트가 높은 위치를 채택함
                     */
                    if(gpsLocation.accuracy > networkLocation.accuracy){
                        location = gpsLocation
                    }else{
                        location = networkLocation
                    }
                }else{
                    if(gpsLocation != null){
                        location = gpsLocation
                    }
                    if(networkLocation != null){
                        location = networkLocation
                    }
                }
            }
        }catch (e: Exception){
            e.printStackTrace()
        }
        return location
    }
    /**
     * INFO:
     * 위도(Latitude)와 경도(longitude)의 값을 조회하는 함수
     */
    fun getLocationLatitude() : Double {
        return location?.latitude ?: 0.0
    }

    fun getLocationLongitude() : Double {
        return location?.longitude ?: 0.0
    }
}