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

import android.view.LayoutInflater
import android.view.ViewGroup
import android.text.Editable
import android.text.TextWatcher



class RecyclerPrices (private val callbackInterface: CallbackInterface, private val storeId:String): RecyclerView.Adapter<RecyclerPrices.ViewHolder>() {

    var computedPrices = ArrayList<Int>()
    var orderList = ArrayList<String>()	
    var isNotifyChange: Boolean = false
    var TagPrices = ArrayList<TagPrice>()
    
     var db = FirebaseFirestore.getInstance()
   var docRef = db.collection("products/store1/prices")
    init {
			docRef.get().addOnSuccessListener { 
				result ->
				for (document in result){
					var product = document.toObject<TagPrice>(TagPrice::class.java)
					 TagPrices.add(product)
					}
				}
				  
            notifyItemInserted(0)
            orderList.add("0")
            notifyDataSetChanged()
            
		  	TagPrices.add(TagPrice("",0))
		  	
            
    }

        interface CallbackInterface {   
            fun passResultCallback(totalPrice: String, strOrderList: String, strComputedPrices: String)
        }
	
    inner class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        var etOrder: EditText
        var etPrice: EditText
        var ivRemove: ImageView
       	 init {
            etOrder = itemView.findViewById(R.id.etProdName)
            etPrice = itemView.findViewById(R.id.etProdPrice)
            ivRemove = itemView.findViewById(R.id.ivProdRemove)
        }
        
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
      	holder.etOrder.clearFocus()
    		holder.etOrder.setText(TagPrices.size.toString())
    		if (TagPrices.size > 0) {
    			val i = holder.getBindingAdapterPosition()
    			holder.etOrder.setText(TagPrices.get(i).name)
    			holder.etPrice.setText(TagPrices.get(i).name)
    		}
    		
    		
         if (holder.getBindingAdapterPosition() == orderList.size - 1){
         	//holder.etOrder.setText(TagPrices.get(0).name?:"rhandys")
         //		holder.etPrice.setText("")
         }
	//holder.etOrder.setText(holder.getBindingAdapterPosition().toString())
        holder.ivRemove.setOnClickListener {
        
            	//	orderList.removeLast()
		//	notifyItemRemoved(holder.getBindingAdapterPosition())               		
           		Toast.makeText(holder.etOrder.getContext(),TagPrices.size.toString(), Toast.LENGTH_SHORT).show()
            
            if (holder.getBindingAdapterPosition() == orderList.size-1) {
            	holder.etOrder.setText("")
            } 
            
            
            if (holder.getBindingAdapterPosition() != orderList.size-1) {
	           //   	callbackInterface.passResultCallback(totalPrice.toString(),orderList.joinToString(),computedPrices.joinToString())  
	       		orderList.removeAt(holder.getBindingAdapterPosition())
	       		notifyItemRemoved(holder.getBindingAdapterPosition())
	       		Toast.makeText(holder.etOrder.getContext(), orderList.joinToString(), Toast.LENGTH_SHORT).show()
            } 
            
        }
        /*
        holder.tvComputedPrice.setOnClickListener {
            		orderList.add((orderList.size).toString())
            		notifyItemInserted(holder.getBindingAdapterPosition()+1);
               			
               		Toast.makeText(holder.etOrder.getContext(), orderList.joinToString(), Toast.LENGTH_SHORT).show()
        }*/
        
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.store_price_layout, parent, false)
        return ViewHolder(v)
    }

    override fun getItemCount(): Int {
        return TagPrices.size
    }
}


