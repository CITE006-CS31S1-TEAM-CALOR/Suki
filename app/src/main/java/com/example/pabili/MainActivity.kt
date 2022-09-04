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
import java.time.LocalDateTime
import android.view.LayoutInflater
import android.view.ViewGroup
import android.text.Editable
import android.text.TextWatcher

class MainActivity : AppCompatActivity() {
    private var layoutManager: RecyclerView.LayoutManager? = null
    private var adapter:RecyclerView.Adapter<RecyclerAdapter.ViewHolder>? = null
    private var customer_id: Int? = null;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

    	val db = FirebaseFirestore.getInstance()
    	val currentUser = intent.getStringExtra(EXTRA_MESSAGE)
	//val storeName = intent.getStringExtra("storename")
	
      //  val tvStorename = findViewById<TextView>(R.id.tvStorename)
        val tvTotal = findViewById<TextView>(R.id.tvTotal)

        //tvStorename.text = storeName

    	layoutManager = LinearLayoutManager(this)
        
        var order: HashMap <String, String> = hashMapOf("null" to "null")
        
        val recyclerView = findViewById<RecyclerView>(R.id.recyclerView)
        recyclerView.layoutManager  = layoutManager
        recyclerView.adapter = RecyclerAdapter( object : RecyclerAdapter.CallbackInterface {
            override fun passResultCallback(totalPrice: String, strOrderList:String, strComputedPrices:String) {
                order = hashMapOf(
						"store" to "TestStore",
						"transactionID" to "testID",
						"timestamp" to LocalDateTime.now().toString(),
						"totalPrice" to totalPrice,
						"status" to "pending",
						"orderList" to strOrderList,
						"computedPrices" to strComputedPrices
						
					)
					
                tvTotal.text = totalPrice
            }
        } ) 
         /*       
    	var btnAddStore = findViewById<ImageView>(R.id.btnAddStore)
    	btnAddStore.setOnClickListener {
            val intent = Intent(this, ScannerActivity::class.java)
            startActivity(intent)	    	
    	}*/


       var btnSubmitOrder = findViewById<Button>(R.id.btnSubmitOrder)
    	btnSubmitOrder.setOnClickListener {
    		db.collection("orders")
					.add(order)
					.addOnSuccessListener { 
						documentReference ->
						Toast.makeText(this@MainActivity,"Order Received",Toast.LENGTH_SHORT).show()
				      val intent = Intent(this, ClaimingActivity::class.java)
				      startActivity(intent)	
					}
					.addOnFailureListener{
						Toast.makeText(this@MainActivity, "There was an error in the server", Toast.LENGTH_SHORT).show()
					}    	
    	}

        }

//    override fun passResultCallback(message: String) {
  //       Toast.makeText(this@MainActivity,("rrasd"),Toast.LENGTH_SHORT).show()
    //}
}
	
