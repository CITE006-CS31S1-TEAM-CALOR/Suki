package com.example.pabili

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.firebase.firestore.FirebaseFirestore
import android.widget.TextView
import android.widget.Toast
import android.widget.Button
import android.widget.EditText
import android.content.Intent
import android.view.View

const val EXTRA_MESSAGE = "com.example.myfirstapp.MESSAGE"
class LoginActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        	
	//try {]	
		
		val db = FirebaseFirestore.getInstance()
		val etUsername: EditText = findViewById(R.id.etUsername)
		val etPassword: EditText = findViewById(R.id.etPassword)
		val btnCustomerLogin: Button = findViewById(R.id.btnCustomerLogin)
		val btnStoreLogin: Button = findViewById(R.id.btnStoreLogin)
		val btnSignup: Button = findViewById(R.id.btnSignup)

		btnCustomerLogin.setOnClickListener {
			var username = etUsername.text.toString()
			var password = etPassword.text.toString()
			var access = false
			db.collection("users")
			.whereEqualTo("username", username).whereEqualTo("password", password)
			.get()
			.addOnSuccessListener { documents ->
			    for (document in documents) {
				access=true
				Toast.makeText(this@LoginActivity,("Access Granted"),Toast.LENGTH_SHORT).show()
				 val intent = Intent(this, MainActivity::class.java).apply {
					    putExtra(EXTRA_MESSAGE, username)
				 }
				 startActivity(intent)				
			    }
			} 
			
			if (access==false){
				Toast.makeText(this@LoginActivity,("Access Denied"), Toast.LENGTH_SHORT).show()
			}
			
		}
		
		btnStoreLogin.setOnClickListener {
			var username = etUsername.text.toString()
			var password = etPassword.text.toString()
			db.collection("stores")
			.whereEqualTo("username", username).whereEqualTo("password", password)
			.get()
			.addOnSuccessListener { documents ->
			    for (document in documents) {
				Toast.makeText(this@LoginActivity,("Access Granted"),Toast.LENGTH_SHORT).show()
			    }
			}
		}

		btnSignup.setOnClickListener {
 			val intent = Intent(this, SignupActivity::class.java)
			startActivity(intent)	
		}
		
		
			
	/*			
	} catch (e: IOException) {
		Toast.makeText(this@LoginActivity,("ttt"),Toast.LENGTH_SHORT).show()
	}*/
    }
}
