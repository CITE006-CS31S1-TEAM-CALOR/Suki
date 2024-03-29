package com.example.pabili

import android.annotation.SuppressLint
import android.app.Dialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.firebase.firestore.FirebaseFirestore
import android.widget.TextView
import android.widget.Toast
import android.widget.Button
import android.widget.EditText
import android.content.Intent
import android.graphics.drawable.AnimationDrawable
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.ImageButton
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.app.NotificationCompat.getAction
import androidx.core.view.accessibility.AccessibilityEventCompat.getAction
import androidx.core.widget.doOnTextChanged
import kotlin.system.exitProcess

var LOGIN_NAME = ""
var LOGIN_ID = ""
public class LoginActivity : AppCompatActivity() {

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(R.style.Theme_Pabili)
        setContentView(R.layout.activity_login)

        val etUsername: EditText = findViewById(R.id.etUsername)
        val etPassword: EditText = findViewById(R.id.etPassword)
        val btnCustomerLogin: ImageButton = findViewById(R.id.btnCustomerLogin)
        val btnStoreLogin: ImageButton = findViewById(R.id.btnStoreLogin)
        val btnSignup: Button = findViewById(R.id.btnSignup)

        val db = FirebaseFirestore.getInstance()

        val scaleUp: Animation = AnimationUtils.loadAnimation(this,R.anim.scale_up)
        val scaleDown: Animation = AnimationUtils.loadAnimation(this,R.anim.scale_down)

        //Pressing enter in username textfield transfers automatically to password textfield
        etUsername.setOnKeyListener { v, keyCode, event ->
            Log.d("Keytouch", "$keyCode")
            if(keyCode == 66){
                etPassword.requestFocus()
                return@setOnKeyListener true
            }
            return@setOnKeyListener false

        }

        //animation for buttons
        btnCustomerLogin.setOnTouchListener(object:View.OnTouchListener {
            override fun onTouch(v: View?, event: MotionEvent?): Boolean{
                when (v) { v ->
                    when (event?.action) { MotionEvent.ACTION_UP -> {
                        btnCustomerLogin.startAnimation(scaleDown)
                    } MotionEvent.ACTION_DOWN -> {
                            btnCustomerLogin.startAnimation(scaleUp)
                        }
                    }
                }
                return v?.onTouchEvent(event) ?: true
            }
        })

        btnStoreLogin.setOnTouchListener(object:View.OnTouchListener {
            override fun onTouch(v: View?, event: MotionEvent?): Boolean{
                when (v) { v ->
                    when (event?.action) { MotionEvent.ACTION_UP -> {
                        btnStoreLogin.startAnimation(scaleDown)
                    } MotionEvent.ACTION_DOWN -> {
                        btnStoreLogin.startAnimation(scaleUp)
                        }
                    }
                }
                return v?.onTouchEvent(event) ?: true
            }
        })

        btnSignup.setOnTouchListener(object:View.OnTouchListener {
            override fun onTouch(v: View?, event: MotionEvent?): Boolean{
                when (v) { v ->
                    when (event?.action) { MotionEvent.ACTION_UP -> {
                        btnSignup.startAnimation(scaleDown)
                    } MotionEvent.ACTION_DOWN -> {
                        btnSignup.startAnimation(scaleUp)
                        }
                    }
                }
                return v?.onTouchEvent(event) ?: true
            }
        })

        //customer Login.
        btnCustomerLogin.setOnClickListener {
            var username = etUsername.text.toString()
            var password = etPassword.text.toString()

            //get user data from database and only get if username and password are equal
            db.collection("users")
            .whereEqualTo("username", username).whereEqualTo("password", password)
            .get()
            .addOnSuccessListener { documents ->
                if (documents.isEmpty()) { //if doesn't match, deny entry
                    Toast.makeText(this@LoginActivity,("Access Denied"),Toast.LENGTH_SHORT).show()
                } else {    //else, enter
                    //check if username have orders. if so, open the previous order
                    db.collection("orders").whereEqualTo("username", username)
                        .get()
                        .addOnSuccessListener { result ->
                            for(data in result){
                                val status = data.data["status"].toString()
                                val transactionId = data.data["transactionID"].toString()
                                if(status.lowercase() == "pending" || status.lowercase() == "ready"){
                                    Toast.makeText(this@LoginActivity,("You have a pending order."),Toast.LENGTH_SHORT).show()
                                    val intent = Intent(this, ClaimingActivity::class.java).apply {
                                        putExtra("transactionId", transactionId)
                                        putExtra("isHavingOrder","true")
                                    }
                                    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
                                    startActivity(intent)
                                    finish()
                                }
                            }
                        }

                    for (document in documents) { //else show home page
                        Toast.makeText(this@LoginActivity,("Access Granted"),Toast.LENGTH_SHORT).show()
                        val intent = Intent(this, HomeActivity::class.java).apply {
                            putExtra("username", username)
                            putExtra("password", password)
                        }
                        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
                        startActivity(intent)
                        finish()
                        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                    }

                
                }
                
            } 
            
        }

        //store login
        btnStoreLogin.setOnClickListener {
            var username = etUsername.text.toString()
            var password = etPassword.text.toString()
            LOGIN_NAME = username

            //get data from stores and see if username and password is equal
            db.collection("stores")
                .whereEqualTo("username", username).whereEqualTo("password", password)
                .get()
                .addOnSuccessListener { documents ->
                    if (documents.isEmpty()) { //if no username or password matched, deny
                        Toast.makeText(this@LoginActivity,("Access Denied"),Toast.LENGTH_SHORT).show()
                    } else { //else, enter

                      for (document in documents) {
							Toast.makeText(this@LoginActivity,("Access Granted"),Toast.LENGTH_SHORT).show()
							LOGIN_ID = document.data["id"].toString()
							val intent = Intent(this, StoreQueueActivity::class.java).apply {
								putExtra("username", LOGIN_NAME)
								putExtra("storeId",LOGIN_ID)
							}
							startActivity(intent)
                            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
						}
                    }
                }
        }

        //enter Signup
        btnSignup.setOnClickListener {
            val intent = Intent(this, SignupActivity::class.java)
            startActivity(intent)
            overridePendingTransition(androidx.appcompat.R.anim.abc_grow_fade_in_from_bottom, androidx.appcompat.R.anim.abc_slide_out_top);

        }
    }

    //quitting the app when back is pressed.
    override fun onBackPressed() {
        //super.onBackPressed()
        val dialog = Dialog(this)
        dialog.setContentView(R.layout.alert_dialog_layout)
        dialog.window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        dialog.setCancelable(false)

        val positiveButton = dialog.findViewById<Button>(R.id.btn_okay)
        val negativeButton = dialog.findViewById<Button>(R.id.btn_cancel)
        val subtitle = dialog.findViewById<TextView>(R.id.alertSubtitle)
        subtitle.text = "Do you want to leave the app?"
        positiveButton.text = "Yes"
        positiveButton.setOnClickListener{
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK
            exitProcess(0)
            dialog.dismiss()
        }
        negativeButton.setOnClickListener{
            dialog.dismiss()
        }

        dialog.show()

    }
}
