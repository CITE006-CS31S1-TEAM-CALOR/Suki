package com.example.pabili


import android.widget.Toast
import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.DisplayMetrics
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.google.mlkit.vision.barcode.BarcodeScanner

import com.google.mlkit.vision.barcode.BarcodeScannerOptions
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.common.InputImage
import java.util.concurrent.Executors
import kotlin.IllegalStateException
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min

import android.content.Intent
import com.google.firebase.firestore.FirebaseFirestore

class QRScannerActivity : AppCompatActivity() {

    private var previewView: PreviewView? = null
    private var cameraProvider: ProcessCameraProvider? = null
    private var cameraSelector: CameraSelector? = null
    private var lensFacing = CameraSelector.LENS_FACING_BACK
    private var previewUseCase: Preview? = null
    private var analysisUseCase: ImageAnalysis? = null
    private var result:String = ""
        
    private val screenAspectRatio: Int
     get() {
         // Get screen metrics used to setup camera for full screen resolution
         val metrics = DisplayMetrics().also { previewView?.display?.getRealMetrics(it) }
         return aspectRatio(metrics.widthPixels, metrics.heightPixels)
     }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_qrscanner)
        previewView = findViewById(R.id.preview_view)
        if (previewView!=null){     
	        setupCamera()
        }
    }

    private fun setupCamera() {
        cameraSelector = CameraSelector.Builder().requireLensFacing(lensFacing).build()
        ViewModelProvider(
            this, ViewModelProvider.AndroidViewModelFactory.getInstance(application)
        ).get(CameraXViewModel::class.java)
            .processCameraProvider
            .observe(this) { provider: ProcessCameraProvider? ->
                    cameraProvider = provider
                    if (isCameraPermissionGranted()) {
                        bindCameraUseCases()
                    } else {
                        ActivityCompat.requestPermissions(
                            this,
                            arrayOf(Manifest.permission.CAMERA),
                            PERMISSION_CAMERA_REQUEST
                        )
                    }
                }
    }

    private fun bindCameraUseCases() {
        bindPreviewUseCase()
        bindAnalyseUseCase()
    }

    private fun bindPreviewUseCase() {
        if (cameraProvider == null) {
            return
        }
        if (previewUseCase != null) {
            cameraProvider!!.unbind(previewUseCase)
        }

		
				
		     previewUseCase = Preview.Builder()
		         .setTargetAspectRatio(screenAspectRatio)
		         .setTargetRotation(android.view.Surface.ROTATION_0)
		         .build()
		         //previewView!!.display.rotation
		         
		     previewUseCase!!.setSurfaceProvider(previewView!!.surfaceProvider)

        try {
            cameraProvider!!.bindToLifecycle(
                /* lifecycleOwner= */this,
                cameraSelector!!,
                previewUseCase
            )
        } catch (illegalStateException: IllegalStateException) {
            Log.e(TAG, illegalStateException.message ?: "IllegalStateException")
        } catch (illegalArgumentException: IllegalArgumentException) {
            Log.e(TAG, illegalArgumentException.message ?: "IllegalArgumentException")
        }
    }

    private fun bindAnalyseUseCase() {
        // Note that if you know which format of barcode your app is dealing with, detection will be
        // faster to specify the supported barcode formats one by one, e.g.
        //BarcodeScannerOptions.Builder()
        //     .setBarcodeFormats(Barcode.FORMAT_QR_CODE)
        //     .build();
        
        val barcodeScanner: BarcodeScanner = BarcodeScanning.getClient()

        if (cameraProvider == null) {
            return
        }
        if (analysisUseCase != null) {
            cameraProvider!!.unbind(analysisUseCase)
        }

        analysisUseCase = ImageAnalysis.Builder()
            .setTargetAspectRatio(screenAspectRatio)
            .setTargetRotation(android.view.Surface.ROTATION_0)
            .build()

        // Initialize our background executor 
        val cameraExecutor = Executors.newSingleThreadExecutor()

        analysisUseCase?.setAnalyzer(
            cameraExecutor,
            ImageAnalysis.Analyzer { imageProxy ->
                processImageProxy(barcodeScanner, imageProxy)
            }
        )

        try {
            cameraProvider!!.bindToLifecycle(
                /* lifecycleOwner= */this,
                cameraSelector!!,
                analysisUseCase
            )
        } catch (illegalStateException: IllegalStateException) {
            Log.e(TAG, illegalStateException.message ?: "IllegalStateException")
        } catch (illegalArgumentException: IllegalArgumentException) {
            Log.e(TAG, illegalArgumentException.message ?: "IllegalArgumentException")
        }
    }

    @SuppressLint("UnsafeExperimentalUsageError")
    private fun processImageProxy(
        barcodeScanner: BarcodeScanner,
        imageProxy: ImageProxy
    ) {
        var inputImage =
            InputImage.fromMediaImage(imageProxy.image!!, imageProxy.imageInfo.rotationDegrees)

        barcodeScanner.process(inputImage)
            .addOnSuccessListener { barcodes ->
                barcodes.forEach {
                    if (it.rawValue!!.toString()!="" && it!!.rawValue!=null){
                        if (result != it.rawValue.toString()){
                            Toast.makeText(this@QRScannerActivity, it.rawValue.toString(), Toast.LENGTH_SHORT).show()
                            result = it.rawValue.toString()
                        
		                     val cDoc = intent.getStringExtra("cDoc")
					            val db = FirebaseFirestore.getInstance()
					            val cus = db.collection("orders").document(cDoc!!)

		                     cus.get().addOnSuccessListener { data ->
		                         val cusTransId = data.getString("transactionID")
		                         if(cusTransId == result){
		                             cus.update("status","claimed").addOnSuccessListener {
		                                 Toast.makeText(baseContext, "Customer Status updated to CLAIM", Toast.LENGTH_SHORT).show()
		                                 val intent = Intent(baseContext, StoreQueueActivity::class.java).apply {
		                                     //putExtra("storename", barcode.rawValue.toString())
		                                     putExtra("username", LOGIN_NAME)
		                                     putExtra("storeId",LOGIN_ID)
		                                 }
		                                 startActivity(intent)
		                                 finish()
		                             }.addOnFailureListener{ e ->
		                                 Toast.makeText(baseContext,"There was an error on updating. Check your Network", Toast.LENGTH_SHORT).show()
		                                 Log.d("ERROR", e.toString())
		                             }
		                         }else{
		                             Toast.makeText(baseContext, "Code didn't matched",Toast.LENGTH_SHORT).show()
		                         }
		                     }.addOnFailureListener{ e ->
		                         Toast.makeText(baseContext, "There was a connection error. Check your Network", Toast.LENGTH_SHORT).show()
		                         Log.d("ERROR", e.toString())
		                     }
                        
                        }
                        

                    
                    
                    }
                }
            }
            .addOnFailureListener {
                Log.e(TAG, it.message ?: it.toString())
            }.addOnCompleteListener {
                // When the image is from CameraX analysis use case, must call image.close() on received
                // images when finished using them. Otherwise, new images may not be received or the camera
                // may stall.
                imageProxy.close()
            }
    }

    /**
     *  [androidx.camera.core.ImageAnalysis], [androidx.camera.core.Preview] requires enum value of
     *  [androidx.camera.core.AspectRatio]. Currently it has values of 4:3 & 16:9.
     *
     *  Detecting the most suitable ratio for dimensions provided in @params by counting absolute
     *  of preview ratio to one of the provided values.
     *
     *  @param width - preview width
     *  @param height - preview height
     *  @return suitable aspect ratio
     */
    private fun aspectRatio(width: Int, height: Int): Int {
        val previewRatio = max(width, height).toDouble() / min(width, height)
        if (abs(previewRatio - RATIO_4_3_VALUE) <= abs(previewRatio - RATIO_16_9_VALUE)) {
            return AspectRatio.RATIO_4_3
        }
        return AspectRatio.RATIO_16_9
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        if (requestCode == PERMISSION_CAMERA_REQUEST) {
            if (isCameraPermissionGranted()) {
                bindCameraUseCases()
            } else {
                Log.e(TAG, "no camera permission")
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    private fun isCameraPermissionGranted(): Boolean {
        return ContextCompat.checkSelfPermission(
            baseContext,
            Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED
    }

    companion object {
        private val TAG = QRScannerActivity::class.java.simpleName
        private const val PERMISSION_CAMERA_REQUEST = 1

        private const val RATIO_4_3_VALUE = 4.0 / 3.0
        private const val RATIO_16_9_VALUE = 16.0 / 9.0
    }
}
