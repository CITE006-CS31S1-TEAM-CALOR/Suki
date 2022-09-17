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
import java.util.ListIterator;
import android.view.LayoutInflater
import android.view.ViewGroup
import android.text.Editable
import android.text.TextWatcher



class RecyclerAdapter (private val callbackInterface: CallbackInterface, private val storeId:String, 
private val choice1:Button, private val choice2:Button, private val choice3:Button): RecyclerView.Adapter<RecyclerAdapter.ViewHolder>() {

    var computedPrices = ArrayList<Int>()
    var orderList = ArrayList<String>()	
    var isNotifyChange: Boolean = false
    val TagPrices = ArrayList<TagPrice>()
    val db = FirebaseFirestore.getInstance()
    init {
    		   val docRef = db.collection("products").document("store"+storeId).collection("prices")
			docRef.get().addOnSuccessListener { 
				result ->
				for (document in result){
					val product = document.toObject<TagPrice>(TagPrice::class.java)!!
					 TagPrices.add(product)
					}
				}
				  
            orderList.add("0")
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
	            callbackInterface.passResultCallback(totalPrice.toString(),orderList.joinToString(),computedPrices.joinToString())  
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
        
          
        val textWatcher:TextWatcher = object:TextWatcher {
                override fun afterTextChanged(s: Editable){
                    val strOrder:String = s.toString()
                    
                    try {
                        val prodqty = strOrder.split(" ").get(0).trim()
                        val matches = getMatchingStrings(strOrder.split(" ").get(1))
                        //Toast.makeText(holder.etOrder.getContext(),matches.joinToString(),Toast.LENGTH_LONG).show()
                        if (matches.size >= 3){
                            choice1.text = matches.get(0)
                            choice2.text = matches.get(1)
                            choice3.text = matches.get(2)
                            
                            choice1.setOnClickListener {
                                holder.etOrder.setText(prodqty + " " + matches.get(0).toString()+ "\n")
                            }
                            choice2.setOnClickListener {
                                holder.etOrder.setText(prodqty+ " "  + matches.get(1).toString() + "\n")
                            }
                            choice3.setOnClickListener {
                                holder.etOrder.setText(prodqty+ " "  + matches.get(2).toString()+ "\n")
                            }
                        }
                        if (matches.size == 2){
                            choice2.text = ""
                            choice2.text = matches.get(0)
                            choice3.text = matches.get(1)

                            choice2.setOnClickListener {
                                holder.etOrder.setText(prodqty+ " "  + matches.get(0).toString()+ "\n")
                            }
                            choice3.setOnClickListener {
                                holder.etOrder.setText(prodqty+ " "  + matches.get(1).toString()+ "\n")
                            }
                        }
                        if (matches.size == 1){
                            choice1.text = ""
                            choice2.text = matches.get(0)
                            choice3.text = ""

                            choice1.setOnClickListener {
                                holder.etOrder.setText(prodqty+ " "  + matches.get(0).toString()+ "\n")
                            }
                            choice2.setOnClickListener {
                                holder.etOrder.setText(prodqty+ " "  + matches.get(0).toString()+ "\n")
                            }
                            choice3.setOnClickListener {
                                holder.etOrder.setText(prodqty+ " "  + matches.get(0).toString()+ "\n")
                            }

                        }

                         

                    } catch (e: IndexOutOfBoundsException){
                        
                    }


                    val pattern = "\\d [A-Za-z0-9]*\\n".toRegex()
                    val found = pattern.find(strOrder)
                    val m = found?.value
                    if (m != null){
                        holder.etOrder.setText(strOrder.trim())	
                        orderList.set(holder.getBindingAdapterPosition(),strOrder.trim())

                        val lsOrder = strOrder.split(" ")
                        val productName: String = lsOrder.get(1)
                        val qty: Int = lsOrder.get(0).toInt()
                        val unitPrice: Int? = (TagPrices.firstOrNull {it.name == productName.trim()})?.price ?: 0
                        val computedPrice = (unitPrice!! * qty)
                        val available: Boolean? = (TagPrices.firstOrNull {it.name == productName.trim()})?.available ?: false
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
        val p = Pattern.compile(regex+".*");

        for (s in list) {
            if (p.matcher(s.name!!.toLowerCase()).matches() && s.available==true) {
            matches.add(s.name!!);
            }
        }

        return matches
    }
}



