package com.example.pabili

import android.annotation.SuppressLint
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
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.app.NotificationCompat.getAction
import androidx.core.view.accessibility.AccessibilityEventCompat.getAction
import androidx.core.widget.doOnTextChanged

var LOGIN_NAME = ""
var LOGIN_ID = ""
class LoginActivity : AppCompatActivity() {

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(R.style.Theme_Pabili)
        setContentView(R.layout.activity_login)

    //try {]
        val constraintLayout: ConstraintLayout = findViewById(R.id.mainLayout)
        val animationDrawable: AnimationDrawable = constraintLayout.getBackground() as AnimationDrawable
        animationDrawable.setEnterFadeDuration(2500);
        animationDrawable.setExitFadeDuration(5000);
        animationDrawable.start()

        val etUsername: EditText = findViewById(R.id.etUsername)
        val etPassword: EditText = findViewById(R.id.etPassword)
        val btnCustomerLogin: Button = findViewById(R.id.btnCustomerLogin)
        val btnStoreLogin: Button = findViewById(R.id.btnStoreLogin)
        val btnSignup: Button = findViewById(R.id.btnSignup)

        val db = FirebaseFirestore.getInstance()

        val scaleUp: Animation = AnimationUtils.loadAnimation(this,R.anim.scale_up)
        val scaleDown: Animation = AnimationUtils.loadAnimation(this,R.anim.scale_down)

        etUsername.setOnKeyListener { v, keyCode, event ->
            Log.d("Keytouch", "$keyCode")
            if(keyCode == 66){
                etPassword.requestFocus()
                return@setOnKeyListener true
            }
            return@setOnKeyListener false

        }

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

        btnCustomerLogin.setOnClickListener {
            var username = etUsername.text.toString()
            var password = etPassword.text.toString()
            db.collection("users")
            .whereEqualTo("username", username).whereEqualTo("password", password)
            .get()
            .addOnSuccessListener { documents ->
                if (documents.isEmpty()) {
                    Toast.makeText(this@LoginActivity,("Access Denied"),Toast.LENGTH_SHORT).show()
                } else {
                
                    for (document in documents) {
                    Toast.makeText(this@LoginActivity,("Access Granted"),Toast.LENGTH_SHORT).show()
                     val intent = Intent(this, HomeActivity::class.java).apply {
                             putExtra("username", username)
                             putExtra("password", password)
                     }
                     startActivity(intent)
                        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);

                    }
                
                }
                
            } 
            
        }
        
        btnStoreLogin.setOnClickListener {
            var username = etUsername.text.toString()
            var password = etPassword.text.toString()
            LOGIN_NAME = username
            db.collection("stores")
                .whereEqualTo("username", username).whereEqualTo("password", password)
                .get()
                .addOnSuccessListener { documents ->
                    if (documents.isEmpty()) {
                        Toast.makeText(this@LoginActivity,("Access Denied"),Toast.LENGTH_SHORT).show()
                    } else {

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

        btnSignup.setOnClickListener {
            val intent = Intent(this, SignupActivity::class.java)
            startActivity(intent)
            overridePendingTransition(androidx.appcompat.R.anim.abc_grow_fade_in_from_bottom, androidx.appcompat.R.anim.abc_slide_out_top);

        }
        
        
            
    /*          
    } catch (e: IOException) {
        Toast.makeText(this@LoginActivity,("ttt"),Toast.LENGTH_SHORT).show()
    }*/
    }
}
