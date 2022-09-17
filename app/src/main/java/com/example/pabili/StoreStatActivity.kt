package com.example.pabili

import android.app.AlertDialog
import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.DashPathEffect
import android.graphics.drawable.Drawable
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.*
import com.github.mikephil.charting.formatter.DefaultAxisValueFormatter
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.github.mikephil.charting.formatter.ValueFormatter
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet
import com.github.mikephil.charting.listener.OnChartValueSelectedListener
import com.github.mikephil.charting.utils.Utils
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class StoreStatActivity : AppCompatActivity() {

    private lateinit var lineGb: LineChart
    private lateinit var lineVb: ArrayList<Entry>

    private lateinit var barGb: BarChart
    private lateinit var barElb: ArrayList<BarEntry>
    private lateinit var storeID: String
    private lateinit var storeName: String
    @RequiresApi(Build.VERSION_CODES.O)
    private val date = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
    private val order = FirebaseFirestore.getInstance().collection("orders")

    //lateinit var barD: BarData
    //lateinit var barDS: BarDataSet
    //lateinit var set1: LineDataSet

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_store_stat)


        storeID = intent.getStringExtra("storeId").toString()
        storeName = intent.getStringExtra("username").toString()

        val txtName:TextView = findViewById(R.id.txtvName)
        val txtId:TextView = findViewById(R.id.txtvStoreid)
        val txtInc:TextView = findViewById(R.id.txtvIncome)

        val btnqueue = findViewById<ImageButton>(R.id.btnSeeOrders)
        val btnprice = findViewById<ImageButton>(R.id.btnSetPrice)
        val btnlogout = findViewById<ImageButton>(R.id.btnStoreLogout)

        btnqueue.setOnClickListener {
            val intent = Intent(this, StoreQueueActivity::class.java).apply {
                putExtra("storeId", storeID)
                putExtra("username", storeName)
            }
            startActivity(intent)
            overridePendingTransition(R.anim.push_right_in, android.R.anim.slide_out_right);
        }

        btnprice.setOnClickListener {
            val intent = Intent(this, StorePricesActivity::class.java).apply {
                putExtra("storeId", storeID)
                putExtra("username", storeName)
            }
            startActivity(intent)
            overridePendingTransition(R.anim.push_right_in, android.R.anim.slide_out_right);
        }

        btnlogout.setOnClickListener {
            val dialog = Dialog(this)
            dialog.setContentView(R.layout.alert_dialog_layout)
            dialog.window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
            dialog.setCancelable(false)

            val positiveButton = dialog.findViewById<Button>(R.id.btn_okay)
            val negativeButton = dialog.findViewById<Button>(R.id.btn_cancel)
            positiveButton.setOnClickListener{
                val intent = Intent(this, LoginActivity::class.java);
                startActivity(intent)
                dialog.dismiss()
            }
            negativeButton.setOnClickListener{
                dialog.dismiss()
            }

            dialog.show()
            /*
            val builder = AlertDialog.Builder(this)
            builder.setMessage("Confirm logging out?")
                .setCancelable(false)
                .setPositiveButton("Yes"){dialog, id ->
                    val intent = Intent(this, LoginActivity::class.java)
                    startActivity(intent)
                }.setNegativeButton("No"){dialog, id->
                    dialog.dismiss()
                }
            builder.create().show()

             */
        }

        txtName.text = "Name: $storeName"
        txtId.text = "ID: $storeID"
        order.whereEqualTo("store", storeID)
            .whereEqualTo("status", "claimed")
            .get()
            .addOnSuccessListener { result->
                var sum = 0
                for(doc in result){
                    sum += doc.getString("totalPrice")!!.toInt()
                }
                txtInc.text = "Total Income: $sum"
            }


        ///////////////////////////////////////////////////////////////////////////////////////////////////
        lineGb = findViewById(R.id.idLineGraph)
        lineVb = ArrayList()

        barGb = findViewById<BarChart>(R.id.idBar)
        barElb = ArrayList()
        runInitial(lineGb, lineVb, barGb, barElb)


        /*
        for (i in barEl){
            Log.d("BarElOut", "${i.x} | ${i.y}")
        }
        if(barEl.isEmpty()) Log.d("BarElOut", "BarEl is Empty")

         */


        //createBarChart(barG, barEl, "Sales on $date")
        /*
        var hasRun = true
        while(barEl.isNotEmpty() && hasRun){
            createBarChart(barG, barEl, "Sales on $date")
            hasRun = false
        }

         */


        //createLineGrid(lineG, lineV, lineLabel)


/*
        lineV.add(Entry(0F, 50F))
        lineV.add(Entry(1F, 100F))
        lineV.add(Entry(2F, 200F))
        lineV.add(Entry(3F, 30F))
        val xlabel = arrayListOf<String>("Sunday","Monday","Tuesday","Wednesday")
        //lineV.add(Entry(4F, 120F))
        //lineV.add(Entry(5F, 100F))
        //lineV.add(Entry(6F, 10F))

        createLineGrid(lineG, lineV, xlabel)


        barEl.add(BarEntry(1f, 5f))
        barEl.add(BarEntry(2f, 2f))
        barEl.add(BarEntry(3f, 9f))

        createBarChart(barG, barEl, "Sales on $date")

        */
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun runInitial(lineG: LineChart, lineV: ArrayList<Entry>, barG: BarChart, barEl: ArrayList<BarEntry> ){
        //lineG = findViewById(R.id.idLineGraph)
        lineG.setTouchEnabled(true);
        lineG.setPinchZoom(false);
        //lineV = ArrayList()

        //barG = findViewById<BarChart>(R.id.idBar)
        barG.setTouchEnabled(true)
        barG.setPinchZoom(false);

        //barEl = ArrayList()

        /*
        barG = findViewById<BarChart>(R.id.idBar)
        barG.setTouchEnabled(true)
        barEl = ArrayList()

         */

        Log.d("DATE", "$date | $storeID | $storeName")

        var lineLabel: ArrayList<String> = ArrayList()
        order.whereEqualTo("store", storeID)
            .whereEqualTo("status", "claimed")
            .orderBy("date", Query.Direction.DESCENDING)
            .get()
            .addOnSuccessListener { result ->

                var temp = ""
                var dot = 0f
                var i = 0f
                for (doc in result) {
                    if (i != 7f) {
                        var idate = doc.getString("date").toString()
                        Log.d("FOR $i", "$idate | $temp | $dot")

                        if (temp.isEmpty()) {
                            temp = idate
                            lineLabel.add(temp)
                            dot++
                        } else if (temp == idate) {
                            dot++
                        } else if (temp != idate) {
                            lineV.add(Entry(i, dot))
                            lineLabel.add(idate)
                            temp = idate
                            dot = 1f
                            i++
                        }
                        Log.d("FOR AFTER $i", "$idate | $temp | $dot")
                    } else {
                        break
                    }
                }
                lineV.add(Entry(i, dot))
                Log.d("LineLabel", "$lineLabel")
                Log.d("lineV", "$lineV")
                createLineGrid(lineG, lineV, lineLabel)

            }.addOnCompleteListener { Log.d("LINE", "It works") }
            .addOnFailureListener { Log.d("LINE", "It DID NOT works") }

        order.whereEqualTo("store", storeID)
            .whereEqualTo("date", date)
            //.whereEqualTo("date", "2022-09-16")
            .get()
            .addOnSuccessListener { result ->
                var dclaim = 0f
                var dpend = 0f
                var dcan = 0f
                for (doc in result) {
                    val status = doc.getString("status").toString()
                    Log.d("STATUS", status)
                    when (status) {
                        "claimed" -> dclaim++
                        "pending" -> dpend++
                        "ready" -> dpend++
                        "cancel" -> dcan++
                    }
                }
                Log.d("FloatIN", "$dclaim | $dpend | $dcan")
                barEl.add(BarEntry(1f, dclaim))
                barEl.add(BarEntry(2f, dpend))
                barEl.add(BarEntry(3f, dcan))
                for (i in barEl) {
                    Log.d("BarEl", "${i.x} | ${i.y}")
                }


                createBarChart(barG, barEl, "Sales on $date")



            }.addOnCompleteListener { Log.d("BAR", "It works") }
            .addOnFailureListener { e -> Log.d("BAR", "It DID NOT works \n $e") }

    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun updateBarChart(barG: BarChart, barEl: ArrayList<BarEntry>, dates: String){
        barEl.clear()
        barG.invalidate()
        barG.clear()

        order.whereEqualTo("store", storeID)
            .whereEqualTo("date", dates)
            //.whereEqualTo("date", "2022-09-16")
            .get()
            .addOnSuccessListener { result ->
                var dclaim = 0f
                var dpend = 0f
                var dcan = 0f
                for (doc in result) {
                    val status = doc.getString("status").toString()
                    Log.d("STATUS", status)
                    when (status) {
                        "claimed" -> dclaim++
                        "pending" -> dpend++
                        "ready" -> dpend++
                        "cancel" -> dcan++
                    }
                }
                Log.d("FloatIN", "$dclaim | $dpend | $dcan")
                barEl.add(BarEntry(1f, dclaim))
                barEl.add(BarEntry(2f, dpend))
                barEl.add(BarEntry(3f, dcan))
                for (i in barEl) {
                    Log.d("BarEl", "${i.x} | ${i.y}")
                }
                createBarChart(barG, barEl, "Sales on $dates")
            }.addOnCompleteListener { Log.d("BAR", "It works") }
            .addOnFailureListener { e -> Log.d("BAR", "It DID NOT works \n $e") }
    }

    private fun createBarChart(chart: BarChart, array: ArrayList<BarEntry>, title: String){
        //val barDataSet: BarDataSet = BarDataSet(array, title)
        val barDataSet: BarDataSet


        if(chart.data != null &&
            chart.data.dataSetCount > 0){
            barDataSet = chart.data.getDataSetByIndex(0) as BarDataSet
            barDataSet.values = array
            chart.data.notifyDataChanged()
            chart.notifyDataSetChanged()

        }else {
            barDataSet = BarDataSet(array, title)
            barDataSet.valueTextColor = Color.DKGRAY
            barDataSet.setColors(Color.GREEN, Color.YELLOW, Color.RED)
            barDataSet.valueTextSize = 16f
            chart.description.isEnabled = false

            val barData: BarData = BarData(barDataSet)

            val xaxis: XAxis = chart.xAxis
            val xlabel = listOf<String>("", "Claimed", "Pending", "Canceled")
            xaxis.valueFormatter = IndexAxisValueFormatter(xlabel)

            chart.data = barData
        }
        chart.invalidate()
    }



    private fun createLineGrid(chart: LineChart, array: ArrayList<Entry>, label: ArrayList<String>){
        //https://medium.com/@leelaprasad4648/creating-linechart-using-mpandroidchart-33632324886d
        var set1: LineDataSet
            if(chart.data != null &&
                chart.data.dataSetCount > 0){
                set1 = chart.data.getDataSetByIndex(0) as LineDataSet
                set1.values = array
                chart.data.notifyDataChanged()
                chart.notifyDataSetChanged()
            }else{
                set1 = LineDataSet(array, "Sales")
                set1.setDrawIcons(false)
                set1.enableDashedLine(10f,5f, 0f)
                set1.enableDashedHighlightLine(10f,5f,0f)
                set1.color = Color.DKGRAY
                set1.setCircleColor(Color.DKGRAY)
                set1.lineWidth = 1f
                set1.circleRadius = 3f
                set1.valueTextSize = 9f
                set1.setDrawFilled(true)
                set1.formLineWidth = 1f
                val arr1 = floatArrayOf(10f,5f)
                set1.formLineDashEffect = DashPathEffect(arr1, 0f)
                set1.formSize = 15f
                set1.fillColor = Color.BLUE
    /*
                if(Utils.getSDKInt() >= 18){
                    val drawable: Drawable = ContextCompat.getDrawable(this, R.drawable.)
                }
     */

                //val xlabel = listOf<String>("Sunday","Monday","Tuesday","Wednesday","Thursday","Friday","Saturday")
                val xaxis: XAxis = chart.xAxis
                xaxis.valueFormatter = IndexAxisValueFormatter(label)


                val dataSets: ArrayList<ILineDataSet> = ArrayList()
                dataSets.add(set1)
                val data: LineData = LineData(dataSets)

                chart.description.isEnabled = false
                chart.data = data;
        }
        chart.setOnChartValueSelectedListener(object: OnChartValueSelectedListener{
            @RequiresApi(Build.VERSION_CODES.O)
            override fun onValueSelected(e: Entry?, h: Highlight?) {
                val datelabel = label.get(e!!.x.toInt())
                updateBarChart(barGb, barElb, datelabel)
            }

            override fun onNothingSelected() {
            }
        })
        chart.invalidate()
    }

}