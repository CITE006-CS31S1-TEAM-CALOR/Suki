	package com.example.pabili

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Parcel
import android.os.Parcelable
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Button
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.widget.Toast
import com.google.firebase.firestore.FirebaseFirestore
import android.content.Intent
import TagPrice
import java.util.Date
import java.text.SimpleDateFormat
//import java.time.LocalDateTime
import java.lang.System
import android.view.LayoutInflater
import android.view.ViewGroup
import android.text.Editable
import android.text.TextWatcher
import java.util.Random

class StorePricesActivity : AppCompatActivity() {
    private var layoutManager: RecyclerView.LayoutManager? = null
    private var adapter:RecyclerView.Adapter<RecyclerAdapter.ViewHolder>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_store_prices)



    	val db = FirebaseFirestore.getInstance()
    	val storeId = intent.getStringExtra("storeId")!!
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
           },storeId,btnAddProduct)
           
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


       var btnSetPrice = findViewById<Button>(R.id.btnSetPrice)
    	 btnSetPrice.setOnClickListener {
    		
    		}
    		
    		    var btnSeeOrders = findViewById<Button>(R.id.btnSeeOrders)
    	       btnSeeOrders.setOnClickListener {
            	val intent = Intent(this, StoreQueueActivity::class.java).apply {
						putExtra("ID", storeId)
						putExtra("username", intent.getStringExtra("username"))
					 }
					 startActivity(intent)	
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
	

