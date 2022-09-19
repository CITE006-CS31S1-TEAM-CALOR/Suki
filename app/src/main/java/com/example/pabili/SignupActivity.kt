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
import android.view.MotionEvent
import android.view.animation.Animation
import android.view.animation.AnimationUtils

class SignupActivity : AppCompatActivity() {

	//create variable under class
	private lateinit var db: FirebaseFirestore
	private lateinit var etUsername: EditText
	private lateinit var etPassword: EditText
	private lateinit var etVerifyPassword: EditText
	private lateinit var btnCustomerSignup: Button
	private lateinit var btnStoreSignup: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)

		//set variables
		db = FirebaseFirestore.getInstance()
		etUsername = findViewById(R.id.etUsername) as EditText
		etPassword = findViewById(R.id.etPassword) as EditText
		etVerifyPassword = findViewById(R.id.etVerifyPassword) as EditText
		btnCustomerSignup = findViewById(R.id.btnCustomerSignup)
		btnStoreSignup = findViewById(R.id.btnStoreSignup)

		val scaleUp: Animation = AnimationUtils.loadAnimation(this,R.anim.scale_up)
		val scaleDown: Animation = AnimationUtils.loadAnimation(this,R.anim.scale_down)

		//animation for the buttons
		btnCustomerSignup.setOnTouchListener(object:View.OnTouchListener {
			override fun onTouch(v: View?, event: MotionEvent?): Boolean{
				when (v) { v ->
					when (event?.action) { MotionEvent.ACTION_UP -> {
						btnCustomerSignup.startAnimation(scaleDown)
					} MotionEvent.ACTION_DOWN -> {
						btnCustomerSignup.startAnimation(scaleUp)
					}
					}
				}
				return v?.onTouchEvent(event) ?: true
			}
		})

		btnStoreSignup.setOnTouchListener(object:View.OnTouchListener {
			override fun onTouch(v: View?, event: MotionEvent?): Boolean{
				when (v) { v ->
					when (event?.action) { MotionEvent.ACTION_UP -> {
						btnStoreSignup.startAnimation(scaleDown)
					} MotionEvent.ACTION_DOWN -> {
						btnStoreSignup.startAnimation(scaleUp)
					}
					}
				}
				return v?.onTouchEvent(event) ?: true
			}
		})

		//register customer
		try {
			btnCustomerSignup.setOnClickListener {
				registerCustomer()
			}
			// register store
			btnStoreSignup.setOnClickListener {
				registerStore()
			}
		} catch (e: IndexOutOfBoundsException) { //if theres an error, toast
			Toast.makeText(this@SignupActivity,"There was an error. Try again",Toast.LENGTH_SHORT).show()
		}
	}

fun registerCustomer(){
	//get username, pass and ver pass
	val username = etUsername.text.toString()
	val password = etPassword.text.toString()
	val verpassword = etVerifyPassword.text.toString()

	//focus on fields that are empty
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

	//if no error, put the account to users database
	val account = hashMapOf(
		"username" to username,
		"password" to password,
		"name" to "",
		"email" to "",
		"phone" to "",
		"address" to "",
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
		//create id for store
		var id:Int = Random().nextInt(10000);

		Toast.makeText(this@SignupActivity, "id:$id", Toast.LENGTH_SHORT).show()

		//get necessary values
		val username = etUsername.text.toString()
		val password = etPassword.text.toString()
		val verpassword = etVerifyPassword.text.toString()

		//focus on field that is empty
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

		//if no error, add account to store in the database
		val account = hashMapOf(
			"username" to username,
			"password" to password,
			"id" to id
		)

		//set price under SRP
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

		//add account to database
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
							startActivity(intent)
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



    
