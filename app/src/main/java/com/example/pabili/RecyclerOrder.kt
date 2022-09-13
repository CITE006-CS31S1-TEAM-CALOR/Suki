package com.example.pabili

import android.app.AlertDialog
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore
import java.util.*

class RecyclerOrder (private val context: Context, private val cDoc: String, private val mList: List<DataOrderList>) : RecyclerView.Adapter<RecyclerOrder.ViewHolder>(){
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
                            var regex = "\\d+".toRegex()
                            var match: Sequence<MatchResult> = regex.findAll(result.getString("computedPrices").toString())
                            var array = LinkedList<String>()
                            for(i in match){
                                array.add(i.value)
                            }
                            array.removeAt(position)
                            orders.update("computedPrices",array.toString().replace("[","").replace("]",""))

                            //TODO update orderList and total Prices after deleting
                            regex = "".toRegex()


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