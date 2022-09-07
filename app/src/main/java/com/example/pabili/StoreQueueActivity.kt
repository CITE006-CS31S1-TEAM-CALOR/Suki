package com.example.pabili

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.widget.Button
import android.content.Intent
class StoreQueueActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_store_queue)

        val recyclerview = findViewById<RecyclerView>(R.id.storeCustomerQueue)
        recyclerview.layoutManager = LinearLayoutManager(this)
        val data = ArrayList<DataRecyclerQueue>()

        //testing
        for (i in 1..20){
            data.add(DataRecyclerQueue("Name " + i, "Date " + (20 - i)))
        }

        val adapter = RecyclerQueue(data)
        recyclerview.adapter = adapter
        
        val btnSetPrice = findViewById<Button>(R.id.btnSetPrice)
        btnSetPrice.setOnClickListener {
							val intent = Intent(this, StorePricesActivity::class.java).apply {
								putExtra("storeId", "1")
							}
							startActivity(intent)
        }
    }
}
