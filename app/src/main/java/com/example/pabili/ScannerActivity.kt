package com.example.pabili

import android.Manifest
import android.content.ContentValues
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.ImageCapture
import androidx.camera.video.Recorder
import androidx.camera.video.Recording
import androidx.camera.video.VideoCapture
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.pabili.databinding.ActivityScannerBinding
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import android.widget.Toast
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.core.Preview
import androidx.camera.core.CameraSelector
import android.util.Log
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.ImageProxy
import androidx.camera.video.FallbackStrategy
import androidx.camera.video.MediaStoreOutputOptions
import androidx.camera.video.Quality
import androidx.camera.video.QualitySelector
import androidx.camera.video.VideoRecordEvent
import androidx.core.content.PermissionChecker
import java.nio.ByteBuffer
import java.text.SimpleDateFormat
import java.util.Locale
import androidx.databinding.DataBindingUtil

import com.google.mlkit.vision.barcode.BarcodeScannerOptions
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.common.InputImage


import android.content.Intent
import com.google.firebase.firestore.FirebaseFirestore

typealias LumaListener = (luma: Double) -> Unit


/* 
override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_qrscanner)
 */
class ScannerActivity : AppCompatActivity() {
   private lateinit var viewBinding: ActivityScannerBinding

   private var imageCapture: ImageCapture? = null

   private var videoCapture: VideoCapture<Recorder>? = null
   private var recording: Recording? = null

   private lateinit var cameraExecutor: ExecutorService

   override fun onCreate(savedInstanceState: Bundle?) {
       super.onCreate(savedInstanceState)
       viewBinding = ActivityScannerBinding.inflate(layoutInflater)
       setContentView(viewBinding.root)

       // Request camera permissions
       if (allPermissionsGranted()) {
           startCamera()
       } else {
           ActivityCompat.requestPermissions(
               this, REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS)
       }

       // Set up the listeners for take photo and video capture buttons
       viewBinding.imageCaptureButton.setOnClickListener { takePhoto() }

       cameraExecutor = Executors.newSingleThreadExecutor()
   }

  private fun startCamera() {

   val cameraProviderFuture = ProcessCameraProvider.getInstance(this)

   cameraProviderFuture.addListener({
       // Used to bind the lifecycle of cameras to the lifecycle owner
       val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()

       // Preview
       val preview = Preview.Builder()
          .build()
          .also {
              it.setSurfaceProvider(viewBinding.viewFinder.surfaceProvider)
          }

       // Select back camera as a default
       val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

        //set Image Capture Builder
        imageCapture = ImageCapture.Builder()
        .setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
        .build()

       try {
           // Unbind use cases before rebinding
           cameraProvider.unbindAll()

           // Bind use cases to camera
           cameraProvider.bindToLifecycle(
               this, cameraSelector, preview,imageCapture)

       } catch(exc: Exception) {
           Log.e(TAG, "Use case binding failed", exc)
       }

   }, ContextCompat.getMainExecutor(this))


   }

   private fun takePhoto() {
  // Get a stable reference of the modifiable image capture use case
   
   val imageCapture = imageCapture?: return
   // Create time stamped name and MediaStore entry.
   val name = SimpleDateFormat(FILENAME_FORMAT, Locale.US)
              .format(System.currentTimeMillis())
   val contentValues = ContentValues().apply {
       put(MediaStore.MediaColumns.DISPLAY_NAME, name)
       put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg")
       if(Build.VERSION.SDK_INT > Build.VERSION_CODES.P) {
           put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/CameraX-Image")
       }
   }

   // Create output options object which contains file + metadata
   val outputOptions = ImageCapture.OutputFileOptions
           .Builder(contentResolver,
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                    contentValues)
           .build()

   // Set up image capture listener, which is triggered after photo has
   // been taken
   imageCapture.takePicture(
       outputOptions,
       ContextCompat.getMainExecutor(this),
       object : ImageCapture.OnImageSavedCallback {
           override fun onError(exc: ImageCaptureException) {
               
               Log.e(TAG, "Photo capture failed: ${exc.message}", exc)
           }

           override fun onImageSaved(
            output: ImageCapture.OutputFileResults){
               val msg = "Photo capture succeeded: ${output.savedUri}"

               var image = InputImage.fromFilePath(baseContext, output.savedUri?:return)
               scanBarcodes(image)
   //          Toast.makeText(baseContext,output.savedU,Toast.LENGTH_SHORT).show()
            
//               Toast.makeText(baseContext, msg, Toast.LENGTH_SHORT).show()
               Log.d(TAG, msg)
           }
       }
   )

   }

   private fun captureVideo() {}


   private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
       ContextCompat.checkSelfPermission(
           baseContext, it) == PackageManager.PERMISSION_GRANTED
   }

   override fun onDestroy() {
       super.onDestroy()
       cameraExecutor.shutdown()
   }

   companion object {
       private const val TAG = "CameraXApp"
       private const val FILENAME_FORMAT = "yyyy-MM-dd-HH-mm-ss-SSS"
       private const val REQUEST_CODE_PERMISSIONS = 10
       private val REQUIRED_PERMISSIONS =
           mutableListOf (
               Manifest.permission.CAMERA,
               Manifest.permission.RECORD_AUDIO
           ).apply {
               if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.P) {
                   add(Manifest.permission.WRITE_EXTERNAL_STORAGE)
               }
           }.toTypedArray()
   }

   override fun onRequestPermissionsResult(
   requestCode: Int, permissions: Array<String>, grantResults:IntArray) {
       super.onRequestPermissionsResult(requestCode, permissions, grantResults)
       if (requestCode == REQUEST_CODE_PERMISSIONS) {
        if (allPermissionsGranted()) {
            startCamera()
        } else {
            Toast.makeText(this,
                "Permissions not granted by the user.",
                Toast.LENGTH_SHORT).show()
            finish()
        }
    }
    }

      private fun scanBarcodes(image: InputImage) {

//                        Toast.makeText(baseContext,"Rhandy",Toast.LENGTH_SHORT).show()
        // [START set_detector_options]
        val options = BarcodeScannerOptions.Builder()
                .setBarcodeFormats(
                        Barcode.FORMAT_QR_CODE,
                        Barcode.FORMAT_AZTEC)
                .build()
        // [END set_detector_options]

        // [START get_detector]
        val scanner = BarcodeScanning.getClient()
        // Or, to specify the formats to recognize:
        // val scanner = BarcodeScanning.getClient(options)
        // [END get_detector]

        // [START run_detector]
        val result = scanner.process(image)
                .addOnSuccessListener { barcodes ->
                    // Task completed successfully
                    // [START_EXCLUDE]
                    // [START get_barcodes]
                    val cDoc = intent.getStringExtra("cDoc")
                    val db = FirebaseFirestore.getInstance()
                    val cus = db.collection("orders").document(cDoc!!)

                    for (barcode in barcodes) {
                        val bounds = barcode.boundingBox
                        val corners = barcode.cornerPoints

                        val rawValue = barcode.rawValue

                        val valueType = barcode.valueType
                        // See API reference for complete list of supported types
                        when (valueType) {
                            Barcode.TYPE_WIFI -> {
                                val ssid = barcode.wifi!!.ssid
                                val password = barcode.wifi!!.password
                                val type = barcode.wifi!!.encryptionType
                            }
                            Barcode.TYPE_URL -> {
                                val title = barcode.url!!.title
                                val url = barcode.url!!.url
                            }
                        }
                        Toast.makeText(baseContext,barcode.rawValue.toString(),Toast.LENGTH_SHORT).show()

                        cus.get().addOnSuccessListener { result ->
                            val cusTransId = result.getString("transactionID")
                            if(cusTransId == barcode.rawValue.toString()){
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
                                Log.d("MISMATCH", "$cusTransId != ${barcode.rawValue.toString()}")
                            }
                        }.addOnFailureListener{ e ->
                            Toast.makeText(baseContext, "There was a connection error. Check your Network", Toast.LENGTH_SHORT).show()
                            Log.d("ERROR", e.toString())
                        }
                    }
                    // [END get_barcodes]
                    // [END_EXCLUDE]
                }
                .addOnFailureListener {
                    Toast.makeText(baseContext,"Please Try Again",Toast.LENGTH_SHORT).show()
                        
                }
        // [END run_detector]
    }
}