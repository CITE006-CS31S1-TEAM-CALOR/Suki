package com.example.pabili

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.TextView
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
        val data = ArrayList<DataOrderList>()

        val customerNameTxt = findViewById<TextView>(R.id.orderCustomerName)
        val customerOrderTotal = findViewById<TextView>(R.id.orderTotal)
        val recyclerview = findViewById<RecyclerView>(R.id.storeCustomerOrder)
        recyclerview.layoutManager = LinearLayoutManager(this)

        db.collection("orders")
            .document(cDoc!!)
            .get()
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