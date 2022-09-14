package com.example.pabili

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import com.google.firebase.firestore.FirebaseFirestore
import android.widget.TextView
import android.widget.ImageView

import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.zxing.BarcodeFormat;
import com.journeyapps.barcodescanner.BarcodeEncoder;

 

class ClaimingActivity: AppCompatActivity() {

	private lateinit var cInfo: DataCustomerInfo
    override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setContentView(R.layout.activity_claiming)

		val db = FirebaseFirestore.getInstance()


		val transactionId = intent.getStringExtra("transactionId")
		val tvTransactionId = findViewById<TextView>(R.id.tvTransactionId)
		tvTransactionId.text = transactionId
		val imageView = findViewById<ImageView>(R.id.imageView)
		val encoder = BarcodeEncoder()
		val bitmap = encoder.encodeBitmap(transactionId, BarcodeFormat.QR_CODE, 400, 400)
		imageView.setImageBitmap(bitmap)
		imageView.setImageBitmap(bitmap)
		val recyclerView = findViewById<RecyclerView>(R.id.storeCustomerOrderClaiming)
		recyclerView.layoutManager = LinearLayoutManager(this)
		val data = ArrayList<DataOrderList>()
		val totaltxt = findViewById<TextView>(R.id.totalText)
		val status = findViewById<TextView>(R.id.txtStatus)
		val storeN = findViewById<TextView>(R.id.txtStore)

		db.collection("orders").whereEqualTo("transactionID", transactionId)
			.get()
			.addOnSuccessListener { result ->
				if(result.isEmpty){
					Log.d("FAIL", "No data")
				} else{
					for(document in result){
						val cDoc = document.id
						val total = document.data["totalPrice"].toString()
						totaltxt.text = "Total: " + total
						val store = document.data["store"].toString()
						storeN.text = "Store: " + store
						val s = document.data["status"].toString()
						status.text = "Status: " + s
						var computedPrices = document.data["computedPrices"].toString()
						var oL = document.data["orderList"].toString()
						val orderListArray = oL.split(", ")
						val cPricesArray = computedPrices.split(", ")
						for((refComp, order) in orderListArray!!.withIndex()){
							if(refComp != orderListArray.size - 1){
								val name = order.replace("\\d+".toRegex(), "")
								val qty = order.replace(" [a-z]+".toRegex(), "")
								val com = cPricesArray[refComp]
								data.add(DataOrderList(name, "x" + qty, "P" + com))
							}
						}
						val adapter = RecyclerOrder(this, cDoc, data, totaltxt)
						recyclerView.adapter = adapter
					}
				}
			}

			val editBtn = findViewById<Button>(R.id.btnEdit)

			editBtn.setOnClickListener{
				super.finish()
			}


	}



}



//			db.collection("orders")
//				.get()
//				.addOnCompleteListener {
//					task ->
//						if(task.isSuccessful){
//							for(document in task.result){
//
//							}
//
//
//
//							val adapter = RecyclerOrder(data)
//							recyclerView.adapter = adapter
//						}
//					Log.d("TAG", "Orders: $oL")
//					}





	//try {]	
		/*
		val db = FirebaseFirestore.getInstance()
		val etUsername: EditText = findViewById(R.id.etUsername) as EditText 
		val etPassword: EditText = findViewById(R.id.etPassword) as EditText 
		val btnCustomerLogin: Button = findViewById(R.id.btnCustomerLogin)
		val btnStoreLogin: Button = findViewById(R.id.btnStoreLogin)

		btnCustomerLogin.setOnClickListener {
			var username = etUsername.text.toString()
			var password = etPassword.text.toString()
			db.collection("users")
			.whereEqualTo("username", username).whereEqualTo("password", password)
			.get()
			.addOnSuccessListener { documents ->
			    for (document in documents) {
				Toast.makeText(this@LoginActivity,'Access Granted',Toast.LENGTH_SHORT).show()
				 val intent = Intent(this, MainActivity::class.java).apply {
					    putExtra(EXTRA_MESSAGE, username)
				 }
				 startActivity(intent)				
			    }
			}
		}
		
		btnStoreLogin.setOnClickListener {
			var username = etUsername.text.toString()
			var password = etPassword.text.toString()
			db.collection("stores")
			.whereEqualTo("username", username).whereEqualTo("password", password)
			.get()
			.addOnSuccessListener { documents ->
			    for (document in documents) {
				Toast.makeText(this@LoginActivity,"Access Granted",Toast.LENGTH_SHORT).show()
			    }
			}
		}
		
		
			
	} catch (e: IOException) {
		Toast.makeText(this@LoginActivity,"ttt",Toast.LENGTH_SHORT).show()
	}*/
    
