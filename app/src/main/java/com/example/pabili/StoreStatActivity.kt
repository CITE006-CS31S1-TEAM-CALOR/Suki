package com.example.pabili

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineDataSet

class StoreStatActivity : AppCompatActivity() {

    lateinit var lineG: LineChart
    lateinit var lineV: ArrayList<Entry>
    lateinit var set1: LineDataSet

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_store_stat)

        lineG = findViewById(R.id.idLineGraph)
        lineG.setTouchEnabled(true);
        lineG.setPinchZoom(true);

        lineV = ArrayList()
        lineV.add(Entry(1F, 50F))
        lineV.add(Entry(2F, 100F))
        lineV.add(Entry(3F, 200F))
        lineV.add(Entry(4F, 30F))
        lineV.add(Entry(5F, 120F))

        if(lineG.data != null &&
        lineG.data.dataSetCount > 0){}



    }
}