package com.example.pabili

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import android.widget.ImageView
import android.widget.EditText
import android.text.Editable
import android.text.TextWatcher
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore



class RecyclerAdapter: RecyclerView.Adapter<RecyclerAdapter.ViewHolder>() {

    var isNotifyChange: Boolean = false
    var title = ArrayList<String>()
    init {
    
        title.add("12 rhandy")
        title.add("12 qweq")
        title.add("12 qwewweq")
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
        
	val textWatcher:TextWatcher = object:TextWatcher {
        	override fun afterTextChanged(s: Editable){
        		val strOrder:String = s.toString()
        		val pattern = "\\d [A-Za-z]*\\n".toRegex()
        		val found = pattern.find(strOrder)
        		val m = found?.value
        		
        		if (m != null){
	        		title.set(position,strOrder.trim())
				if (!title.get(title.size-1).equals("")){								
					title.add("")					
		        	}
		        	
				notifyItemChanged(position-1)		
	        		
				//Toast.makeText(holder.etOrder.getContext(), title.get(position), Toast.LENGTH_SHORT).show()
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
