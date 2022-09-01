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
import City

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

	val currentUser = intent.getStringExtra(EXTRA_MESSAGE)
	
	Toast.makeText(this@MainActivity,"Hello $currentUser",Toast.LENGTH_SHORT).show()
        layoutManager = LinearLayoutManager(this)
        
        val recyclerView = findViewById<RecyclerView>(R.id.recyclerView)
        recyclerView.layoutManager  = layoutManager
        adapter = RecyclerAdapter()
        recyclerView.adapter = adapter
        
        
    	var btnScanner = findViewById<Button>(R.id.btnScanner)
    	btnScanner.setOnClickListener {

		val intent = Intent(this, ScannerActivity::class.java).apply {
	        }
		 startActivity(intent)	    	
    	
    	
    	}
    }
}

