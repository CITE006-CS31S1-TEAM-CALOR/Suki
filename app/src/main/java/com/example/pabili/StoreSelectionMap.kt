package com.example.pabili

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.Manifest
import android.content.IntentSender.SendIntentException
import android.content.pm.PackageManager
import android.location.LocationManager
import android.os.AsyncTask
import android.os.Build
import android.os.Looper
import android.view.View
import android.widget.Button
import android.widget.FrameLayout
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import com.example.pabili.Model.SukiStore
import com.example.pabili.Model.SukiStoreMap

import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*
import com.google.gson.Gson
import com.microsoft.maps.*


class StoreSelectionMap : AppCompatActivity(), View.OnClickListener {
    private var mMapView: MapView? = null
    private var btnLocateMe: Button? = null
    private var locationRequest: LocationRequest? = null
    private val userGPS = DoubleArray(2)
    @RequiresApi(api = Build.VERSION_CODES.S)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_store_selection_map)
        locationRequest = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 10000)
            .setWaitForAccurateLocation(false)
            .setIntervalMillis(10000)
            .setPriority(Priority.PRIORITY_HIGH_ACCURACY)
            .build()
        try {
            SukiStoreMap.initializeMap()
        } catch (e: Exception) {
            throw RuntimeException(e)
        }
        mMapView = MapView(this, MapRenderMode.VECTOR) // or use MapRenderMode.RASTER for 2D map

        mMapView!!.setCredentialsKey(BuildConfig.CREDENTIALS_KEY)

        (findViewById<View>(R.id.map_view) as FrameLayout).addView(mMapView)
        mMapView!!.onCreate(savedInstanceState)
        btnLocateMe = findViewById<View>(R.id.btnLocateMe) as Button
        btnLocateMe!!.setOnClickListener(this)
        userLocation
    }

    override fun onClick(view: View) {
        when (view.id) {
            R.id.btnLocateMe -> {
                //getUserLocation();
                //Geoposition currentLocation = findGPSLocation(userGPS[0],userGPS[1]);
                println(userGPS[0].toString() + " ; " + userGPS[1])
                val currentLocation = findGPSLocation(userGPS[0], userGPS[1])
                mMapView!!.setScene(MapScene.createFromLocationAndRadius(Geopoint(currentLocation), 200.0), MapAnimationKind.LINEAR)
                findNearestSukiStore()
            }
            else -> {}
        }
    }

    //System.out.println((location));
    private val userLocation: Unit
        private get() {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                println("SDK Correct")
                if (ActivityCompat.checkSelfPermission(this@StoreSelectionMap, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    println("Permission Granted")
                    if (isGPSEnabled) {
                        println("GPS Enabled")
                        //System.out.println((location));
                        LocationServices.getFusedLocationProviderClient(this@StoreSelectionMap)
                            .requestLocationUpdates(locationRequest!!, object : LocationCallback() {
                                override fun onLocationResult(locationResult: LocationResult) {
                                    super.onLocationResult(locationResult)
                                    LocationServices.getFusedLocationProviderClient(this@StoreSelectionMap)
                                        .removeLocationUpdates(this)
                                    println("onLocationResult method")
                                    if (locationResult != null && locationResult.locations.size > 0) {
                                        val index = locationResult.locations.size - 1
                                        val latitude = locationResult.locations[index].latitude
                                        val longtitude = locationResult.locations[index].longitude
                                        println("USER GPS ACQUIRED")
                                        println("$latitude | $longtitude")
                                        userGPS[0] = latitude
                                        userGPS[1] = longtitude
                                        println(userGPS[0].toString() + " ; " + userGPS[1])
                                    }
                                }
                            }, Looper.getMainLooper())
                    } else {
                        turnOnGPS()
                    }
                } else {
                    requestPermissions(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 1)
                }
            }
        }

    private fun turnOnGPS() {
        val builder = LocationSettingsRequest.Builder()
            .addLocationRequest(locationRequest!!)
        builder.setAlwaysShow(true)
        val result = LocationServices.getSettingsClient(applicationContext)
            .checkLocationSettings(builder.build())
        result.addOnCompleteListener { task ->
            try {
                val response = task.getResult(ApiException::class.java)
                Toast.makeText(this@StoreSelectionMap, "GPS is already tured on", Toast.LENGTH_SHORT).show()
            } catch (e: ApiException) {
                when (e.statusCode) {
                    LocationSettingsStatusCodes.RESOLUTION_REQUIRED -> try {
                        val resolvableApiException = e as ResolvableApiException
                        resolvableApiException.startResolutionForResult(this@StoreSelectionMap, 2)
                    } catch (ex: SendIntentException) {
                        ex.printStackTrace()
                    }
                    LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE -> {}
                }
            }
        }
    }

    private val isGPSEnabled: Boolean
        private get() {
            var locMan: LocationManager? = null
            var isEnabled = false
            if (locMan == null) {
                locMan = getSystemService(LOCATION_SERVICE) as LocationManager
            }
            isEnabled = locMan!!.isProviderEnabled(LocationManager.GPS_PROVIDER)
            return isEnabled
        }

    fun findNearestSukiStore() {
        //showSukiStore(new SukiStore("My Dummy Location", 14.55836343526571, 121.0841731165051, 15));
        showSukiStore(SukiStore("My Dummy Location", userGPS[0], userGPS[1], 15.0))
        //String strGPSLat = String.valueOf(findGPSLocation(14.55836343526571,121.0841731165051).getLatitude());
        //String strGPSLon = String.valueOf(findGPSLocation(14.55836343526571,121.0841731165051).getLongitude());
        //userGPS[0],userGPS[1]
        val strGPSLat = findGPSLocation(userGPS[0], userGPS[1]).latitude.toString()
        val strGPSLon = findGPSLocation(userGPS[0], userGPS[1]).longitude.toString()
        val nearestStoreTask = NearestStoreTask()
        nearestStoreTask.execute(strGPSLat, strGPSLon)
    }

    fun showSukiStore(sukiStore: SukiStore) {
        val icon = MapIcon()
        icon.location = Geopoint(sukiStore.latitude, sukiStore.longitude)
        val elementLayer = MapElementLayer()
        elementLayer.elements.add(icon)
        mMapView!!.layers.add(elementLayer)
        val flyout = MapFlyout()
        flyout.title = sukiStore.storeName.replace("[", "").replace("]", "").replace("\"", "")
        flyout.description = "[Detail]"
        icon.flyout = flyout
    }

    override fun onStart() {
        super.onStart()
        mMapView!!.onStart()
    }

    override fun onResume() {
        super.onResume()
        mMapView!!.onResume()
    }

    override fun onPause() {
        super.onPause()
        mMapView!!.onPause()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        mMapView!!.onSaveInstanceState(outState)
    }

    override fun onStop() {
        super.onStop()
        mMapView!!.onStop()
    }

    override fun onDestroy() {
        super.onDestroy()
        mMapView!!.onDestroy()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        mMapView!!.onLowMemory()
    }

    private inner class NearestStoreTask : AsyncTask<String?, String?, String>() {
        override fun doInBackground(vararg params: String?): String {
            var responseBody = ""

            responseBody = SukiStoreMap.findNearestStore(14.558505450401091, 121.08402743016885, 300.0)
            // Deserializing JSON List<SukiStore>
            val arrSukiStores = Gson().fromJson(responseBody, Array<SukiStore>::class.java)
            //responseBody = arrSukiStores[0].getStoreName();
            if (arrSukiStores == null){
                return "No SukiStores nearby"
            }
            for (sukiStore in arrSukiStores) {
                showSukiStore(sukiStore)
            }
            return responseBody
        }


        override fun onPostExecute(s: String) {}
    }

    companion object {
        fun findGPSLocation(latitude: Double, longtitude: Double): Geoposition {
            // dummy data for the meantime because Anbox emulator don't have GPS
            //return new Geoposition(14.558363435265706, 121.08417311650506);
            //14.6051137979 | 120.98134164
            return Geoposition(latitude, longtitude)
            //return new Geoposition(14.6051137979 , 120.98134164);
        }
    }
}