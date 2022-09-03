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

class RecyclerAdapter: RecyclerView.Adapter<RecyclerAdapter.ViewHolder>() {

    var title = ArrayList<String>()	
    var isNotifyChange: Boolean = false
    val TagPrices = ArrayList<TagPrice>()
    val db = FirebaseFirestore.getInstance()
    val docRef = db.collection("products").document()
    init {
            title.add("")
            TagPrices.add(TagPrice("toyo",15))
            TagPrices.add(TagPrice("suka",125))
            TagPrices.add(TagPrice("patis",135))
            TagPrices.add(TagPrice("tt",150))
            TagPrices.add(TagPrice("pp",150))
    }

    inner class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {

        var etOrder: EditText
        var ivRemove: ImageView
        var tvComputedPrice: TextView
        init {
            etOrder = itemView.findViewById(R.id.etOrder)
            ivRemove = itemView.findViewById(R.id.ivRemove)
            tvComputedPrice = itemView.findViewById(R.id.tvComputedPrice)
        }
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.etOrder.setText(title.get(position))
        holder.ivRemove.setOnClickListener {
            if (title.size!=1 && position<(title.size - 1)){
            		title.removeAt(position)
               		notifyDataSetChanged()
            	} else {
            		holder.etOrder.setText("")	
            }
        }
        
          Toast.makeText(holder.etOrder.getContext(), title.joinToString(), Toast.LENGTH_SHORT).show()
          
	val textWatcher:TextWatcher = object:TextWatcher {
        	override fun afterTextChanged(s: Editable){
        		val strOrder:String = s.toString()
        		val pattern = "\\d [A-Za-z]*\\n".toRegex()
        		val found = pattern.find(strOrder)
        		val m = found?.value
        		if (m != null){
	            	holder.etOrder.setText(strOrder.trim())	
	        		title.set(position,strOrder.trim())

                    val lsOrder = strOrder.split(" ")
                    val productName: String = lsOrder.get(1)
                    val qty: Int = lsOrder.get(0).toInt()
                    val unitPrice: Int? = (TagPrices.firstOrNull {it.name == productName.trim()})?.price ?: 0
                    val computedPrice = (unitPrice!! * qty)
                    holder.tvComputedPrice.text = computedPrice.toString()
				    if (!title.get(title.size-1).equals("")){									
					    title.add("")					
		        	}
				//notifyItemChanged(position-1)			
        		}
        		
        		
        	}
        	
        	override fun beforeTextChanged(s:CharSequence, start: Int,count: Int, after: Int){
        	
        	}
        	
        	override fun onTextChanged(s:CharSequence, start:Int,before:Int,count:Int){

        	}
        	
        }
        
        holder.etOrder.addTextChangedListener(textWatcher)
        
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.orderlayout, parent, false)
        return ViewHolder(v)
    }

    override fun getItemCount(): Int {
        return title.size
    }
}


