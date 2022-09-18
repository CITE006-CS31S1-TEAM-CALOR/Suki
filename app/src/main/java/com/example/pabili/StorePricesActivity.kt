	package com.example.pabili

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Parcel
import android.os.Parcelable
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore
import android.content.Intent
import TagPrice
import android.app.AlertDialog
import android.app.Dialog
import java.util.Date
import java.text.SimpleDateFormat
import java.lang.System
import android.view.LayoutInflater
import android.view.ViewGroup
import android.text.Editable
import android.text.TextWatcher
import android.widget.*
import java.util.Random

class StorePricesActivity : AppCompatActivity() {
   private var layoutManager: RecyclerView.LayoutManager? = null
   private var adapter:RecyclerView.Adapter<RecyclerAdapter.ViewHolder>? = null

   override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_store_prices)

			val storeID = intent.getStringExtra("storeId")!!
			val storeName = intent.getStringExtra("username")
	    	val db = FirebaseFirestore.getInstance()

	    	layoutManager = LinearLayoutManager(this)
	        
	      val btnAddProduct = findViewById<Button>(R.id.btnAddProduct)
	        
	      val recyclerView = findViewById<RecyclerView>(R.id.rvPrices)
	      recyclerView.layoutManager  = layoutManager
	      recyclerView.adapter = RecyclerPrices( object : RecyclerPrices.CallbackInterface {
	           override fun passResultCallback(totalPrice: String, strOrderList:String, strComputedPrices:String) {
	           }
	      },storeID,btnAddProduct)

			val btnqueue = findViewById<ImageButton>(R.id.btnSeeOrders)
			val btnstat = findViewById<ImageButton>(R.id.btnStat)
			val btnlogout = findViewById<ImageButton>(R.id.btnStoreLogout)

			btnqueue.setOnClickListener {
				val intent = Intent(this, StoreQueueActivity::class.java).apply {
					putExtra("storeId", storeID)
					putExtra("username", storeName)
				}
				startActivity(intent)
				overridePendingTransition(R.anim.push_right_in, android.R.anim.slide_out_right);

			}

			btnstat.setOnClickListener {
				val intent = Intent(this, StoreStatActivity::class.java).apply {
					putExtra("storeId", storeID)
					putExtra("username", storeName)
				}
				startActivity(intent)
				overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);

			}

			btnlogout.setOnClickListener {
			val dialog = Dialog(this)
			dialog.setContentView(R.layout.alert_dialog_layout)
			dialog.window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
			dialog.setCancelable(false)

			val positiveButton = dialog.findViewById<Button>(R.id.btn_okay)
			val negativeButton = dialog.findViewById<Button>(R.id.btn_cancel)
			positiveButton.setOnClickListener{
				val intent = Intent(this, LoginActivity::class.java);
				startActivity(intent)
				finish()
				dialog.dismiss()
			}
			negativeButton.setOnClickListener{
				dialog.dismiss()
			}
			dialog.show()
		}
	}
        
	fun fillStr(value:String):String {
    	if (value != ""){
    		return value
    	}
    	return "0"
	 }
}
	

