package com.example.pabili

import android.app.AlertDialog
import android.content.Context
import android.service.autofill.FieldClassification
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore
import java.util.*
import kotlin.collections.ArrayList

class RecyclerOrder (private val context: Context, private val cDoc: String, private val mList: ArrayList<DataOrderList>, private val totaltxt: TextView?) : RecyclerView.Adapter<RecyclerOrder.ViewHolder>(){
    class ViewHolder(ItemView : View) : RecyclerView.ViewHolder(ItemView){
        val name: TextView = itemView.findViewById(R.id.OrderName)
        val qty: TextView = itemView.findViewById(R.id.OrderQty)
        val pri: TextView = itemView.findViewById(R.id.OrderPrice)
        val num: TextView = itemView.findViewById(R.id.numTxt)
        val del: ImageButton = itemView.findViewById(R.id.btnDeleteOrder)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.store_order_layout, parent, false)

        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val DataOrderList = mList[position]

        val db = FirebaseFirestore.getInstance()
        val oName = DataOrderList.nameOrder
        val oQty = DataOrderList.qtyOrder
        val oPrice = DataOrderList.priOrder
        val orders = db.collection("orders").document(cDoc)

        holder.num.text = ((position+1).toString()) + ": "
        holder.name.text = oName
        holder.qty.text = oQty
        holder.pri.text = oPrice
        holder.del.setOnClickListener {
            val builder = AlertDialog.Builder(context)
            builder.setMessage("Are you sure you want to delete $oName?")
                .setCancelable(false)
                .setPositiveButton("Yes"){dialog, id ->
                    Log.d("TAG","$position")
                    orders.get()
                        .addOnSuccessListener { result ->
                            //update computedPrices
                            var regex = "\\d+".toRegex()
                            var match: Sequence<MatchResult> = regex.findAll(result.getString("computedPrices").toString())
                            var array = LinkedList<String>()
                            for(i in match){
                                array.add(i.value)
                            }
                            array.removeAt(position)
                            orders.update("computedPrices",array.toString().replace("[","").replace("]",""))

                            //update orderList
                            regex = "\\d+ \\w+".toRegex()
                            var mmatch: Sequence<MatchResult> = regex.findAll(result.getString("orderList").toString())
                            var aaray = LinkedList<String>()
                            for(i in mmatch){
                                aaray.add(i.value)
                            }
                            aaray.removeAt(position)
                            orders.update("orderList",aaray.toString().replace("[","").replace("]","")+", ")

                            //update totalPrice
                            var sum = 0
                            for(i in array){
                                sum += i.toInt()
                            }
                            orders.update("totalPrice", sum.toString())

                            totaltxt!!.text = "total: P$sum"
                            mList.removeAt(holder.bindingAdapterPosition)
                            notifyItemRemoved(holder.bindingAdapterPosition)
                            notifyDataSetChanged()
                        }

                }
                .setNegativeButton("No"){dialog, id ->
                    dialog.dismiss()
                }
            builder.create().show()
        }

    }

    override fun getItemCount(): Int {
        return mList.size
    }
}