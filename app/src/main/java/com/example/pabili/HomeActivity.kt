package com.example.pabili

import android.app.AlertDialog
import android.content.DialogInterface
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.firebase.firestore.FirebaseFirestore

import android.content.Intent


import android.annotation.SuppressLint
import android.app.Dialog
import android.graphics.drawable.AnimationDrawable
import android.util.Log
import android.view.MotionEvent
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.app.NotificationCompat.getAction
import androidx.core.view.accessibility.AccessibilityEventCompat.getAction
import androidx.core.widget.doOnTextChanged
import kotlin.system.exitProcess
import android.view.ViewGroup
import android.widget.*

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

        db.collection("users").whereEqualTo("username", currentUser).get()
            .addOnSuccessListener { result ->
                for (data in result) {
                    name.setText(data["name"].toString())
                    email.setText(data["email"].toString())
                    phone.setText(data["phone"].toString())
                    address.setText(data["address"].toString())
                }
            }

        val edit = findViewById<Button>(R.id.btnEditProfile)
        edit.setOnClickListener {
            name.isFocusableInTouchMode = true
            email.isFocusableInTouchMode = true
            phone.isFocusableInTouchMode = true
            address.isFocusableInTouchMode = true
            save.isEnabled = true
            save.setOnClickListener {
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
                    .get().addOnSuccessListener { result ->
                        for (data in result) {
                            db.collection("users").document(data.id).set(userDetails)
                                .addOnSuccessListener {
                                    Toast.makeText(
                                        this,
                                        ("Edit profile Success"),
                                        Toast.LENGTH_SHORT
                                    ).show()
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

        val btnSelectStore = findViewById<ImageButton>(R.id.btnSelectStore)
        btnSelectStore.setOnClickListener() {
            val intent = Intent(this, StoreSelectionMap::class.java).apply{
                putExtra("currentUser", "$currentUser");
                putExtra("currentUserPw", "$currentUserPw");
            }
            startActivity(intent)
        }

        //btnSelectStore.setOnClickListener(){
//
        //    val mAlertDialogBuilder = AlertDialog.Builder(this)
        //    val storeList = ArrayList<String>()
        //    val storeIDList = ArrayList<String>()
//
        //    val scaleUp: Animation = AnimationUtils.loadAnimation(this,R.anim.scale_up)
        //    val scaleDown: Animation = AnimationUtils.loadAnimation(this,R.anim.scale_down)
//
        //    btnSelectStore.setOnTouchListener(object: View.OnTouchListener {
        //        override fun onTouch(v: View?, event: MotionEvent?): Boolean{
        //            when (v) { v ->
        //                when (event?.action) { MotionEvent.ACTION_UP -> {
        //                    btnSelectStore.startAnimation(scaleDown)
        //                } MotionEvent.ACTION_DOWN -> {
        //                    btnSelectStore.startAnimation(scaleUp)
        //                }
        //                }
        //            }
        //            return v?.onTouchEvent(event) ?: true
        //        }
        //    })
//
        //    db.collection("stores")
        //        .get()
        //        .addOnCompleteListener { task ->
        //            if(task.isSuccessful){
        //                for (document in task.result) {
        //                    var storeName = document.data["username"].toString()
        //                    storeList.add(storeName)
        //                    storeIDList.add(document.data["id"].toString())
        //                }
        //            }
        //            val storeArray = storeList.toTypedArray()
        //            mAlertDialogBuilder.setTitle("STORE")
        //            mAlertDialogBuilder.setCancelable(true)
        //            mAlertDialogBuilder.setItems(storeArray, DialogInterface.OnClickListener{ dialog, index ->
        //                val intent = Intent(this, MainActivity::class.java).apply {
        //                    val selStoreName=storeList.get(index).toString()
        //                    val selStoreId=storeIDList.get(index).toString()
        //                    putExtra("storeId", "$selStoreId");
        //                    putExtra("storeName", "$selStoreName");
        //                    putExtra("currentUser", "$currentUser");
        //                    putExtra("currentUserPw", "$currentUserPw");
        //
        //                }
        //                startActivity(intent)
        //                finish()
        //            })
        //            mAlertDialogBuilder.setNegativeButton("Cancel") { dialog, which ->
        //                Toast.makeText(applicationContext,
        //                    "Canceled", Toast.LENGTH_SHORT).show()
        //            }
        //            mAlertDialogBuilder.show()
        //        }
        //    }
        //}

        //override fun onBackPressed() {
        //    val dialog = Dialog(this)
        //    dialog.setContentView(R.layout.alert_dialog_layout)
        //    dialog.window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        //    dialog.setCancelable(false)
//
        //    val positiveButton = dialog.findViewById<Button>(R.id.btn_okay)
        //    val negativeButton = dialog.findViewById<Button>(R.id.btn_cancel)
        //    val subtitle = dialog.findViewById<TextView>(R.id.alertSubtitle)
        //    subtitle.text = "Do you want to logout?"
        //    positiveButton.text = "Yes"
        //    positiveButton.setOnClickListener{
        //        val intent =  Intent(this, LoginActivity::class.java)
        //        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK
        //        startActivity(intent)
        //        finish()
        //        dialog.dismiss()
        //    }
        //    negativeButton.setOnClickListener{
        //        dialog.dismiss()
        //    }
        //    dialog.show()
        //}
    }
}



