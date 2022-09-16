package com.example.pabili

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.widget.Button
import android.content.Intent
import android.util.Log
import android.widget.ImageButton
import android.widget.TextView
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query

class StoreQueueActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_store_queue)


        val storeName = intent.getStringExtra("username")
        val storeID = intent.getStringExtra("ID")
        val storeNameDisplay = findViewById<TextView>(R.id.StoreNameText)
        val db = FirebaseFirestore.getInstance()

        val recyclerview = findViewById<RecyclerView>(R.id.storeCustomerQueue)
        recyclerview.layoutManager = LinearLayoutManager(this)
        val data = ArrayList<DataRecyclerQueue>()
        val btnSetPrice = findViewById<ImageButton>(R.id.btnSetPrice)
        val btnStat = findViewById<ImageButton>(R.id.btnStat)


        storeNameDisplay.text = storeName

        db.collection("orders")
            .whereEqualTo("store",storeID).whereIn("status", listOf("pending","ready"))
            .orderBy("timestamp")
            .get()
            .addOnSuccessListener { result ->
                for(document in result) {
                    //Log.d("TAG", "${document.id} => ${document.data}")
                    //val stringUser = document.getString("username").toString()
                    //Log.d("TAG", stringUser)

                    val stringUser = document.getString("username").toString()
                    val stringDate = document.getString("timestamp").toString()
                    val stringStore = document.id

                    Log.d(
                        "TAG",
                        "Name: $stringUser | Date: $stringDate | ID: $stringStore"
                    )
                    data.add(DataRecyclerQueue(stringUser, stringDate, stringStore))
                }
                val adapter = RecyclerQueue(this,data)
                recyclerview.adapter = adapter
            }.addOnFailureListener{ Exception ->
                Log.d("TAG", "Fail to get from database \n$Exception")
            }

        //testing
        /*for (i in 1..20){
            data.add(DataRecyclerQueue("Name " + i, "Date " + (20 - i)))
        }

        val adapter = RecyclerQueue(data)
        recyclerview.adapter = adapter
         */

        btnSetPrice.setOnClickListener {
							val intent = Intent(this, StorePricesActivity::class.java).apply {
								putExtra("storeId", storeID)
                                putExtra("username", storeName)
							}
							startActivity(intent)
        }

        btnStat.setOnClickListener {
            val intent = Intent(this, StoreStatActivity::class.java).apply{
                putExtra("storeId", storeID)
                putExtra("username", storeName)
            }
            startActivity(intent)
        }
    }
}

