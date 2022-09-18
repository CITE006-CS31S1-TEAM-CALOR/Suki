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
//import java.util.Date
import java.text.SimpleDateFormat
import java.lang.System
import android.view.LayoutInflater
import android.view.ViewGroup
import android.text.Editable
import android.text.TextWatcher
import java.util.Random
//import java.sql.Date
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter

class MainActivity : AppCompatActivity() {
    private var layoutManager: RecyclerView.LayoutManager? = null
    private var adapter:RecyclerView.Adapter<RecyclerAdapter.ViewHolder>? = null
    private var customer_id: Int? = null
	private var order = HashMap <String, String>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

    	val db = FirebaseFirestore.getInstance()

		var currentUser: String = ""
		var storeId: String = ""
		var storeName: String = ""

    	var isHavingOrder: String? = intent.getStringExtra("isHavingOrder")?:"false"

    	currentUser = intent.getStringExtra("currentUser")!!
	    storeId = intent.getStringExtra("storeId")!!
		storeName = intent.getStringExtra("storeName")!!

        var transactionId:String = ""
        if (isHavingOrder=="false"){
        	transactionId = generateTransactionId()
        } else {
        	transactionId = intent.getStringExtra("transactionId")!!
        }

        var strExistingOrderList:String = intent.getStringExtra("strExistingOrderList")?:""
		var strExistingComputedPrices:String = intent.getStringExtra("strExistingComputedPrices")?:""

		Toast.makeText(this@MainActivity, strExistingOrderList, Toast.LENGTH_SHORT).show()
		Toast.makeText(this@MainActivity, strExistingComputedPrices, Toast.LENGTH_SHORT).show()
		
		val choice1 = findViewById<Button>(R.id.choice1)
		val choice2 = findViewById<Button>(R.id.choice2)
		val choice3 = findViewById<Button>(R.id.choice3)

		//Toast.makeText(this@MainActivity, currentUser, Toast.LENGTH_SHORT).show()
	    //Toast.makeText(this@MainActivity,storeId,Toast.LENGTH_SHORT).show()

		findViewById<TextView>(R.id.txtStoreName).apply{
			text = storeName
		}
        //tvStorename.text = storeName

      //  val tvStorename = findViewById<TextView>(R.id.tvStorename)
        val tvTotal = findViewById<TextView>(R.id.tvTotal)

        //tvStorename.text = storeName

    	layoutManager = LinearLayoutManager(this)
        
        var orderList = ArrayList<String>()
        if (strExistingOrderList!=""){
        	val lsExistingOrders = strExistingOrderList.split(", ")
        	for(data in lsExistingOrders){
        		orderList.add(data.trim())
        	}
        }

        var computedPrices = ArrayList<Int>()
		if (strExistingComputedPrices!=""){
        	val lsExistingPrices = strExistingComputedPrices.split(", ")
        	for(data in lsExistingPrices){
        		computedPrices.add(data.trim().toInt())
        	}
        }
        

        val recyclerView = findViewById<RecyclerView>(R.id.recyclerView)
        recyclerView.layoutManager  = layoutManager
        recyclerView.adapter = RecyclerAdapter( object : RecyclerAdapter.CallbackInterface {
            override fun passResultCallback(totalPrice: String, strOrderList:String, strComputedPrices:String) {
    				if (totalPrice.equals("0")){
    					order = hashMapOf()
    				} else {
               			order = hashMapOf (
							"username" to fillStr(currentUser),
							"store" to fillStr(storeId),
							"transactionID" to fillStr(transactionId),
							"orderList" to fillStr(strOrderList),
							"computedPrices" to fillStr(strComputedPrices),
							"status" to "pending",
							"totalPrice" to fillStr(totalPrice)
		                )
                }
                tvTotal.setText(totalPrice.toString())
            }
        },storeId,choice1,choice2,choice3,orderList,computedPrices) 
         /*       
    	var btnAddStore = findViewById<ImageView>(R.id.btnAddStore)
    	btnAddStore.setOnClickListener {
            val intent = Intent(this, ScannerActivity::class.java)
            startActivity(intent)	    	
    	}*/


       var btnSubmitOrder = findViewById<Button>(R.id.btnSubmitOrder)
    	btnSubmitOrder.setOnClickListener {
    		if (order.isEmpty()) {
				Toast.makeText(this@MainActivity, "Order is Empty", Toast.LENGTH_SHORT).show()
    		} else {
				val date = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
				val time = LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss"))
				println(date + " " + time)
				order.put("date",date)
				order.put("time",time)

				val docref = db.collection("orders")
				docref.whereEqualTo("transactionID",fillStr(transactionId)).get()
				.addOnSuccessListener {
					result ->
					if (result.isEmpty()){
						docref.add(order)
						.addOnSuccessListener { 
							documentReference ->
							Toast.makeText(this@MainActivity,("Order Received"),Toast.LENGTH_SHORT).show()
								val intent = Intent(this, ClaimingActivity::class.java).apply {
								putExtra("transactionId", transactionId)

							}
							startActivity(intent)

							}
					} else {
						for (data in result){
							docref.document(data.id).delete().addOnSuccessListener {
								docref.add(order).addOnSuccessListener {
									Toast.makeText(this@MainActivity,("Order Received"),Toast.LENGTH_SHORT).show()
									val intent = Intent(this, ClaimingActivity::class.java).apply {
										putExtra("transactionId", transactionId)
									}
									startActivity(intent)
								}
							}
						}
					}
				}
			}
		}
    		/*
	    	db.collection("orders")
					.add(order)
					.addOnSuccessListener { 
						documentReference ->
						Toast.makeText(this@MainActivity,"Order Received",Toast.LENGTH_SHORT).show()
						     val intent = Intent(this, ClaimingActivity::class.java).apply {
				      		 putExtra("transactionId", transactionId)
					      }
					      startActivity(intent)
						}
					.addOnFailureListener{
						Toast.makeText(this@MainActivity, "There was an error in the server", Toast.LENGTH_SHORT).show()
					}    	
    			}
    		
    		}
    		
    		*/

        }
        
		fun generateTransactionId():String {
				 var charPool = "abcdefghijklmnopqrstuvwxyzABCDEFGHIKLMNOPQRSTUVWXYZ1234567890123456789001234567890"
			 	 var transactionId = ""
				 for (i in 0..30){
					transactionId += charPool[Random().nextInt(82)]
				 }
			        
			  return transactionId
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
	
