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
import java.text.SimpleDateFormat
import java.lang.System
import android.view.LayoutInflater
import android.view.ViewGroup
import android.text.Editable
import android.text.TextWatcher
import java.util.Random
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter

import android.annotation.SuppressLint
import android.app.Dialog
import android.graphics.drawable.AnimationDrawable
import android.util.Log
import android.view.MotionEvent
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.app.NotificationCompat.getAction
import androidx.core.view.accessibility.AccessibilityEventCompat.getAction
import androidx.core.widget.doOnTextChanged
import kotlin.system.exitProcess



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

		findViewById<TextView>(R.id.txtStoreName).apply{
			text = storeName
		}
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

    override fun onBackPressed() {
    	var isHavingOrder: String? = intent.getStringExtra("isHavingOrder")?:"false"

    	if (isHavingOrder=="true"){
    		val dialog = Dialog(this)
	        dialog.setContentView(R.layout.alert_dialog_layout)
	        dialog.window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
	        dialog.setCancelable(false)

	        val positiveButton = dialog.findViewById<Button>(R.id.btn_okay)
	        val negativeButton = dialog.findViewById<Button>(R.id.btn_cancel)
	        val subtitle = dialog.findViewById<TextView>(R.id.alertSubtitle)
	        subtitle.text = "Do you want to logout?"
	        positiveButton.text = "Yes"
	        positiveButton.setOnClickListener{
	            val intent =  Intent(this, LoginActivity::class.java)
	            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK
				startActivity(intent)	
                finish()	
	            dialog.dismiss()
	        }
	        negativeButton.setOnClickListener{
	            dialog.dismiss()
	        }
	        dialog.show()
    	}


    	if (isHavingOrder!="true"){
	    	val intent =  Intent(this, HomeActivity::class.java).apply {
	    		putExtra("currentUser", intent.getStringExtra("currentUser"));
	            putExtra("currentUserPw", intent.getStringExtra("currentUserPw"));
	    	}
	        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK
			startActivity(intent)   
			finish()
    	}
    }
}
	
