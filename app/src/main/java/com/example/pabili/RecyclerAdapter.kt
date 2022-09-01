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
import City

import android.view.LayoutInflater
import android.view.ViewGroup
import android.text.Editable
import android.text.TextWatcher

class RecyclerAdapter: RecyclerView.Adapter<RecyclerAdapter.ViewHolder>() {

var title = ArrayList<String>()	
    var isNotifyChange: Boolean = false
    init {
    	//Toast.makeText(this@RecyclerAdapter,"test",Toast.LENGTH_SH)
            title.add("")
    }
    
   	

    inner class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {

        var etOrder: EditText
        var ivRemove: ImageView
        init {
            etOrder = itemView.findViewById(R.id.etOrder)
            ivRemove = itemView.findViewById(R.id.ivRemove)

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


