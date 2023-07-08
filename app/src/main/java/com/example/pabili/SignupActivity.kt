package com.example.pabili

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.firebase.firestore.FirebaseFirestore
import android.widget.TextView
import android.widget.Toast
import android.widget.Button
import android.widget.EditText
import android.content.Intent
import android.util.Log
import java.util.Random
import android.view.View
import TagPrice
import android.Manifest
import android.content.IntentSender
import android.content.pm.PackageManager
import android.location.LocationManager
import android.os.Build
import android.os.Looper
import android.view.MotionEvent
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.core.app.ActivityCompat
import com.firebase.geofire.GeoFireUtils
import com.firebase.geofire.GeoLocation
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*
import com.google.firebase.firestore.GeoPoint

class SignupActivity : AppCompatActivity() {

	//create variable under class
	private lateinit var db: FirebaseFirestore
	private lateinit var etUsername: EditText
	private lateinit var etPassword: EditText
	private lateinit var etVerifyPassword: EditText
	private lateinit var btnCustomerSignup: Button
	private lateinit var btnStoreSignup: Button
	private var locationRequest: LocationRequest? = null
	//private val userGPS = DoubleArray(2)
	private lateinit var location: GeoPoint

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)
		locationRequest = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 10000)
			.setWaitForAccurateLocation(false)
			.setIntervalMillis(10000)
			.setPriority(Priority.PRIORITY_HIGH_ACCURACY)
			.build()

		//set variables
		db = FirebaseFirestore.getInstance()
		etUsername = findViewById(R.id.etUsername) as EditText
		etPassword = findViewById(R.id.etPassword) as EditText
		etVerifyPassword = findViewById(R.id.etVerifyPassword) as EditText
		btnCustomerSignup = findViewById(R.id.btnCustomerSignup)
		btnStoreSignup = findViewById(R.id.btnStoreSignup)

		val scaleUp: Animation = AnimationUtils.loadAnimation(this,R.anim.scale_up)
		val scaleDown: Animation = AnimationUtils.loadAnimation(this,R.anim.scale_down)

		//animation for the buttons
		btnCustomerSignup.setOnTouchListener(object:View.OnTouchListener {
			override fun onTouch(v: View?, event: MotionEvent?): Boolean{
				when (v) { v ->
					when (event?.action) { MotionEvent.ACTION_UP -> {
						btnCustomerSignup.startAnimation(scaleDown)
					} MotionEvent.ACTION_DOWN -> {
						btnCustomerSignup.startAnimation(scaleUp)
					}
					}
				}
				return v?.onTouchEvent(event) ?: true
			}
		})

		btnStoreSignup.setOnTouchListener(object:View.OnTouchListener {
			override fun onTouch(v: View?, event: MotionEvent?): Boolean{
				when (v) { v ->
					when (event?.action) { MotionEvent.ACTION_UP -> {
						btnStoreSignup.startAnimation(scaleDown)
					} MotionEvent.ACTION_DOWN -> {
						btnStoreSignup.startAnimation(scaleUp)
					}
					}
				}
				return v?.onTouchEvent(event) ?: true
			}
		})

		//register customer
		try {
			btnCustomerSignup.setOnClickListener {
				registerCustomer()
			}
			// register store
			btnStoreSignup.setOnClickListener {
				userLocation
				//registerStore()
			}
		} catch (e: IndexOutOfBoundsException) { //if theres an error, toast
			Toast.makeText(this@SignupActivity,"There was an error. Try again",Toast.LENGTH_SHORT).show()
		}
	}

fun registerCustomer(){
	//get username, pass and ver pass
	val username = etUsername.text.toString()
	val password = etPassword.text.toString()
	val verpassword = etVerifyPassword.text.toString()

	//focus on fields that are empty
	if(password != verpassword){
		if(password.isEmpty()){
			Toast.makeText(this@SignupActivity, "Password is Empty", Toast.LENGTH_SHORT).show()
			etPassword.requestFocus()
			return
		}
		Toast.makeText(this@SignupActivity, "Passwords does not match", Toast.LENGTH_SHORT).show()
		etVerifyPassword.requestFocus()
		return
	}

	if(username.isEmpty()) {
		Toast.makeText(this@SignupActivity, "Username is Empty", Toast.LENGTH_SHORT).show()
		etUsername.requestFocus()
		return
	}

	//if no error, put the account to users database
	val account = hashMapOf(
		"username" to username,
		"password" to password,
		"name" to "",
		"email" to "",
		"phone" to "",
		"address" to "",
	)

	db.collection("users")
		.whereEqualTo("username", username)
		.get()
		.addOnSuccessListener { documents ->
			if (documents.isEmpty){
				db.collection("users")
					.add(account)
					.addOnSuccessListener { documentReference ->
						Toast.makeText(this@SignupActivity,"Account Crated",Toast.LENGTH_SHORT).show()
						val intent = Intent(this, LoginActivity::class.java)
						startActivity(intent)
					}
					.addOnFailureListener{
						Toast.makeText(this@SignupActivity, "There was an error in the server", Toast.LENGTH_SHORT).show()
					}
			}else{
				Toast.makeText(this@SignupActivity, "Account was taken. Change your Username", Toast.LENGTH_SHORT).show()
				etUsername.requestFocus()
			}
		}
		.addOnFailureListener{
			Toast.makeText(this@SignupActivity, "There was an error in the server", Toast.LENGTH_SHORT).show()
		}
	}
	//fun SaveHash(){
	//	db = FirebaseFirestore.getInstance()
	//	db.collection("stores").get().addOnCompleteListener{task ->
	//		if(task.isSuccessful){
	//			for(doc in task.result){
	//				val gp = doc.getGeoPoint("geopoint")
	//				val lat = gp!!.latitude
	//				val lng = gp!!.longitude
	//				val hash = GeoFireUtils.getGeoHashForLocation(GeoLocation(lat,lng))
	//				val updates: MutableMap<String,Any> = mutableMapOf("geohash" to hash, "lat" to lat, "lng" to lng
	//				)
	//				db.collection("stores").document(doc.id).update(updates).addOnSuccessListener { Log.d("GeoHash","SUCCESS SAVE FOR " + doc.getString("username")) }
	//			}
	//		}
	//	}
	//}
	fun registerStore(){
		//create id for store
		var id:Int = Random().nextInt(10000);

		Toast.makeText(this@SignupActivity, "id:$id", Toast.LENGTH_SHORT).show()

		//get necessary values
		val username = etUsername.text.toString()
		val password = etPassword.text.toString()
		val verpassword = etVerifyPassword.text.toString()
		val lat = location.latitude
		val lng = location.longitude
		val hash = GeoFireUtils.getGeoHashForLocation(GeoLocation(lat,lng))

		//focus on field that is empty
		if(password != verpassword){
			if(password.isEmpty()){
				Toast.makeText(this@SignupActivity, "Password is Empty", Toast.LENGTH_SHORT).show()
				etPassword.requestFocus()
				return
			}
			Toast.makeText(this@SignupActivity, "Passwords does not match", Toast.LENGTH_SHORT).show()
			etVerifyPassword.requestFocus()
			return
		}
		if(username.isEmpty()) {
			Toast.makeText(this@SignupActivity, "Username is Empty", Toast.LENGTH_SHORT).show()
			etUsername.requestFocus()
			return
		}

		//get store's current location


		//if no error, add account to store in the database
		val account = hashMapOf(
			"username" to username,
			"password" to password,
			"id" to id,
			"geopoint" to location,
			"geohash" to hash,
			"lat" to lat,
			"lng" to lng
		)

		//set price under SRP
		db.collection("products/SRP/prices").get().addOnSuccessListener {
			documents ->
			if (documents.isEmpty){
				Toast.makeText(this@SignupActivity,"empty document",Toast.LENGTH_SHORT).show()
					
			}
				for(document in documents){
					val tagPrice = document.toObject<TagPrice>(TagPrice::class.java)
					db.collection("products/store$id/prices").add(tagPrice)
					Toast.makeText(this@SignupActivity,tagPrice.name,Toast.LENGTH_SHORT).show()
							
				}
		}

		//add account to database
		db.collection("stores")
			.whereEqualTo("username", username)
			.get()
			.addOnSuccessListener { documents ->
				if (documents.isEmpty){
					db.collection("stores")
						.add(account)
						.addOnSuccessListener { documentReference ->
							Toast.makeText(this@SignupActivity,"Store Crated",Toast.LENGTH_SHORT).show()
							val intent = Intent(this, LoginActivity::class.java)
							startActivity(intent)
						}
						.addOnFailureListener{
							Toast.makeText(this@SignupActivity, "There was an error in the server", Toast.LENGTH_SHORT).show()
						}
				}else{
					Toast.makeText(this@SignupActivity, "Store was taken. Change your Username", Toast.LENGTH_SHORT).show()
					etUsername.requestFocus()
				}

			}
			.addOnFailureListener{
				Toast.makeText(this@SignupActivity, "There was an error in the server", Toast.LENGTH_SHORT).show()
			}
	}

	private val userLocation: Unit
		private get() {
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
				println("SDK Correct")
				if (ActivityCompat.checkSelfPermission(this@SignupActivity, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
					println("Permission Granted")
					if (isGPSEnabled) {
						println("GPS Enabled")
						//System.out.println((location));
						LocationServices.getFusedLocationProviderClient(this@SignupActivity)
							.requestLocationUpdates(locationRequest!!, object : LocationCallback() {
								override fun onLocationResult(locationResult: LocationResult) {
									super.onLocationResult(locationResult)
									LocationServices.getFusedLocationProviderClient(this@SignupActivity)
										.removeLocationUpdates(this)
									println("onLocationResult method")
									if (locationResult != null && locationResult.locations.size > 0) {
										val index = locationResult.locations.size - 1
										val latitude = locationResult.locations[index].latitude
										val longtitude = locationResult.locations[index].longitude
										println("USER GPS ACQUIRED")
										println("$latitude | $longtitude")
										location = GeoPoint(latitude,longtitude)
										registerStore()
										//userGPS[0] = latitude
										//userGPS[1] = longtitude
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
				Toast.makeText(this@SignupActivity, "GPS is already tured on", Toast.LENGTH_SHORT).show()
			} catch (e: ApiException) {
				when (e.statusCode) {
					LocationSettingsStatusCodes.RESOLUTION_REQUIRED -> try {
						val resolvableApiException = e as ResolvableApiException
						resolvableApiException.startResolutionForResult(this@SignupActivity, 2)
					} catch (ex: IntentSender.SendIntentException) {
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
}



    
