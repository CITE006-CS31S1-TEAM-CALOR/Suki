package com.example.pabili

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.toObject

class StoreOrderActivity : AppCompatActivity() {

    private lateinit var cInfo: DataCustomerInfo

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_store_order)

        val computedPricesView: TextView = findViewById(R.id.computedPricesTXT)
        val orderListView: TextView = findViewById(R.id.orderListTXT)
        val statusView: TextView = findViewById(R.id.statusTXT)
        val storeView: TextView = findViewById(R.id.storeTXT)
        val timestampView: TextView = findViewById(R.id.timestampTXT)
        val totalPriceView: TextView = findViewById(R.id.totalPriceTXT)
        val transactionIDView: TextView = findViewById(R.id.transactionIDTXT)
        val usernameView: TextView = findViewById(R.id.usernameTXT)
        val cDoc = intent.getStringExtra("cDoc")
        val db = FirebaseFirestore.getInstance()

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
            }



    }
}