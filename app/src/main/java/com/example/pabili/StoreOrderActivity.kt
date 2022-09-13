package com.example.pabili

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore

class StoreOrderActivity : AppCompatActivity() {

    private lateinit var cInfo: DataCustomerInfo

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_store_order)

        /*
        val computedPricesView: TextView = findViewById(R.id.computedPricesTXT)
        val orderListView: TextView = findViewById(R.id.orderListTXT)
        val statusView: TextView = findViewById(R.id.statusTXT)
        val storeView: TextView = findViewById(R.id.storeTXT)
        val timestampView: TextView = findViewById(R.id.timestampTXT)
        val totalPriceView: TextView = findViewById(R.id.totalPriceTXT)
        val transactionIDView: TextView = findViewById(R.id.transactionIDTXT)
        val usernameView: TextView = findViewById(R.id.usernameTXT)
         */

        val cDoc = intent.getStringExtra("cDoc")
        val db = FirebaseFirestore.getInstance()
        val orders = db.collection("orders").document(cDoc!!)
        val data = ArrayList<DataOrderList>()

        val customerNameTxt = findViewById<TextView>(R.id.orderCustomerName)
        val customerOrderTotal = findViewById<TextView>(R.id.orderTotal)
        val recyclerview = findViewById<RecyclerView>(R.id.storeCustomerOrder)
        val delBtn = findViewById<Button>(R.id.deleteBtn)
        val scnBtn = findViewById<Button>(R.id.scannerBTN)
        recyclerview.layoutManager = LinearLayoutManager(this)

        delBtn.setOnClickListener{
            orders.update("status","canceled")
                .addOnSuccessListener {
                    Toast.makeText(this@StoreOrderActivity,("Order was canceled"), Toast.LENGTH_SHORT).show()
                    orders.get().addOnSuccessListener { result ->
                        val intent = Intent(this, StoreQueueActivity::class.java).apply {
                            putExtra("ID", result.getString("store"))
                        }
                        startActivity(intent)
                    }


                }.addOnFailureListener{
                    Toast.makeText(this@StoreOrderActivity, ("There was an issue in the server. Please try again"), Toast.LENGTH_LONG).show()
                }
        }



        orders.get()
            .addOnSuccessListener { result ->
                cInfo = DataCustomerInfo(
                    result.getString("computedPrices").toString(),
                    result.getString("orderList").toString(),
                    result.getString("status").toString(),
                    result.getString("store").toString(),
                    result.getString("timestamp").toString(),
                    result.getString("totalPrice").toString(),
                    result.getString("transactionID").toString(),
                    result.getString("username").toString()
                )

                customerNameTxt.text = "Name: " + cInfo.username
                customerOrderTotal.text = "Total: " + cInfo.totalPrice



                val computedPricesArray = cInfo.computedPrices!!.split(", ")
                val orderListArray = cInfo.orderList!!.split(", ")
                for ((refComp, order) in orderListArray!!.withIndex()){
                    if(refComp != orderListArray.size-1){
                        val name = order.replace("\\d+".toRegex(),"")
                        val qty = order.replace(" [a-z]+".toRegex(),"")
                        val com = computedPricesArray[refComp]

                        data.add(DataOrderList(name,"x"+qty,"P"+com))
                    }

                }

                val adapter = RecyclerOrder(data)
                recyclerview.adapter = adapter
/*
                data.add(cInfo)
                val adapter = RecyclerOrder(data)
                recyclerview.adapter = adapter

                Log.d("TAG",cInfo.computedPrices + " | "
                        + cInfo.orderList + " | "
                        + cInfo.status + " | "
                        + cInfo.store + " | "
                        + cInfo.timestamp + " | "
                        + cInfo.totalPrice + " | "
                        + cInfo.transactionID + " | "
                        + cInfo.username + " | ")

                computedPricesView.setText(cInfo.computedPrices)
                orderListView.setText(cInfo.orderList)
                statusView.setText(cInfo.status)
                storeView.setText(cInfo.store)
                timestampView.setText(cInfo.timestamp)
                totalPriceView.setText(cInfo.totalPrice)
                transactionIDView.setText(cInfo.transactionID)
                usernameView.setText(cInfo.username)
                 */
            }



    }
}