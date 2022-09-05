package com.example.pabili

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.firebase.firestore.FirebaseFirestore
import android.widget.TextView
import android.widget.Toast
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.content.Intent
import android.view.View

import android.util.Log;
import android.graphics.Bitmap;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.journeyapps.barcodescanner.BarcodeEncoder;

 

class ClaimingActivity: AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_claiming)
 
 
		   val transactionId = intent.getStringExtra("transactionId")
		   
 			val tvTransactionId = findViewById<TextView>(R.id.tvTransactionId)
 			tvTransactionId.text = transactionId
    		//val currentUser = intent.getStringExtra(EXTRA_MESSAGE)
 			val imageView = findViewById<ImageView>(R.id.imageView)
		     val encoder = BarcodeEncoder()
		     val bitmap = encoder.encodeBitmap(transactionId, BarcodeFormat.QR_CODE, 400, 400)
		     imageView.setImageBitmap(bitmap)
			imageView.setImageBitmap(bitmap)
    }
}
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
    
