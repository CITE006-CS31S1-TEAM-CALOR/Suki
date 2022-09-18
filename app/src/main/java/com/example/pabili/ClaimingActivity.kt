package com.example.pabili

import android.graphics.Bitmap
import android.media.MediaScannerConnection
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore
import com.google.zxing.BarcodeFormat
import com.journeyapps.barcodescanner.BarcodeEncoder
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.util.*

class ClaimingActivity: AppCompatActivity() {

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
		val editBtn = findViewById<Button>(R.id.btnEdit)
		editBtn.setOnClickListener{
			super.finish()
		}
		val saveQR = findViewById<Button>(R.id.btnSaveQr)
		saveQR.setOnClickListener{
			saveImage(bitmap)
		}
		db.collection("orders").whereEqualTo("transactionID", transactionId)
			.get()
			.addOnSuccessListener { result ->
				if(result.isEmpty){
					Toast.makeText(this, ("No orders received"), Toast.LENGTH_SHORT).show()
				} else{
					for(document in result){
						val cDoc = document.id
						val total = document.data["totalPrice"].toString()
						totaltxt.text = "Total: " + total
						val store = document.data["store"].toString()
						storeN.text = "Store: " + store
						val s = document.data["status"].toString()
						status.text = "Status: " + s
						if(s.lowercase() == "ready"){
							editBtn.setVisibility(View.GONE)
						}
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
						val adapter = RecyclerClaiming(this, cDoc, data, totaltxt)
						recyclerView.adapter = adapter
					}
				}
			}


	}

	fun saveImage(myBitmap: Bitmap){
		val bytes = ByteArrayOutputStream()
		myBitmap.compress(Bitmap.CompressFormat.JPEG, 90, bytes)
		val wallpaperDirectory = File(
			Environment.getExternalStorageDirectory(), Environment.DIRECTORY_PICTURES
		)
		// have the object build the directory structure, if needed.
		if (!wallpaperDirectory.exists()) {
			Toast.makeText(this, ("Current Directory does not exist, creating one."), Toast.LENGTH_LONG).show()
			wallpaperDirectory.mkdirs()
		}
		try {
			val f = File(
				wallpaperDirectory, Calendar.getInstance()
					.timeInMillis.toString() + ".jpg"
			)
			f.createNewFile() //give read write permission
			val fo = FileOutputStream(f)
			fo.write(bytes.toByteArray())
			MediaScannerConnection.scanFile(this, arrayOf(f.path), arrayOf("image/jpeg"), null)
			fo.close()
			Toast.makeText(this, ("File Saved at: " + f.absolutePath), Toast.LENGTH_LONG).show()
		} catch (e1: IOException) {
			Toast.makeText(this, ("No Read/Write Permission"), Toast.LENGTH_LONG).show()
		}
	}
}

    
