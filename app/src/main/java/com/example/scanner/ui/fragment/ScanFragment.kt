package com.example.scanner.ui.fragment

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.OptIn
import androidx.camera.core.CameraSelector
import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.concurrent.futures.await
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResult
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.scanner.R
import com.example.scanner.databinding.FragmentScanBinding
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.common.InputImage
import kotlinx.coroutines.launch
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class ScanFragment : Fragment() {

    private var _binding: FragmentScanBinding? = null
    private val binding get() = _binding!!

    private lateinit var cameraExecutor: ExecutorService
    private var isScanning = false

    companion object {
        private const val CAMERA_PERMISSION_CODE = 101
        const val SCAN_RESULT_KEY = "SCAN_RESULT"
        const val BARCODE_KEY = "BARCODE"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        cameraExecutor = Executors.newSingleThreadExecutor()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentScanBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.cancelButton.setOnClickListener {
            findNavController().navigate(R.id.action_scanFragment_to_TestFragment)
        }

        checkCameraPermission()
    }

    private fun checkCameraPermission() {
        if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            startCamera()
        } else {
            requestPermissions(arrayOf(Manifest.permission.CAMERA), CAMERA_PERMISSION_CODE)
        }
    }

    @OptIn(ExperimentalGetImage::class)
    private fun startCamera() {
        if (isScanning) return
        isScanning = true

        lifecycleScope.launch {
            try {
                val cameraProvider = ProcessCameraProvider.getInstance(requireContext()).await()

                // Preview
                val preview = Preview.Builder().build().also {
                    it.setSurfaceProvider(binding.previewView.surfaceProvider)
                }

                // Image Analysis cho Barcode
                val imageAnalyzer = ImageAnalysis.Builder()
                    .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                    .build()
                    .also { analysis ->
                        analysis.setAnalyzer(cameraExecutor) { imageProxy ->
                            processBarcode(imageProxy)
                        }
                    }

                val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

                cameraProvider.unbindAll()
                cameraProvider.bindToLifecycle(
                    this@ScanFragment,
                    cameraSelector,
                    preview,
                    imageAnalyzer
                )

            } catch (e: Exception) {
                Log.e("ScanFragment", "Lỗi khởi động camera", e)
                Toast.makeText(requireContext(), "Không thể mở camera", Toast.LENGTH_SHORT).show()
                findNavController().navigate(R.id.action_scanFragment_to_TestFragment)
            }
        }
    }

    @OptIn(ExperimentalGetImage::class)
    private fun processBarcode(imageProxy: androidx.camera.core.ImageProxy) {
        val mediaImage = imageProxy.image
        if (mediaImage != null) {
            val image = InputImage.fromMediaImage(
                mediaImage,
                imageProxy.imageInfo.rotationDegrees
            )

            val scanner = BarcodeScanning.getClient()
            scanner.process(image)
                .addOnSuccessListener { barcodes ->
                    if (barcodes.isNotEmpty() && !isScanning) {  // Tránh quét nhiều lần
                        val barcode = barcodes[0].rawValue
                        if (barcode != null) {
                            isScanning = true
                            setFragmentResult(
                                SCAN_RESULT_KEY,
                                Bundle().apply { putString(BARCODE_KEY, barcode) }
                            )
                            findNavController().navigate(R.id.action_scanFragment_to_TestFragment)
                        }
                    }
                    imageProxy.close()
                }
                .addOnFailureListener { e ->
                    Log.e("ScanFragment", "Quét barcode thất bại: ${e.message}")
                    imageProxy.close()
                }
        } else {
            imageProxy.close()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == CAMERA_PERMISSION_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startCamera()
            } else {
                Toast.makeText(requireContext(), "Cần quyền camera để quét mã", Toast.LENGTH_LONG).show()
                findNavController().navigate(R.id.action_scanFragment_to_TestFragment)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        cameraExecutor.shutdown()
        _binding = null
    }
}