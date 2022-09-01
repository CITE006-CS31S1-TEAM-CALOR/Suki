package com.example.pabili

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.firebase.firestore.FirebaseFirestore
import android.widget.TextView
import android.widget.Toast
import android.widget.Button
import android.widget.EditText
import android.content.Intent
import android.view.View



import android.app.Activity
import android.content.Context
import android.content.Context.CAMERA_SERVICE
import android.graphics.Bitmap
import android.hardware.camera2.CameraAccessException
import android.hardware.camera2.CameraCharacteristics
import android.hardware.camera2.CameraManager
import android.media.Image
import android.net.Uri
import android.os.Build
import android.util.SparseIntArray
import android.view.Surface
import androidx.annotation.RequiresApi
import com.google.mlkit.vision.common.InputImage
import java.io.IOException
import java.nio.ByteBuffer


class ScannerActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_qrscanner)
     	

	
    }
}

