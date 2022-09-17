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
import android.widget.EditText


class HomeActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        val currentUser = intent.getStringExtra("username")
        val currentUserPw = intent.getStringExtra("password")
        val db = FirebaseFirestore.getInstance()

        val name = findViewById<EditText>(R.id.etName)
        val email = findViewById<EditText>(R.id.etEmail)
        val phone = findViewById<EditText>(R.id.etPhone)
        val address = findViewById<EditText>(R.id.etAddress)
        val save = findViewById<Button>(R.id.btnSaveEdit)
        name.isFocusable = false
        email.isFocusable = false
        phone.isFocusable = false
        address.isFocusable = false
        save.isEnabled = false

        db.collection("users").whereEqualTo("username", currentUser).get().addOnSuccessListener {
            result ->
                for(data in result){
                    name.setText(data["name"].toString())
                    email.setText(data["email"].toString())
                    phone.setText(data["phone"].toString())
                    address.setText(data["address"].toString())
                }
            }

        val edit = findViewById<Button>(R.id.btnEditProfile)
        edit.setOnClickListener{
            name.isFocusableInTouchMode = true
            email.isFocusableInTouchMode = true
            phone.isFocusableInTouchMode = true
            address.isFocusableInTouchMode = true
            save.isEnabled = true
            save.setOnClickListener{
                var cName = name.text.toString()
                var cEmail = email.text.toString()
                var cPhone = phone.text.toString()
                var cAddress = address.text.toString()
                val userDetails = hashMapOf(
                "username" to currentUser,
                "password" to currentUserPw,
                "name" to cName,
                "email" to cEmail,
                "phone" to cPhone,
                "address" to cAddress
            )
                db.collection("users").whereEqualTo("username", currentUser)
                    .get().addOnSuccessListener {
                            result ->
                            for(data in result){
                                   db.collection("users").document(data.id).set(userDetails).addOnSuccessListener {
                                        Toast.makeText(this,("Edit profile Success"), Toast.LENGTH_SHORT).show()
                                    }
                            }

                    }
                name.clearFocus()
                email.clearFocus()
                phone.clearFocus()
                address.clearFocus()
                name.isFocusableInTouchMode = false
                email.isFocusableInTouchMode = false
                phone.isFocusableInTouchMode = false
                address.isFocusableInTouchMode = false
                save.isEnabled = false
            }
        }
        val btnSelectStore = findViewById<Button>(R.id.btnSelectStore)
        btnSelectStore.setOnClickListener(){

            val mAlertDialogBuilder = AlertDialog.Builder(this)
            val storeList = ArrayList<String>()
            val storeIDList = ArrayList<String>()

            db.collection("stores")
                .get()
                .addOnCompleteListener { task ->
                    if(task.isSuccessful){
                        for (document in task.result) {
                            var storeName = document.data["username"].toString()
                            storeList.add(storeName)
                            storeIDList.add(document.data["id"].toString())
                        }
                    }
                    val storeArray = storeList.toTypedArray()
                    mAlertDialogBuilder.setTitle("STORE")
                    mAlertDialogBuilder.setCancelable(true)
                    mAlertDialogBuilder.setItems(storeArray, DialogInterface.OnClickListener{ dialog, index ->
                        val intent = Intent(this, MainActivity::class.java).apply {
                            val selectedStore=storeIDList.get(index).toString()
                            putExtra("storeId,currentUser", "$selectedStore,$currentUser");
                            
                        }
                        startActivity(intent)
                    })
                    mAlertDialogBuilder.setNegativeButton("Cancel") { dialog, which ->
                        Toast.makeText(applicationContext,
                            "Canceled", Toast.LENGTH_SHORT).show()
                    }
                    mAlertDialogBuilder.show()
                }
            }
        }
}



