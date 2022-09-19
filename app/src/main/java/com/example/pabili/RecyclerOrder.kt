package com.example.pabili

import android.app.Activity
import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.service.autofill.FieldClassification
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore
import java.util.*
import kotlin.collections.ArrayList
import kotlin.properties.Delegates

//private var readyBool by Delegates.notNull<Boolean>()

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
        //var readyBool:Boolean

        holder.num.text = ((position+1).toString()) + ": "
        holder.name.text = oName
        holder.qty.text = oQty
        holder.pri.text = oPrice
        holder.del.setOnClickListener {
            orders.get().addOnSuccessListener { result->
                val readyBool = booleanStatus(result.getString("status").toString())
                if(readyBool) {
                    val dialog = Dialog(context)
                    dialog.setContentView(R.layout.alert_dialog_layout)
                    dialog.window?.setLayout(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT
                    )
                    dialog.setCancelable(false)

                    val positiveButton = dialog.findViewById<Button>(R.id.btn_okay)
                    val negativeButton = dialog.findViewById<Button>(R.id.btn_cancel)
                    val subtitle = dialog.findViewById<TextView>(R.id.alertSubtitle)
                    subtitle.text = "Do you want to delete $oName?"
                    positiveButton.setOnClickListener {
                        Log.d("TAG", "$position")
                        orders.get()
                            .addOnSuccessListener { result ->
                                //update computedPrices
                                var regex = "\\d+[^\\w]".toRegex()
                                var match: Sequence<MatchResult> =
                                    regex.findAll(result.getString("computedPrices").toString())
                                var array = LinkedList<String>()
                                for (i in match) {
                                    array.add(i.value)
                                }
                                array.removeAt(position)
                                orders.update(
                                    "computedPrices",
                                    array.toString().replace("[", "").replace("]", "")
                                )

                                //update orderList
                                regex = "\\d+ \\S+[^,]+".toRegex()
                                var mmatch: Sequence<MatchResult> =
                                    regex.findAll(result.getString("orderList").toString())
                                var aaray = LinkedList<String>()
                                for (i in mmatch) {
                                    aaray.add(i.value)
                                }
                                aaray.removeAt(position)
                                orders.update(
                                    "orderList",
                                    aaray.toString().replace("[", "").replace("]", "") + ", "
                                )

                                //update totalPrice
                                var sum = 0
                                for (i in array) {
                                    sum += i.replace(" ","").toInt()
                                }
                                orders.update("totalPrice", sum.toString())

                                totaltxt!!.text = "total: P$sum"
                                Toast.makeText(context, "${position+1}. $oName has been deleted", Toast.LENGTH_SHORT).show()
                                mList.removeAt(holder.bindingAdapterPosition)
                                notifyItemRemoved(holder.bindingAdapterPosition)
                                notifyDataSetChanged()
                                if(mList.isEmpty()){
                                    orders.update("status", "cancel")
                                    Toast.makeText(context, "Whole Order's canceled. Back to Queue", Toast.LENGTH_SHORT)

                                    val intent = Intent(context, StoreQueueActivity::class.java).apply {
                                        putExtra("username", LOGIN_NAME)
                                        putExtra("storeId",LOGIN_ID)
                                    }
                                    context.startActivity(intent)
                                    (context as Activity).finish()
                                }
                            }
                        dialog.dismiss()
                    }
                    negativeButton.setOnClickListener {
                        dialog.dismiss()
                    }

                    dialog.show()
                    /*
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

                 */
                }else{
                    Toast.makeText(context, "The order is ready. Can't edit order.", Toast.LENGTH_SHORT).show()
                }
            }.addOnFailureListener { r ->
                Toast.makeText(context, "Couldn't retrieve data from the database. Check your internet", Toast.LENGTH_SHORT).show()
                Log.d("ERROR", "$r")
            }

        }



    }

    override fun getItemCount(): Int {
        return mList.size
    }

    private fun booleanStatus(string: String): Boolean{
        return when(string){
            "pending" -> true
            else -> false
        }
    }
}