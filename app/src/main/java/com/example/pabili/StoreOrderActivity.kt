package com.example.pabili

import android.app.AlertDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.Switch
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore
import com.google.zxing.client.android.Intents

class StoreOrderActivity : AppCompatActivity() {

    private lateinit var cInfo: DataCustomerInfo

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_store_order)

        val data = ArrayList<DataOrderList>()
        val cDoc = intent.getStringExtra("cDoc")
        val db = FirebaseFirestore.getInstance()
        val orders = db.collection("orders").document(cDoc!!)

        val customerNameTxt = findViewById<TextView>(R.id.orderCustomerName)
        val customerOrderTotal = findViewById<TextView>(R.id.orderTotal)
        val recyclerview = findViewById<RecyclerView>(R.id.storeCustomerOrder)
        val delBtn = findViewById<Button>(R.id.deleteBtn)
        val scnBtn = findViewById<Button>(R.id.scannerBTN)
        val swt = findViewById<Switch>(R.id.switchToClaim)
        recyclerview.layoutManager = LinearLayoutManager(this)

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

                swt.isChecked = checkStatus(cInfo.status.toString())
                customerNameTxt.text = "Name: " + cInfo.username
                customerOrderTotal.text = "Total: P" + cInfo.totalPrice



                val computedPricesArray = cInfo.computedPrices!!.split(", ")
                val orderListArray = cInfo.orderList!!.split(", ")
                for ((refComp, order) in orderListArray!!.withIndex()){
                    if(refComp != orderListArray.size-1){
                        val name = order.replace("\\d+".toRegex(),"")
                        val qty = order.replace(" [a-z]+".toRegex(),"")
                        val com = computedPricesArray[refComp]

                        data.add(DataOrderList(name, "x$qty", "P$com"))
                    }

                }

                val adapter = RecyclerOrder(this, cDoc, data, customerOrderTotal)
                recyclerview.adapter = adapter

            }

        delBtn.setOnClickListener{
            if(!swt.isChecked){
                val builder = AlertDialog.Builder(this)
                builder.setMessage("Are you sure you want to delete this customer?")
                    .setCancelable(false)
                    .setPositiveButton("Yes"){dialog,id->
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
                    .setNegativeButton("No"){dialog, id ->
                        dialog.dismiss()
                    }
                builder.create().show()
            }else{
                Toast.makeText(this@StoreOrderActivity,"Order is ready to be Claimed. Can't cancel order.", Toast.LENGTH_SHORT).show()
            }
        }



        swt.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                orders.update("status", "ready")
            } else {
                orders.update("status", "pending")
            }
        }

        scnBtn.setOnClickListener {
            if(swt.isChecked){
                val intent = Intent(this, ScannerActivity::class.java).apply {
                    putExtra("cDoc",cDoc)
                }
                startActivity(intent)
            }else{
                Toast.makeText(this@StoreOrderActivity,"Order not yet ready", Toast.LENGTH_SHORT).show()
            }
        }

        /*
        val computedPricesView: TextView = findViewById(R.id.computedPricesTXT)
        val orderListView: TextView = findViewById(R.id.orderListTXT)
        val statusView: TextView = findViewById(R.id.statusTXT)
        val storeView: TextView = findViewById(R.id.storeTXT)
        val timestampView: TextView = findViewById(R.id.timestampTXT)
        val totalPriceView: TextView = findViewById(R.id.totalPriceTXT)
        val transactionIDView: TextView = findViewById(R.id.transactionIDTXT)
        val usernameView: TextView = findViewById(R.id.usernameTXT)

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
    private fun checkStatus(comp:String): Boolean{
        return (comp == "ready")
    }
}