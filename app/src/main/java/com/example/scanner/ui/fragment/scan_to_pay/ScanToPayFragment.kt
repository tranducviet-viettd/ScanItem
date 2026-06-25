package com.example.scanner.ui.fragment.scan_to_pay

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
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResult
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.scanner.R
import com.example.scanner.databinding.FragmentListItemsBinding
import com.example.scanner.databinding.FragmentScanBinding
import com.example.scanner.databinding.FragmentScanToPayBinding
import com.example.scanner.databinding.FragmentShowInfoItemBinding
import com.example.scanner.ui.fragment.list_items.ItemsViewModel
import com.example.scanner.ui.fragment.list_items.ItemsViewModelFactory
import com.example.scanner.util.fragmentDestinationFlag
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.common.InputImage
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
//import kotlin.getValue

class ScanToPayFragment : Fragment(){

    private val viewModel : ScanToPayViewModel by viewModels { ScanToPayViewModelFactory() }

    private lateinit var viewDataBinding : FragmentScanToPayBinding


    private lateinit var cameraExecutor: ExecutorService
    private var isScanning = false

    private var currentBarcode: String = ""
    private var emptyFrameStartTime: Long = 0L

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
        viewDataBinding = FragmentScanToPayBinding.inflate(inflater, container, false).apply { viewmodel = viewModel }
        viewDataBinding.lifecycleOwner=this.viewLifecycleOwner
        return viewDataBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        checkCameraPermission()
        viewModel.boxEditText.observe(viewLifecycleOwner) { viewModel.updateMoneys() }
        viewModel.packEditText.observe(viewLifecycleOwner) { viewModel.updateMoneys() }
        viewModel.pieceEditText.observe(viewLifecycleOwner) { viewModel.updateMoneys() }
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

        val cameraProviderFuture = ProcessCameraProvider.getInstance(requireContext())
        cameraProviderFuture.addListener({
            val cameraProvider = cameraProviderFuture.get()

            // Cấu hình Preview
            val preview = Preview.Builder().build().also {
                it.setSurfaceProvider(viewDataBinding.previewView.surfaceProvider)
            }

            // Cấu hình ImageAnalysis cho ML Kit
            val imageAnalyzer = ImageAnalysis.Builder().build().also { analysis ->
                analysis.setAnalyzer(cameraExecutor) { imageProxy ->
                    val mediaImage = imageProxy.image
                    if (mediaImage != null) {
                        val image = InputImage.fromMediaImage(mediaImage, imageProxy.imageInfo.rotationDegrees)
                        val scanner = BarcodeScanning.getClient()
                        scanner.process(image)
                            .addOnSuccessListener { barcodes ->
                                if (barcodes.isNotEmpty()) {
                                    val barcode = barcodes[0] // Không lấy rawValue vội, lấy nguyên object Barcode

                                    // BƯỚC 1: XÁC ĐỊNH VỊ TRÍ CỦA MÃ VẠCH (ÉP KHUÔN 300x200)
                                    val box = barcode.boundingBox
                                    var isInsideFrame = false

                                    if (box != null) {
                                        val centerX = box.centerX()
                                        val centerY = box.centerY()
                                        val imgW = imageProxy.width
                                        val imgH = imageProxy.height

                                        val isPortrait = imgW > imgH
                                        val verticalAxisLen = if (isPortrait) imgW else imgH
                                        val horizontalAxisLen = if (isPortrait) imgH else imgW

                                        val barcodeVerticalCenter = if (isPortrait) centerX else centerY
                                        val barcodeHorizontalCenter = if (isPortrait) centerY else centerX

                                        val minVertical = verticalAxisLen * 0.35
                                        val maxVertical = verticalAxisLen * 0.65
                                        val minHorizontal = horizontalAxisLen * 0.10
                                        val maxHorizontal = horizontalAxisLen * 0.90

                                        if (barcodeVerticalCenter > minVertical && barcodeVerticalCenter < maxVertical &&
                                            barcodeHorizontalCenter > minHorizontal && barcodeHorizontalCenter < maxHorizontal) {
                                            isInsideFrame = true
                                        }
                                    }

                                    // BƯỚC 2: NẾU NẰM TRONG KHUNG THÌ MỚI XỬ LÝ
                                    if (isInsideFrame) {
                                        emptyFrameStartTime = 0L // Hủy bấm giờ

                                        val barcodeValue = barcode.rawValue
                                        if (barcodeValue != null && barcodeValue != currentBarcode) {
                                            currentBarcode = barcodeValue // Khóa lại

                                            activity?.runOnUiThread {
                                                viewModel.loadInfoItem(barcodeValue)
                                            }
                                        }
                                    } else {
                                        // Nằm ngoài khung thì coi như không thấy (Mở khóa nếu người dùng vừa rê mã vạch ra ngoài)
                                        currentBarcode = ""
                                    }
                                } else {
                                    // Không thấy mã vạch nào -> Bắt đầu bấm giờ chống rung
                                    if (emptyFrameStartTime == 0L) {
                                        emptyFrameStartTime = System.currentTimeMillis()
                                    } else {
                                        if (System.currentTimeMillis() - emptyFrameStartTime > 1000L) {
                                            currentBarcode = ""
                                            emptyFrameStartTime = 0L
                                        }
                                    }
                                }
                                imageProxy.close()
                            }
                            .addOnFailureListener { e ->
                                Log.e("ScanFragment", "Quét thất bại: ${e.message}")
                                imageProxy.close()
                            }
                    } else {
                        imageProxy.close()
                    }
                }
            }

            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA
            try {
                cameraProvider.unbindAll()
                cameraProvider.bindToLifecycle(this, cameraSelector, preview, imageAnalyzer)
            } catch (e: Exception) {
                Log.e("ScanFragment", "Không thể mở camera: ${e.message}")
                Toast.makeText(requireContext(), "Lỗi mở camera", Toast.LENGTH_SHORT).show()
                findNavController().popBackStack()
            }
        }, ContextCompat.getMainExecutor(requireContext()))
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
                findNavController().popBackStack()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        cameraExecutor.shutdown()

    }

}