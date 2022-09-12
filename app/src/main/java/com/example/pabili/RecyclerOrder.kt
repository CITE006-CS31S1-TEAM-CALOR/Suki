package com.example.pabili

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore

class RecyclerOrder (private val mList: List<DataOrderList>) : RecyclerView.Adapter<RecyclerOrder.ViewHolder>(){
    class ViewHolder(ItemView : View) : RecyclerView.ViewHolder(ItemView){
        val name: TextView = itemView.findViewById(R.id.OrderName)
        val qty: TextView = itemView.findViewById(R.id.OrderQty)
        val pri: TextView = itemView.findViewById(R.id.OrderPrice)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.store_order_layout, parent, false)

        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val DataOrderList = mList[position]

        val oName = DataOrderList.nameOrder
        val oQty = DataOrderList.qtyOrder
        val oPrice = DataOrderList.priOrder

        holder.name.text = oName
        holder.qty.text = oQty
        holder.pri.text = oPrice
    }

    override fun getItemCount(): Int {
        return mList.size
    }
}