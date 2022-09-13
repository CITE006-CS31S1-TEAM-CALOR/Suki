package com.example.pabili

import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class RecyclerQueue (private val context: Context, private val mList: List<DataRecyclerQueue>) : RecyclerView.Adapter<RecyclerQueue.ViewHolder>(){

    class ViewHolder(ItemView: View) : RecyclerView.ViewHolder(ItemView){
        val name: TextView = itemView.findViewById(R.id.HEADERstoreCustomerName)
        val date: TextView = itemView.findViewById(R.id.HEADERstoreCustomerDate)
        val btn: Button = itemView.findViewById(R.id.HEADERstoreCustomerAction)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder{
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.store_queue_layout, parent, false)

        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int){
        val DataRecyclerQueue = mList[position]

        val cName = DataRecyclerQueue.name
        val cDate = DataRecyclerQueue.date
        val cDoc = DataRecyclerQueue.id
        holder.name.text = DataRecyclerQueue.name
        holder.date.text =  DataRecyclerQueue.date

        holder.btn.setOnClickListener(){
            Log.d("TAG", "Name: " + cName + " | Date: " + cDate + " | ID: " + cDoc)

            val intent = Intent(context, StoreOrderActivity::class.java).apply {
                putExtra("cDoc", cDoc)
            }
            context.startActivity(intent)
/*
            db.collection("orders")
                .document(cDoc)
                .get()
                .addOnSuccessListener { result ->

                    /*
                    val cInfo = result.data?.toList()
                    Log.d("TAG", cInfo.toString())
                     */

                    val cInfo = result.toObject<DataCustomerInfo>()
                    val intent = Intent(context, StoreOrderActivity::class.java).apply {
                        putExtra("Poop", cInfo)
                    }
                    context.startActivity(intent)


                }

 */

        }

    }

    override fun getItemCount(): Int {
        return mList.size
    }



}