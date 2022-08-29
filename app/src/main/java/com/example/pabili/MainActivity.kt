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
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.widget.Toast
import com.google.firebase.firestore.FirebaseFirestore
import City
class MainActivity : AppCompatActivity() {
    private var layoutManager: RecyclerView.LayoutManager? = null
    private var adapter:RecyclerView.Adapter<RecyclerAdapter.ViewHolder>? = null
    private var customer_id: Int? = null;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        layoutManager = LinearLayoutManager(this)
        customer_id = 1
        
        val recyclerView = findViewById<RecyclerView>(R.id.recyclerView)
        recyclerView.layoutManager  = layoutManager
        adapter = RecyclerAdapter()
        recyclerView.adapter = adapter
	readDataFromFirestore()
	
    }
    
    
 
     private fun readDataFromFirestore() {
		val db = FirebaseFirestore.getInstance()
		val docRef = db.collection("Database").document("orders")
		
		val docData = hashMapOf(
        "stringExample" to "Hello world!",
        "booleanExample" to true,
        "numberExample" to 3.14159265,
        "listExample" to arrayListOf(1, 2, 3),
        "nullExample" to null
		)

	val nestedData = hashMapOf(
		"a" to 5,
		"b" to true
	)

	docData["objectExample"] = nestedData

	db.collection("Database").document("test")
		.set(docData)
		.addOnSuccessListener {  }
		.addOnFailureListener {  }
	
	val data5 = hashMapOf(
        "name" to "Beijing",
        "state" to null,
        "country" to "China",
        "capital" to true,
        "population" to 21500000,
        "regions" to listOf("jingjinji", "hebei")
	)
	db.collection("Database").document("BJ").set(data5)

val city = City("Los Angeles", "CA", "USA",
        false, 5000000L, listOf("west_coast", "socal"))
db.collection("Database").document("LA").set(city)

	val docRef1 = db.collection("Database").document("BJ")
	docRef1.get().addOnSuccessListener { documentSnapshot ->
	    val city = documentSnapshot.toObject<City>(City::class.java)
	    Toast.makeText(this@MainActivity,city?.regions.toString(),Toast.LENGTH_SHORT).show()
	}
		//Writing
		
		//Reading
		/*	
		docRef.get().addOnSuccessListener { document -> 
			if (document != null){
				Toast.makeText(this@MainActivity,"Document Snapshot data: ${document.data}",Toast.LENGTH_SHORT).show()
			} else {
				Toast.makeText(this@MainActivity,"No such document",Toast.LENGTH_SHORT).show()
			}
			/*.addOnFailureListener { exception ->
				Toast.makeText(this@MainActivity,"get failed with",Toast.LENGTH_SHORT).show()
			}*/
		}*/
   	 }

}   
