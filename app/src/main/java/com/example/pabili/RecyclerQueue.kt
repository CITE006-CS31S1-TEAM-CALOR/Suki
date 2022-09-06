package com.example.pabili

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class RecyclerQueue (private val mList: List<DataRecyclerQueue>) : RecyclerView.Adapter<RecyclerQueue.ViewHolder>(){

    class ViewHolder(ItemView: View) : RecyclerView.ViewHolder(ItemView){
        val name: TextView = itemView.findViewById(R.id.storeCustomerName)
        val date: TextView = itemView.findViewById(R.id.storeCustomerDate)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder{
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.store_queue_layout, parent, false)

        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int){
        val DataRecyclerQueue = mList[position]
        holder.name.text = DataRecyclerQueue.name
        holder.date.text = DataRecyclerQueue.date
    }

    override fun getItemCount(): Int {
        return mList.size
    }



}