package com.example.pabili

//import android.R
import android.Manifest
import android.annotation.SuppressLint
import android.content.DialogInterface
import android.content.Intent
import android.content.IntentSender.SendIntentException
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.graphics.Point
import android.location.LocationManager
import android.os.AsyncTask
import android.os.Build
import android.os.Bundle
import android.os.Looper
import android.util.Log
import android.view.View
import android.widget.FrameLayout
import android.widget.ProgressBar
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.example.pabili.Model.SukiStore
import com.example.pabili.Model.SukiStoreMap
import com.firebase.geofire.GeoFireUtils
import com.firebase.geofire.GeoLocation
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.Tasks
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.QuerySnapshot
import com.google.gson.Gson
import com.microsoft.maps.*
import java.util.*


class StoreSelectionMap : AppCompatActivity(), View.OnClickListener {
    private var mMapView: MapView? = null
    //private var btnLocateMe: Button? = null
    private lateinit var progBar: ProgressBar
    private var locationRequest: LocationRequest? = null
    private val userGPS = DoubleArray(2)
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()
    private lateinit var currentUser: String
    private lateinit var currentUserPw: String
    @RequiresApi(api = Build.VERSION_CODES.S)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_store_selection_map)
        locationRequest = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 5000)
            .setWaitForAccurateLocation(false)
            //.setIntervalMillis(10000)
            .setPriority(Priority.PRIORITY_HIGH_ACCURACY)
            .build()
        //try {
        //    SukiStoreMap.initializeMap()
        //} catch (e: Exception) {
        //    throw RuntimeException(e)
        //}
        mMapView = MapView(this, MapRenderMode.VECTOR) // or use MapRenderMode.RASTER for 2D map

        mMapView!!.setCredentialsKey(BuildConfig.CREDENTIALS_KEY)


        (findViewById<View>(R.id.map_view) as FrameLayout).addView(mMapView)
        mMapView!!.onCreate(savedInstanceState)

        var tempFlyout:String = ""
        mMapView!!.addOnMapTappedListener { mapTappedEventArgs ->
            val position: Point = mapTappedEventArgs.position
            val elements: LinkedList<MapElement> =
                mMapView!!.findMapElementsAtOffset(position) as LinkedList<MapElement>
            for (mapElement in elements) {
                println(mapElement)
                if (mapElement is MapIcon) {
                    val mapIcon = mapElement as MapIcon
                    if(tempFlyout == mapIcon.flyout?.title.toString() && tempFlyout != "YOU"){
                        //TODO Go to next activity
                        println("Same Icon was clicked")
                        val id = Integer.parseInt(mapIcon.flyout?.description.toString().replace("id: ",""))
                        openDialog(id,tempFlyout)
                    }
                    // Do your thing. For example set fly out visibility.
                    //mapIcon.isVisible = !mapIcon.isVisible;
                    //mapIcon.flyout?.description = "WOW I was clicked lmao eksdi"
                    //println("Flyout was visible? " + (mapIcon.flyout?.isVisible))
                    println("Icon was clicked")
                    tempFlyout = mapIcon.flyout?.title.toString()
                    Log.d("tempFlyout", tempFlyout)
                }
            }
            false
        }


        //btnLocateMe = findViewById<View>(R.id.btnLocateMe) as Button
        //btnLocateMe!!.setOnClickListener(this)
        currentUser = intent.getStringExtra("currentUser").toString()
        currentUserPw = intent.getStringExtra("currentUserPw").toString()
        userLocation
    }

    fun openDialog(id: Int, storeName: String){
        val mAlertDialogBuilder = AlertDialog.Builder(this)
        val productList = ArrayList<String>()
        //val storeIDList = ArrayList<String>()
        //val scaleUp: Animation = AnimationUtils.loadAnimation(this,R.anim.scale_up)
        //val scaleDown: Animation = AnimationUtils.loadAnimation(this,R.anim.scale_down)
        //btnSelectStore.setOnTouchListener(object: View.OnTouchListener {
        //    override fun onTouch(v: View?, event: MotionEvent?): Boolean{
        //        when (v) { v ->
        //            when (event?.action) { MotionEvent.ACTION_UP -> {
        //                btnSelectStore.startAnimation(scaleDown)
        //            } MotionEvent.ACTION_DOWN -> {
        //                btnSelectStore.startAnimation(scaleUp)
        //            }
        //            }
        //        }
        //        return v?.onTouchEvent(event) ?: true
        //    }
        //})
        db.collection("products/store$id/prices")
            .whereEqualTo("available",true)
            .get()
            .addOnCompleteListener { task ->
                if(task.isSuccessful){
                    for (document in task.result) {
                        var productName = document.data["name"].toString()
                        productList.add(productName)
                        //storeIDList.add(document.data["id"].toString())
                    }
                }
                val productArray = productList.toTypedArray()
                mAlertDialogBuilder.setTitle(storeName)
                mAlertDialogBuilder.setCancelable(true)
                mAlertDialogBuilder.setItems(productArray,DialogInterface.OnClickListener{_,_->})
                //mAlertDialogBuilder.setItems(productList, DialogInterface.OnClickListener{ dialog, index ->
                //    val intent = Intent(this, MainActivity::class.java).apply {
                //        val selStoreName=storeList.get(index).toString()
                //        val selStoreId=storeIDList.get(index).toString()
                //        putExtra("storeId", "$selStoreId");
                //        putExtra("storeName", "$selStoreName");
                //        putExtra("currentUser", "$currentUser");
                //        putExtra("currentUserPw", "$currentUserPw");
                //    }
                //    startActivity(intent)
                //    finish()
                //})
                mAlertDialogBuilder.setPositiveButton("Proceed"){_,_ ->
                    val intent = Intent(this, MainActivity::class.java).apply {
                        //val selStoreName=storeList.get(index).toString()
                        //val selStoreId=storeIDList.get(index).toString()
                        putExtra("storeId", "$id");
                        putExtra("storeName", "$storeName");
                        putExtra("currentUser", "$currentUser");
                        putExtra("currentUserPw", "$currentUserPw");
                    }
                    startActivity(intent)
                    finish()
                }
                mAlertDialogBuilder.setNegativeButton("Cancel") { _, _ ->
                    Toast.makeText(applicationContext,
                        "Canceled", Toast.LENGTH_SHORT).show()
                }
                mAlertDialogBuilder.show()
            }
    }

    override fun onClick(view: View) {
        //when (view.id) {
        //    R.id.btnLocateMe -> {
        //        //getUserLocation();
        //        //Geoposition currentLocation = findGPSLocation(userGPS[0],userGPS[1]);
        //        Log.d("Current Position:", userGPS[0].toString() + " ; " + userGPS[1])
        //        val currentLocation = findGPSLocation(userGPS[0], userGPS[1])
        //        mMapView!!.setScene(MapScene.createFromLocationAndRadius(Geopoint(currentLocation), 200.0), MapAnimationKind.LINEAR)
    //
        //        showSukiStore(SukiStore("YOU", userGPS[0], userGPS[1], 15.0))
        //        DoNearestStore(GeoLocation(userGPS[0], userGPS[1]), 500.0)
    //
        //        //findNearestSukiStore()
        //    }
        //    else -> {}
        //}
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
                                        //println("$latitude | $longtitude")
                                        userGPS[0] = latitude
                                        userGPS[1] = longtitude

                                        Log.d("Current Position:", userGPS[0].toString() + " ; " + userGPS[1])
                                        val currentLocation = findGPSLocation(userGPS[0], userGPS[1])
                                        mMapView!!.setScene(MapScene.createFromLocationAndRadius(Geopoint(currentLocation), 200.0), MapAnimationKind.LINEAR)

                                        showSukiStore(SukiStore("YOU", userGPS[0], userGPS[1], "0"))
                                        DoNearestStore(GeoLocation(userGPS[0], userGPS[1]), 500.0)

                                        //println(userGPS[0].toString() + " ; " + userGPS[1])
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
        showSukiStore(SukiStore("YOU", userGPS[0], userGPS[1], "15")) //15
        //String strGPSLat = String.valueOf(findGPSLocation(14.55836343526571,121.0841731165051).getLatitude());
        //String strGPSLon = String.valueOf(findGPSLocation(14.55836343526571,121.0841731165051).getLongitude());
        //userGPS[0],userGPS[1]
        val strGPSLat = findGPSLocation(userGPS[0], userGPS[1]).latitude.toString()
        val strGPSLon = findGPSLocation(userGPS[0], userGPS[1]).longitude.toString()
        val nearestStoreTask = NearestStoreTask()
        //SaveHash()
        DoNearestStore(GeoLocation(userGPS[0], userGPS[1]),500.0)
        nearestStoreTask.execute(strGPSLat, strGPSLon)
    }

    fun showSukiStore(sukiStore: SukiStore) {
        val icon = MapIcon()
        icon.location = Geopoint(sukiStore.latitude, sukiStore.longitude)
        val elementLayer = MapElementLayer()
        val flyout = MapFlyout()
        if((sukiStore.latitude == userGPS[0]) && (sukiStore.longitude == userGPS[1])){
            val bitmap = BitmapFactory.decodeResource(resources,R.drawable.icon_gps)
            icon.image = MapImage(bitmap)
        }else{
            flyout.description = "id: ${sukiStore.id}"
        }

        Log.d("showSukiStore",sukiStore.storeName)
        flyout.title = sukiStore.storeName.replace("[", "").replace("]", "").replace("\"", "")
        icon.flyout = flyout

        elementLayer.elements.add(icon)
        mMapView!!.layers.add(elementLayer)
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

    //TODO add to register, then delete here
    //fun SaveHash(){
    //    db = FirebaseFirestore.getInstance()
    //    db.collection("stores").get().addOnCompleteListener{task ->
    //        if(task.isSuccessful){
    //            for(doc in task.result){
    //                val gp = doc.getGeoPoint("geopoint")
    //                val lat = gp!!.latitude
    //                val lng = gp!!.longitude
    //                val hash = GeoFireUtils.getGeoHashForLocation(GeoLocation(lat,lng))
    //                val updates: MutableMap<String,Any> = mutableMapOf("geohash" to hash, "lat" to lat, "lng" to lng
    //                )
    //                db.collection("stores").document(doc.id).update(updates).addOnSuccessListener { Log.d("GeoHash","SUCCESS SAVE FOR " + doc.getString("username")) }
    //            }
    //        }
    //    }
    //}

    fun DoNearestStore(center: GeoLocation, radius: Double){
        //db = FirebaseFirestore.getInstance()
        val bounds = GeoFireUtils.getGeoHashQueryBounds(center, radius)
        val tasks: MutableList<Task<QuerySnapshot>> = ArrayList()
        for (b in bounds) {
            val q: Query = db.collection("stores")
                .orderBy("geohash")
                .startAt(b.startHash)
                .endAt(b.endHash)
            tasks.add(q.get())
        }

        Tasks.whenAllComplete(tasks)
            .addOnCompleteListener {
                val matchingDocs: MutableList<DocumentSnapshot> = ArrayList()
                for (task in tasks) {
                    val snap = task.result
                    for (doc in snap!!.documents) {
                        val lat = doc.getDouble("lat")!!
                        val lng = doc.getDouble("lng")!!

                        // We have to filter out a few false positives due to GeoHash
                        // accuracy, but most will match
                        val docLocation = GeoLocation(lat, lng)
                        val distanceInM = GeoFireUtils.getDistanceBetween(docLocation, center)
                        if (distanceInM <= radius) {
                            matchingDocs.add(doc)
                        }
                    }
                }
                for(list in matchingDocs){
                    Log.d("GeoQueries", list.getString("username")+ " : "+list.getGeoPoint("geopoint"))
                    val un = list.getString("username")
                    val lat = list.getGeoPoint("geopoint")!!.latitude
                    val lon = list.getGeoPoint("geopoint")!!.longitude
                    val id = list.data?.get("id")?.toString()

                    val ss = SukiStore(un,lat ,lon,id)
                    showSukiStore(ss)
                }
                progBar = findViewById(R.id.progressBar)
                progBar.visibility = View.GONE
            }
    }

    @SuppressLint("StaticFieldLeak")
    private inner class NearestStoreTask : AsyncTask<String?, String?, String>() {
        override fun doInBackground(vararg params: String?): String {
            var responseBody = ""

            //responseBody = SukiStoreMap.findNearestStore(14.558505450401091, 121.08402743016885, 300.0)
            responseBody = SukiStoreMap.findNearestStore(userGPS[0], userGPS[1], 2000.0)
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
