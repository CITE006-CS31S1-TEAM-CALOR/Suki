package com.example.pabili

import android.app.AlertDialog
import android.app.Dialog
import android.content.DialogInterface
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.firestore.FirebaseFirestore

import android.content.Intent

class HomeActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        val currentUser = intent.getStringExtra(EXTRA_MESSAGE)

		//remove this button below after implementation of startactivity in alert dialog
		val tempbutton = findViewById<Button>(R.id.btnTempStore)
		tempbutton.setOnClickListener {
		
        val currentUser = intent.getStringExtra(EXTRA_MESSAGE)
		   	 val intent = Intent(this, MainActivity::class.java).apply {
		     						 putExtra(EXTRA_MESSAGE, currentUser)
		     						 putExtra("storeId", "1")
		     }
		     startActivity(intent)
		} 
		
		

        val db = FirebaseFirestore.getInstance()
        findViewById<TextView>(R.id.txtName).apply{
            text = currentUser
        }

        val btnSelectStore = findViewById<Button>(R.id.btnSelectStore)
        btnSelectStore.setOnClickListener(){

            val mAlertDialogBuilder = AlertDialog.Builder(this)

            val storeList = ArrayList<String>()
            db.collection("stores")
                .get()
                .addOnCompleteListener { task ->
                    if(task.isSuccessful){
                        for (document in task.result) {
                            var storeName = document.data["username"].toString()
                            storeList.add(storeName)
                        }
                    }
                    var storeArray = storeList.toTypedArray()
                    mAlertDialogBuilder.setTitle("STORE")
                    mAlertDialogBuilder.setCancelable(true)
                    mAlertDialogBuilder.setItems(storeArray, DialogInterface.OnClickListener{ dialog, which ->  })
                    mAlertDialogBuilder.setNegativeButton("Cancel") { dialog, which ->
                        Toast.makeText(applicationContext,
                            "Canceled", Toast.LENGTH_SHORT).show()
                    }
                    mAlertDialogBuilder.show()
                }
            }
        }
}



