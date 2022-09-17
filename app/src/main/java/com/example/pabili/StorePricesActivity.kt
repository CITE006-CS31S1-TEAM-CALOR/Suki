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
import java.util.Date
import java.text.SimpleDateFormat
//import java.time.LocalDateTime
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
	  // val storeId = intent.getStringExtra("storeId")
	
        //tvStorename.text = storeName

      //  val tvStorename = findViewById<TextView>(R.id.tvStorename)

        //tvStorename.text = storeName

    	layoutManager = LinearLayoutManager(this)
        
        val btnAddProduct = findViewById<Button>(R.id.btnAddProduct)
        
        
        val recyclerView = findViewById<RecyclerView>(R.id.rvPrices)
        recyclerView.layoutManager  = layoutManager
        recyclerView.adapter = RecyclerPrices( object : RecyclerPrices.CallbackInterface {
           override fun passResultCallback(totalPrice: String, strOrderList:String, strComputedPrices:String) {
           }
           },storeID,btnAddProduct)
           
//
        
                /*
    		 val sdf = SimpleDateFormat("dd/M/yyyy hh:mm:ss")
			 val currentDate = sdf.format(Date())
			 
    		order.put("timestamp",currentDate)
    		
    				if (totalPrice.equals("0")){	
    					order = hashMapOf()
    				} else {
                order = hashMapOf (
				       "username" to fillStr(currentUser!!),
				       "store" to fillStr(storeId!!),
				       "transactionID" to fillStr(transactionId),
				       "orderList" to fillStr(strOrderList),
				       "computedPrices" to fillStr(strComputedPrices),
				       "status" to "pending",
				       "totalPrice" to fillStr(totalPrice),
                )
                }
                tvTotal.setText(totalPrice)
                */
     //       }
   //     },storeId) 
        
         /*       
    	var btnAddStore = findViewById<ImageView>(R.id.btnAddStore)
    	btnAddStore.setOnClickListener {
            val intent = Intent(this, ScannerActivity::class.java)
            startActivity(intent)	    	
    	}*/


		val btnqueue = findViewById<ImageButton>(R.id.btnSeeOrders)
		val btnstat = findViewById<ImageButton>(R.id.btnStat)
		val btnlogout = findViewById<ImageButton>(R.id.btnStoreLogout)

		btnqueue.setOnClickListener {
			val intent = Intent(this, StoreQueueActivity::class.java).apply {
				putExtra("storeId", storeID)
				putExtra("username", storeName)
			}
			startActivity(intent)
		}
		btnstat.setOnClickListener {
			val intent = Intent(this, StoreStatActivity::class.java).apply {
				putExtra("storeId", storeID)
				putExtra("username", storeName)
			}
			startActivity(intent)
		}
		btnlogout.setOnClickListener {
			val builder = AlertDialog.Builder(this)
			builder.setMessage("Confirm logging out?")
				.setCancelable(false)
				.setPositiveButton("Yes"){dialog, id ->
					val intent = Intent(this, LoginActivity::class.java)
					startActivity(intent)
				}.setNegativeButton("No"){dialog, id->
					dialog.dismiss()
				}
			builder.create().show()
		}
    		
}
        
        
		 fun fillStr(value:String):String {
                	if (value != ""){
                		return value
                	}
                	return "0"
                
                }

//    override fun passResultCallback(message: String) {
   //     Toast.makeText(this@MainActivity,("rrasd"),Toast.LENGTH_SHORT).show()
    //}
}
	

