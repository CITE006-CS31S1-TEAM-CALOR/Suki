package com.example.pabili

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.firebase.firestore.FirebaseFirestore
import android.widget.TextView
import android.widget.Toast
import android.widget.Button
import android.widget.EditText
import android.content.Intent
import android.util.Log
import java.util.Random
import android.view.View
import TagPrice

class SignupActivity : AppCompatActivity() {
	private lateinit var db: FirebaseFirestore
	private lateinit var etUsername: EditText
	private lateinit var etPassword: EditText
	private lateinit var etVerifyPassword: EditText
	private lateinit var btnCustomerSignup: Button
	private lateinit var btnStoreSignup: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)

		db = FirebaseFirestore.getInstance()
		etUsername = findViewById(R.id.etUsername) as EditText
		etPassword = findViewById(R.id.etPassword) as EditText
		etVerifyPassword = findViewById(R.id.etVerifyPassword) as EditText
		btnCustomerSignup = findViewById(R.id.btnCustomerSignup)
		btnStoreSignup = findViewById(R.id.btnStoreSignup)

		try {
			btnCustomerSignup.setOnClickListener {
				registerCustomer()
			}


			btnStoreSignup.setOnClickListener {
				registerStore()
			}
		} catch (e: IndexOutOfBoundsException) {
			Toast.makeText(this@SignupActivity,"There was an error. Try again",Toast.LENGTH_SHORT).show()
		}
	}

fun registerCustomer(){
	val username = etUsername.text.toString()
	val password = etPassword.text.toString()
	val verpassword = etVerifyPassword.text.toString()

	if(password != verpassword){
		if(password.isEmpty()){
			Toast.makeText(this@SignupActivity, "Password is Empty", Toast.LENGTH_SHORT).show()
			etPassword.requestFocus()
			return
		}
		Toast.makeText(this@SignupActivity, "Passwords does not match", Toast.LENGTH_SHORT).show()
		etVerifyPassword.requestFocus()
		return
	}

	if(username.isEmpty()) {
		Toast.makeText(this@SignupActivity, "Username is Empty", Toast.LENGTH_SHORT).show()
		etUsername.requestFocus()
		return
	}



	val account = hashMapOf(
		"username" to username,
		"password" to password
	)

	db.collection("users")
		.whereEqualTo("username", username)
		.get()
		.addOnSuccessListener { documents ->
			if (documents.isEmpty){
				db.collection("users")
					.add(account)
					.addOnSuccessListener { documentReference ->
						Toast.makeText(this@SignupActivity,"Account Crated",Toast.LENGTH_SHORT).show()
						val intent = Intent(this, LoginActivity::class.java)
						startActivity(intent)
					}
					.addOnFailureListener{
						Toast.makeText(this@SignupActivity, "There was an error in the server", Toast.LENGTH_SHORT).show()
					}
			}else{
				Toast.makeText(this@SignupActivity, "Account was taken. Change your Username", Toast.LENGTH_SHORT).show()
				etUsername.requestFocus()
			}
		}
		.addOnFailureListener{
			Toast.makeText(this@SignupActivity, "There was an error in the server", Toast.LENGTH_SHORT).show()
		}
	}

	fun registerStore(){
		var id:Int = Random().nextInt(10000);

		Toast.makeText(this@SignupActivity, "id:$id", Toast.LENGTH_SHORT).show()
			
		val username = etUsername.text.toString()
		val password = etPassword.text.toString()
		val verpassword = etVerifyPassword.text.toString()

		if(password != verpassword){
			if(password.isEmpty()){
				Toast.makeText(this@SignupActivity, "Password is Empty", Toast.LENGTH_SHORT).show()
				etPassword.requestFocus()
				return
			}
			Toast.makeText(this@SignupActivity, "Passwords does not match", Toast.LENGTH_SHORT).show()
			etVerifyPassword.requestFocus()
			return
		}
		if(username.isEmpty()) {
			Toast.makeText(this@SignupActivity, "Username is Empty", Toast.LENGTH_SHORT).show()
			etUsername.requestFocus()
			return
		}

		val account = hashMapOf(
			"username" to username,
			"password" to password,
			"id" to id
		)

		db.collection("products/SRP/prices").get().addOnSuccessListener {
			documents ->
			if (documents.isEmpty){
				Toast.makeText(this@SignupActivity,"empty document",Toast.LENGTH_SHORT).show()
					
			}
				for(document in documents){
					val tagPrice = document.toObject<TagPrice>(TagPrice::class.java)
					db.collection("products/store$id/prices").add(tagPrice)
					Toast.makeText(this@SignupActivity,tagPrice.name,Toast.LENGTH_SHORT).show()
							
				}
		}



		db.collection("stores")
			.whereEqualTo("username", username)
			.get()
			.addOnSuccessListener { documents ->
				if (documents.isEmpty){
					db.collection("stores")
						.add(account)
						.addOnSuccessListener { documentReference ->
							Toast.makeText(this@SignupActivity,"Store Crated",Toast.LENGTH_SHORT).show()
							val intent = Intent(this, LoginActivity::class.java)
							//startActivity(intent)
						}
						.addOnFailureListener{
							Toast.makeText(this@SignupActivity, "There was an error in the server", Toast.LENGTH_SHORT).show()
						}
				}else{
					Toast.makeText(this@SignupActivity, "Store was taken. Change your Username", Toast.LENGTH_SHORT).show()
					etUsername.requestFocus()
				}

			}
			.addOnFailureListener{
				Toast.makeText(this@SignupActivity, "There was an error in the server", Toast.LENGTH_SHORT).show()
			}
	}
}



    
