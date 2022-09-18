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
import android.widget.Button
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.widget.Toast
import com.google.firebase.firestore.FirebaseFirestore
import android.content.Intent
import TagPrice

import android.app.AlertDialog
import android.view.LayoutInflater
import android.view.ViewGroup

import android.widget.Switch
import android.text.Editable
import android.text.TextWatcher

class RecyclerPrices (private val callbackInterface: CallbackInterface, private val storeId:String, private val btnAddProduct:Button): RecyclerView.Adapter<RecyclerPrices.ViewHolder>() {

    var computedPrices = ArrayList<Int>()
    var orderList = ArrayList<String>()	
    var TagPrices = ArrayList<TagPrice>()
    
    var db = FirebaseFirestore.getInstance()
    var docRef = db.collection("products/store$storeId/prices")

    init {
		docRef.get().addOnSuccessListener { 
			result ->
            var i:Int=0;
			for (document in result){
				var product = document.toObject<TagPrice>(TagPrice::class.java)
				TagPrices.add(product)
                notifyItemInserted(i)
                    i=i+1
				}
            TagPrices.add(TagPrice("",0,true))
			notifyItemInserted(i)
        }     
    }

    interface CallbackInterface {   
        fun passResultCallback(totalPrice: String, strOrderList: String, strComputedPrices: String)
    }
	
    inner class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        var etProduct: EditText
        var etPrice: EditText
        var ivRemove: ImageView
        var ivUpdate: ImageView
        var swProd: Switch
       	 init {
            etProduct = itemView.findViewById(R.id.etProdName)
            etPrice = itemView.findViewById(R.id.etProdPrice)
            ivRemove = itemView.findViewById(R.id.ivProdRemove)
            ivUpdate = itemView.findViewById(R.id.ivProdUpdate)
            swProd = itemView.findViewById(R.id.switchProd)
        }
        
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val docref = db.collection("products/store$storeId/prices")
        holder.etProduct.clearFocus()
            holder.etProduct.setText(TagPrices.size.toString())
            if (TagPrices.size > 0) {
                val i = holder.getBindingAdapterPosition()
                val product = TagPrices.get(i)
                holder.etProduct.setText(product.name)
                holder.etPrice.setText(product.price.toString())
                holder.swProd.setChecked(product.available!!)
            }

        holder.swProd.setOnCheckedChangeListener {
            _, isChecked ->
            if (isChecked) {
                docref.whereEqualTo("name",TagPrices.get(holder.getBindingAdapterPosition()).name).get().addOnSuccessListener { 
                    result -> 
                    for (data in result){
                         docref.document(data.id).update("available",true)
                    }
                }     
            } else {
                docref.whereEqualTo("name",TagPrices.get(holder.getBindingAdapterPosition()).name).get().addOnSuccessListener { 
                    result -> 
                    for (data in result){
                         docref.document(data.id).update("available",false)
                    }
                }    
            }
        }
	
        if (holder.getBindingAdapterPosition() == TagPrices.size - 1){
         	      holder.etProduct.setText("")
         		holder.etPrice.setText("")
        } 

        holder.ivRemove.setOnClickListener {

                if (holder.getBindingAdapterPosition() == TagPrices.size-1) {
                    holder.etProduct.setText("")
                    holder.etPrice.setText("")
                }

                if (holder.getBindingAdapterPosition() != TagPrices.size-1) {
    	       	    val productName = holder.etProduct.text.toString()

                    val builder = AlertDialog.Builder(holder.etPrice.getContext())
                    builder.setMessage("Are you sure you want to delete $productName from list")
                        .setCancelable(false)
                        .setPositiveButton("Yes"){
                            dialog,id->
                        
                    val dr = db.collection("products/store$storeId/prices")
                    dr.whereEqualTo("name",holder.etProduct.text.toString()).get().addOnSuccessListener { 
                         results ->
                         for(data in results){
                             dr.document(data.id).delete()
                         }
                         Toast.makeText(holder.etProduct.getContext(),"Product Deleted", Toast.LENGTH_SHORT).show()
                         TagPrices.removeAt(holder.getBindingAdapterPosition())
                         notifyItemRemoved(holder.getBindingAdapterPosition())
                    }.addOnFailureListener{ 
                    }
                            }
                    .setNegativeButton("No"){dialog, id ->
                        dialog.dismiss()
                    }
                    builder.create().show()
            }
        }

        btnAddProduct.setOnClickListener{
            try {
                if (holder.etProduct.text.toString() != "" && holder.etPrice.text.toString().toInt() > 0 &&  holder.etPrice.text.toString()!="")
                    {
                        val product = TagPrice(holder.etProduct.text.toString(),holder.etPrice.text.toString().toInt(),true)
                        db.collection("products/store$storeId/prices").get().addOnSuccessListener{
                            result ->
                                var found = false
                                for (data in result)
                                {
                                    if (data["name"]!!.toString().lowercase() == product.name!!.lowercase())
                                    {
                                        found = true     
                                    }
                                }

                                if (found==false)
                                {
                                    TagPrices.add(product)
                                    db.collection("products/store$storeId/prices").add(product).addOnSuccessListener{
                                        Toast.makeText(holder.etProduct.getContext(),"Product Added",Toast.LENGTH_SHORT).show()
                                        notifyItemInserted(holder.getBindingAdapterPosition()+1) 
                                    }
                                } else {
                                    Toast.makeText(holder.etProduct.getContext(),"Product Already Existing",Toast.LENGTH_SHORT).show()
                                }
                        }
                    } else {
                        Toast.makeText(holder.etProduct.getContext(),"Missing Input(s)",Toast.LENGTH_SHORT).show() 
                    }
            } catch (e:NumberFormatException){
                 Toast.makeText(holder.etProduct.getContext(),"Wrong Price Format",Toast.LENGTH_SHORT).show()
            }  
        }

        holder.ivUpdate.setOnClickListener {
            try {
                if (holder.etProduct.text.toString() != "" && holder.etPrice.text.toString().toInt() > 0 &&  holder.etPrice.text.toString()!="") {
                    val product = TagPrice(holder.etProduct.text.toString(),holder.etPrice.text.toString().toInt(),true)
                    db.collection("products/store$storeId/prices").get().addOnSuccessListener{
                        result ->
                            var found = false
                            for (data in result)
                            {
                                if (data["name"]!!.toString().lowercase() == product.name!!.lowercase())
                                {
                                    docref.document(data.id).set(product)
                                    TagPrices.set(holder.getBindingAdapterPosition(),product)
                                    Toast.makeText(holder.etProduct.getContext(),"Product Updated",Toast.LENGTH_SHORT).show()
                                    found = true
                                }
                            }

                            if (found==false)
                            {
                                TagPrices.add(product)
                                db.collection("products/store$storeId/prices").add(product).addOnSuccessListener{
                                    Toast.makeText(holder.etProduct.getContext(),"Product Added",Toast.LENGTH_SHORT).show()
                                    notifyItemInserted(holder.getBindingAdapterPosition()+1) 
                                }
                            } 
                    }
                } else {
                   Toast.makeText(holder.etProduct.getContext(),"Missing Input(s)",Toast.LENGTH_SHORT).show() 
                }
            } catch (e:NumberFormatException){
                 Toast.makeText(holder.etProduct.getContext(),"Wrong Price Format",Toast.LENGTH_SHORT).show()
            }
        }
    }    

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.store_price_layout, parent, false)
        return ViewHolder(v)
    }

    override fun getItemCount(): Int {
        return TagPrices.size
    }
}