package com.example.pabili
import java.util.regex.Pattern;
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



class RecyclerAdapter (private val callbackInterface: CallbackInterface, private val storeId:String, 
private val choice1:Button, private val choice2:Button, private val choice3:Button, private var orderList:ArrayList<String>, private var computedPrices: ArrayList<Int>): RecyclerView.Adapter<RecyclerAdapter.ViewHolder>() {
    var isNotifyChange: Boolean = false
    val TagPrices = ArrayList<TagPrice>()
    val db = FirebaseFirestore.getInstance()
    var isInit = false
    init {

            choice1.setVisibility(View.GONE)
            choice2.setVisibility(View.GONE)
            choice3.setVisibility(View.GONE)
            
    		val docRef = db.collection("products").document("store"+storeId).collection("prices")
			docRef.get().addOnSuccessListener { 
			result ->
			for (document in result){
				val product = document.toObject<TagPrice>(TagPrice::class.java)
				 TagPrices.add(product)
				}
			}
			
            if (orderList.isEmpty()){
                orderList.add("")
            }
            notifyItemInserted(0);
            
    }

        interface CallbackInterface {   
            fun passResultCallback(totalPrice: String, strOrderList: String, strComputedPrices: String)
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
        if (isInit==false){
            if (holder.getBindingAdapterPosition()<orderList.size-1){
                holder.etOrder.setText(orderList.get(holder.getBindingAdapterPosition()))
                holder.tvComputedPrice.text=computedPrices.get(holder.getBindingAdapterPosition()).toString()
                callbackInterface.passResultCallback(computedPrices.sum().toString(),orderList.joinToString(),computedPrices.joinToString())  
            } else {
                isInit = true
            }
        }

        if (holder.getBindingAdapterPosition() == orderList.size - 1){
         	holder.etOrder.setText("")
         	holder.tvComputedPrice.text = ""
            holder.etOrder.requestFocus()
        }
	//holder.etOrder.setText(holder.getBindingAdapterPosition().toString())
        holder.ivRemove.setOnClickListener {
        
            	//	orderList.removeLast()
		//	notifyItemRemoved(holder.getBindingAdapterPosition())               		
              // 		Toast.makeText(holder.etOrder.getContext(), orderList.joinToString(), Toast.LENGTH_SHORT).show()
            
            if (holder.getBindingAdapterPosition() == orderList.size-1) {
            	holder.etOrder.setText("")
            } 
            
            
            if (holder.getBindingAdapterPosition() != orderList.size-1) {
          		computedPrices.removeAt(holder.getBindingAdapterPosition())
	    		val totalPrice = computedPrices.sum()	
	       		orderList.removeAt(holder.getBindingAdapterPosition())
	       		notifyItemRemoved(holder.getBindingAdapterPosition())
	       		Toast.makeText(holder.etOrder.getContext(), orderList.joinToString(), Toast.LENGTH_SHORT).show()
                callbackInterface.passResultCallback(totalPrice.toString(),orderList.joinToString(),computedPrices.joinToString())  
            } 
            
        }
        /*
        holder.tvComputedPrice.setOnClickListener {
            		orderList.add((orderList.size).toString())
            		notifyItemInserted(holder.getBindingAdapterPosition()+1);
               			
               		Toast.makeText(holder.etOrder.getContext(), orderList.joinToString(), Toast.LENGTH_SHORT).show()
        }*/
        
          
        val textWatcher:TextWatcher = object:TextWatcher {
                override fun afterTextChanged(s: Editable){
                    val strOrder:String = s.toString()
                    var toSearch:String
                    try {
                        val prodqty = strOrder.lowercase().split(" ").get(0).trim()
                        toSearch = strOrder.split(" ",limit=2).get(1)
                        val matches = getMatchingStrings(toSearch)

                        if (prodqty.toIntOrNull() != null){
                            choice1.setVisibility(View.VISIBLE)
                            choice2.setVisibility(View.VISIBLE)
                            choice3.setVisibility(View.VISIBLE)
                        } else {
                            choice1.setVisibility(View.GONE)
                            choice2.setVisibility(View.GONE)
                            choice3.setVisibility(View.GONE)
                        }

                        if (matches.size >= 3){
                            choice1.text = matches.get(0)
                            choice2.text = matches.get(1)
                            choice3.text = matches.get(2)
                            
                            choice1.setOnClickListener {
                                holder.etOrder.setText(prodqty + " " + choice1.text.toString() + "\n")                    
                            }
                            choice2.setOnClickListener {
                                holder.etOrder.setText(prodqty+ " "  + choice2.text.toString() + "\n")
                            }
                            choice3.setOnClickListener {
                                holder.etOrder.setText(prodqty+ " "  + choice3.text.toString() + "\n")
                            }
                        }
                        if (matches.size == 2){
                            choice1.text = matches.get(0)
                            choice2.text = matches.get(1)
                            choice3.setVisibility(View.GONE)
                            

                            choice1.setOnClickListener {
                                holder.etOrder.setText(prodqty+ " "  + choice2.text.toString() + "\n")
                            }
                            choice2.setOnClickListener {
                                holder.etOrder.setText(prodqty+ " "  + choice3.text.toString() + "\n")
                            }
                        }
                        if (matches.size == 1){
                            choice1.setVisibility(View.VISIBLE)
                            choice2.setVisibility(View.GONE)
                            choice3.setVisibility(View.GONE)

                            choice1.text = matches.get(0)

                            choice1.setOnClickListener {
                                holder.etOrder.setText(prodqty+ " "  + choice1.text.toString() + "\n")
                            }

                        }

                        if (matches.size == 0){
                            choice1.setVisibility(View.GONE)
                            choice2.setVisibility(View.GONE)
                            choice3.setVisibility(View.GONE)
                        }
                         

                    } catch (e: IndexOutOfBoundsException){
                        
                    }


                    val pattern = "\\d [A-Za-z0-9 ]*\\n".toRegex()
                    val found = pattern.find(strOrder)
                    val m = found?.value
                    if (m != null){
                        holder.etOrder.setText(strOrder.trim())	
                        orderList.set(holder.getBindingAdapterPosition(),strOrder.trim())

                        val lsOrder = strOrder.split(" ",limit=2)
                       // var productName: String = lsOrder.get(1).lowercase()
                        var rawName:String = lsOrder.get(1).lowercase().trim()
                        
                        var productName: String? = (TagPrices.firstOrNull {it.name!!.lowercase() == lsOrder.get(1).lowercase().trim()})?.name ?: rawName!!
                        val qty: Int = lsOrder.get(0).toInt()
                        val unitPrice: Int? = (TagPrices.firstOrNull {it.name!!.lowercase() == rawName.trim().lowercase()})?.price ?: 0
                        val computedPrice = (unitPrice!! * qty)
                        val available: Boolean? = (TagPrices.firstOrNull {it.name!!.lowercase() == rawName.trim().lowercase()})?.available ?: false
                        
                        holder.etOrder.setText("$qty " + productName)
                        if (computedPrice == 0 || available==false){
                            Toast.makeText(holder.etOrder.getContext(),"Product Unavailable",Toast.LENGTH_SHORT).show()
                            holder.etOrder.setSelection(holder.etOrder.text.length)
                            return
                        } else {
                            holder.tvComputedPrice.text =  computedPrice.toString()
                        }


                        if (orderList.get(orderList.size-1).equals("")){       
                            computedPrices.set(holder.getBindingAdapterPosition(), computedPrice)
                            
	            		
                        } else {	
                            orderList.add("")
                 	        notifyItemInserted(orderList.size - 1);
                            computedPrices.add(computedPrice)
                        }
                    
                    Toast.makeText(holder.etOrder.getContext(), orderList.joinToString(), Toast.LENGTH_SHORT).show()
                    val totalPrice = computedPrices.sum()
                    callbackInterface.passResultCallback(totalPrice.toString(), orderList.joinToString(), computedPrices.joinToString())  
                    holder.etOrder.clearFocus()
                    resetChoices()
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
        return orderList.size
    }

    fun getMatchingStrings(regex:String):ArrayList<String> {
        val list = TagPrices
        
        val matches = ArrayList<String>();

        for (s in list) {
            val front = Pattern.compile("^"+regex.lowercase()+".*");
            if (front.matcher(s.name!!.lowercase()).matches() && s.available==true) {
                matches.add(s.name!!)
            }

            val middle = Pattern.compile(".*" + regex.lowercase() + ".*");
            if (middle.matcher(s.name!!.lowercase()).matches() && s.available==true) {
                if ((matches.firstOrNull { it!!.lowercase() == s.name!!.lowercase()})==null) {
                    matches.add(s.name!!)
                }
            }
        }
        
        

        return matches
    }

    fun resetChoices(){
        choice1.text = ""
        choice2.text = ""
        choice3.text = ""

        choice1.setVisibility(View.GONE)
        choice2.setVisibility(View.GONE)
        choice3.setVisibility(View.GONE)
        

        choice1.setOnClickListener {

        }
        choice2.setOnClickListener {

        }
        choice3.setOnClickListener {

        }
    }
}



