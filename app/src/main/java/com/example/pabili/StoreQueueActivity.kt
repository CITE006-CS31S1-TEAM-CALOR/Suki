package com.example.pabili

import android.app.AlertDialog
import android.app.Dialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.content.Intent
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import com.google.firebase.firestore.FirebaseFirestore

class StoreQueueActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_store_queue)
        initialize()

    }
    override fun onRestart() {
        super.onRestart()
        initialize()
    }

    private fun initialize(){
        val storeName = intent.getStringExtra("username")
        val storeID = intent.getStringExtra("storeId")
        val storeNameDisplay = findViewById<TextView>(R.id.StoreNameText)
        val db = FirebaseFirestore.getInstance()

        val recyclerview = findViewById<RecyclerView>(R.id.storeCustomerQueue)
        recyclerview.layoutManager = LinearLayoutManager(this)
        val data = ArrayList<DataRecyclerQueue>()

        val btnstat = findViewById<ImageButton>(R.id.btnStat)
        val btnprice = findViewById<ImageButton>(R.id.btnSetPrice)
        val btnlogout = findViewById<ImageButton>(R.id.btnStoreLogout)

        btnstat.setOnClickListener {
            val intent = Intent(this, StoreStatActivity::class.java).apply {
                putExtra("storeId", storeID)
                putExtra("username", storeName)
            }
            startActivity(intent)
            overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out)

        }
        btnprice.setOnClickListener {
            val intent = Intent(this, StorePricesActivity::class.java).apply {
                putExtra("storeId", storeID)
                putExtra("username", storeName)
            }
            startActivity(intent)
            overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);

        }
        btnlogout.setOnClickListener {
            val dialog = Dialog(this)
            dialog.setContentView(R.layout.alert_dialog_layout)
            dialog.window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
            dialog.setCancelable(false)

            val positiveButton = dialog.findViewById<Button>(R.id.btn_okay)
            val negativeButton = dialog.findViewById<Button>(R.id.btn_cancel)
            positiveButton.setOnClickListener{
                val intent = Intent(this, LoginActivity::class.java);
                startActivity(intent)
                finish()
                dialog.dismiss()
            }
            negativeButton.setOnClickListener{
                dialog.dismiss()
            }

            dialog.show()

            /*
            val builder = AlertDialog.Builder(this)

            builder.setMessage("Confirm logging out?")
                .setCancelable(false)
                .setPositiveButton("Yes"){dialog, id ->
                    val intent = Intent(this, LoginActivity::class.java);
                    startActivity(intent)
                }.setNegativeButton("No"){dialog, id->
                    dialog.dismiss()
                }
            builder.create().show()
            */

        }


        storeNameDisplay.text = storeName

        db.collection("orders")
            .whereEqualTo("store",storeID).whereIn("status", listOf("pending","ready"))
            .orderBy("date").orderBy("time")
            .get()
            .addOnSuccessListener { result ->
                for(document in result) {
                    //Log.d("TAG", "${document.id} => ${document.data}")
                    //val stringUser = document.getString("username").toString()
                    //Log.d("TAG", stringUser)

                    val stringUser = document.getString("username").toString()
                    val stringDate = document.getString("date").toString() + " " + document.getString("time").toString()
                    val stringStore = document.id
                    val boolStatus = booleanStatus(document.getString("status").toString())

                    Log.d(
                        "TAG",
                        "Name: $stringUser | Date: $stringDate | ID: $stringStore"
                    )
                    data.add(DataRecyclerQueue(stringUser, stringDate, stringStore, boolStatus))
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
/*
        btnSetPrice.setOnClickListener {
							val intent = Intent(this, StorePricesActivity::class.java).apply {
								putExtra("storeId", storeID)
                                putExtra("username", storeName)
							}
							startActivity(intent)
            overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
        }

        btnStat.setOnClickListener {
                            val intent = Intent(this, StoreStatActivity::class.java).apply{
                                putExtra("storeId", storeID)
                                putExtra("username", storeName)
                            }
                            startActivity(intent)
            overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
        }

 */
    }
    private fun booleanStatus(string: String): Boolean{
        return when(string){
            "ready" -> true
            else -> false
        }
    }
}

