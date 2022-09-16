package com.example.pabili

import android.graphics.Color
import android.graphics.DashPathEffect
import android.graphics.drawable.Drawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.core.content.ContextCompat
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.*
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.github.mikephil.charting.formatter.ValueFormatter
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet
import com.github.mikephil.charting.utils.Utils
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class StoreStatActivity : AppCompatActivity() {

    private lateinit var lineG: LineChart
    private lateinit var lineV: ArrayList<Entry>

    private lateinit var barG: BarChart
    //lateinit var barD: BarData
    //lateinit var barDS: BarDataSet
    private lateinit var barEl: ArrayList<BarEntry>
    //lateinit var set1: LineDataSet

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_store_stat)

        val storeID = intent.getStringExtra("storeId")
        val storeName = intent.getStringExtra("username")
        val db = FirebaseFirestore.getInstance()
        val order = db.collection("orders")
        val date = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))



        lineG = findViewById(R.id.idLineGraph)
        lineG.setTouchEnabled(true);
        lineG.setPinchZoom(false);
        lineV = ArrayList()

        barG = findViewById<BarChart>(R.id.idBar)
        barG.setTouchEnabled(true)
        barEl = ArrayList()

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
                for(doc in result){
                    if(i != 7f){
                        var idate = doc.getString("date").toString()
                        if(temp.isEmpty()){
                            temp = idate
                            lineLabel.add(temp)
                            dot++
                        }else if(temp == idate){
                            dot++
                        }else if(temp != idate){
                            lineV.add(Entry(i, dot))
                            lineLabel.add(idate)
                            temp = idate
                            dot = 0f
                            i++
                        }
                    }else{
                        break
                    }
                }

            }.addOnCompleteListener { Log.d("LINE", "It works") }
            .addOnFailureListener { Log.d("LINE", "It DID NOT works") }

        order.whereEqualTo("store", storeID)
            .whereEqualTo("date", date)
            .get()
            .addOnSuccessListener { result ->
                var dclaim = 0f
                var dpend = 0f
                var dcan = 0f
                for(doc in result){
                    val status = doc.getString("status").toString()
                    Log.d("STATUS", status)
                    when(status){
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
                for (i in barEl){
                    Log.d("BarEl", "${i.x} | ${i.y}")
                }

            }.addOnCompleteListener { Log.d("BAR", "It works") }
            .addOnFailureListener { e -> Log.d("BAR", "It DID NOT works \n $e") }


        createBarChart(barG, barEl, "Sales on $date")

        createLineGrid(lineG, lineV, lineLabel)





       /*
        lineV.add(Entry(0F, 50F))
        lineV.add(Entry(1F, 100F))
        lineV.add(Entry(2F, 200F))
        lineV.add(Entry(3F, 30F))
        lineV.add(Entry(4F, 120F))
        lineV.add(Entry(5F, 100F))
        lineV.add(Entry(6F, 10F))

        createLineGrid(lineG, lineV)


        barEl.add(BarEntry(1f, 5f))
        barEl.add(BarEntry(2f, 2f))
        barEl.add(BarEntry(3f, 9f))

        createBarChart(barG, barEl, "Sales on $date")

        */

    }

    private fun createBarChart(chart: BarChart, array: ArrayList<BarEntry>, title: String){
        val barDataSet: BarDataSet = BarDataSet(array, title)
        val barData: BarData = BarData(barDataSet)
        barDataSet.valueTextColor = Color.DKGRAY
        barDataSet.setColors(Color.GREEN,Color.YELLOW,Color.RED)
        barDataSet.valueTextSize = 16f
        chart.description.isEnabled = false

        val xaxis: XAxis = chart.xAxis
        val xlabel = listOf<String>("","Claimed", "Pending", "Canceled")
        xaxis.valueFormatter = IndexAxisValueFormatter(xlabel)

        chart.data = barData
    }


    private fun createLineGrid(chart: LineChart, array: ArrayList<Entry>, label: ArrayList<String>){
        var set1: LineDataSet
            if(chart.data != null &&
                chart.data.dataSetCount > 0){
                set1 = chart.data.getDataSetByIndex(0) as LineDataSet
                set1.values = lineV
                chart.data.notifyDataChanged()
                chart.notifyDataSetChanged();
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
    /*
                if(Utils.getSDKInt() >= 18){
                    val drawable: Drawable = ContextCompat.getDrawable(this, R.drawable.)
                }
     */

                //val xlabel = listOf<String>("Sunday","Monday","Tuesday","Wednesday","Thursday","Friday","Saturday")
                val xaxis: XAxis = chart.xAxis
                xaxis.valueFormatter = IndexAxisValueFormatter(label)
                set1.fillColor = Color.BLUE
                val dataSets: ArrayList<ILineDataSet> = ArrayList()
                dataSets.add(set1)
                val data: LineData = LineData(dataSets)

                chart.description.isEnabled = false
                chart.data = data;
        }
    }

}