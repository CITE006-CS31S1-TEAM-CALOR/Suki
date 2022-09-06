package com.example.pabili

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

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
    }
}