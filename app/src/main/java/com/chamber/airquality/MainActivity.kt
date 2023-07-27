 package com.chamber.airquality

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.LocationManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.chamber.airquality.databinding.ActivityMainBinding
import java.io.IOException
import java.util.Locale

 class MainActivity : AppCompatActivity() {

    lateinit var binding : ActivityMainBinding
    lateinit var locationProvider: LocationProvider

    private val PERMISSIONS_REQUEST_CODE = 100

     val REQUIRED_PERMISSIONS = arrayOf(
         Manifest.permission.ACCESS_FINE_LOCATION,
         Manifest.permission.ACCESS_COARSE_LOCATION
     )

     lateinit var getGPSPermissionLauncher : ActivityResultLauncher<Intent>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        checkAllPermissions()
        updateUI()
    }

     private fun updateUI() {
         locationProvider = LocationProvider(this@MainActivity)

         val latitude: Double? = locationProvider.getLocationLatitude()
         val longitude: Double? = locationProvider.getLocationLongitude()

         if(latitude != null && longitude != null){
             // 1. 현재 위치 가져오고 UI 업데이트
             val address = getCurrentAddress(latitude, longitude)

             address?.let {
                 binding.tvLocationTitle.text = "${it.thoroughfare}"
                 binding.tvLocationContent.text= "${it.countryName} ${it.adminArea}"
             }
             // 2. 미세먼지 농도 가져오고 UI 업데이트

         }else{
             Toast.makeText(this, R.string.no_fetch_location_info, Toast.LENGTH_LONG).show()
         }
     }

     private fun getCurrentAddress(latitude: Double, longitude: Double) : Address? {
         val geoCoder = Geocoder(this, Locale.KOREA)

         val addresses : List<Address>?

         addresses = try{
             geoCoder.getFromLocation(latitude, longitude,7)
         }catch (ioException: IOException){
             Toast.makeText(this, R.string.impossible_geocoder_service,Toast.LENGTH_LONG).show()
             return null
         }catch (illegalArgumentException : java.lang.IllegalArgumentException){
             Toast.makeText(this, R.string.incurrect_location_info,Toast.LENGTH_LONG).show()
             return null
         }

         if(addresses == null || addresses.size == 0){
             Toast.makeText(this,R.string.not_found_address, Toast.LENGTH_LONG).show()
             return null
         }

         return addresses[0]
     }



     private fun checkAllPermissions() {
         if(isLocationServicesAvailable()){
             isRunTimePermissionsGranted()
         }else{
             showDialogForLocationServiceSetting()
         }
     }

     private fun isLocationServicesAvailable(): Boolean {
         val locationManager = getSystemService(LOCATION_SERVICE) as LocationManager

         return (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER))
     }

     private fun isRunTimePermissionsGranted() {
         val hasFineLocationPermission = ContextCompat.checkSelfPermission(this@MainActivity, Manifest.permission.ACCESS_FINE_LOCATION)
         val hasCoarseLocationPermission = ContextCompat.checkSelfPermission(this@MainActivity, Manifest.permission.ACCESS_COARSE_LOCATION)

         if(hasFineLocationPermission != PackageManager.PERMISSION_GRANTED || hasCoarseLocationPermission != PackageManager.PERMISSION_GRANTED){
             ActivityCompat.requestPermissions(this@MainActivity, REQUIRED_PERMISSIONS, PERMISSIONS_REQUEST_CODE)
         }
     }

     override fun onRequestPermissionsResult(
         requestCode: Int,
         permissions: Array<out String>,
         grantResults: IntArray
     ) {
         super.onRequestPermissionsResult(requestCode, permissions, grantResults)
         if(requestCode == PERMISSIONS_REQUEST_CODE && grantResults.size == REQUIRED_PERMISSIONS.size){
             var checkResult = true

             for(result in grantResults){
                 if(result != PackageManager.PERMISSION_GRANTED){
                     checkResult = false
                     break
                 }
             }

             if(checkResult){
                 // 위치값을 가져올 수 있음
                 updateUI()
             }else{
                 Toast.makeText(this@MainActivity, R.string.all_permission_check, Toast.LENGTH_LONG).show()
                 finish()
             }
         }
     }

     private fun showDialogForLocationServiceSetting() {
         // definition: getGPSPermissionLauncher
         getGPSPermissionLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){
             result -> if(result.resultCode == Activity.RESULT_OK){
                 if(isLocationServicesAvailable()){
                     isRunTimePermissionsGranted()
                 }else{
                     Toast.makeText(this@MainActivity, R.string.no_location_service, Toast.LENGTH_LONG).show()
                 }
             }
         }

         val builder : AlertDialog.Builder = AlertDialog.Builder(this@MainActivity)
         builder.setTitle(R.string.deactivate_location_service)
         builder.setMessage(R.string.non_consent_location_service)
         builder.setCancelable(true)
         builder.setPositiveButton(R.string.setting, DialogInterface.OnClickListener { dialogInterface, i ->
             val callGPSSettingIntent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
             getGPSPermissionLauncher.launch(callGPSSettingIntent)
         })

         builder.setNegativeButton(R.string.cancel, DialogInterface.OnClickListener { dialogInterface, i ->
             dialogInterface.cancel()
             Toast.makeText(this@MainActivity, R.string.unavailable_location_service, Toast.LENGTH_LONG).show()
             finish()
         })

         builder.create().show()
     }

 }