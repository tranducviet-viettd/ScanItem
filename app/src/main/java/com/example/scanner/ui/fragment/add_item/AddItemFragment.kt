package com.example.scanner.ui.fragment.add_item

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.LifecycleObserver
import com.example.scanner.databinding.FragmentAddItemBinding
import java.io.File
import java.io.FileOutputStream
import com.example.scanner.data.db.Result
import androidx.appcompat.app.AlertDialog

class AddItemFragment : Fragment() {

    private lateinit var viewDataBinding: FragmentAddItemBinding

    private val viewModel: AddItemViewModel by viewModels {
        AddItemViewModelFactory(requireContext()
        )}

    private val imagePickerLauncher =registerForActivityResult(ActivityResultContracts.GetContent()){ uri ->
        uri?.let { viewModel.sendImageToCloudinary(it) }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewDataBinding= FragmentAddItemBinding.inflate(inflater,container,false).apply { viewmodel = viewModel }
        viewDataBinding.lifecycleOwner = this.viewLifecycleOwner

        return viewDataBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

    override fun onStart() {
        super.onStart()
    }

    override fun onResume() {
        super.onResume()
    }

    override fun onStop() {
        super.onStop()
    }

    override fun onDestroyView() {
        super.onDestroyView()
    }

    override fun onDestroy() {
        super.onDestroy()
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        setupItemImagePicker()
        setupAddItem()
        setupObserver()
    }

    private fun setupItemImagePicker(){
        viewModel.itemImageUrl.observe(viewLifecycleOwner) { url ->
            Log.d("AddItem", "LiveData thay đổi: $url")
        }
         viewDataBinding.cameraBtn.setOnClickListener {
             checkAndRequestCameraPermission()
         }
    }
    private fun setupAddItem(){
        viewDataBinding.addItemBtn.setOnClickListener {
            viewModel.addItem()
        }
    }
    private fun setupObserver() {
        viewModel.addItemResult.observe(viewLifecycleOwner) { result ->
            when (result) {
                is Result.Success -> {
                    showSuccessDialog()
                }

                is Result.Error -> {
                    showErrorDialog(result.msg ?: "Thêm item thất bại")
                }

                is Result.Loading -> {
                    // Có thể show ProgressBar hoặc disable button
                    viewDataBinding.addItemBtn.isEnabled = false
                }

            }
        }
    }
    private fun showSuccessDialog() {
        AlertDialog.Builder(requireContext())
            .setTitle("THÀNH CÔNG")
            .setMessage("Thêm mặt hàng mới THÀNH CÔNG")
            .setPositiveButton("OK") { dialog, _ ->
            }
            .setCancelable(false)
            .show()
    }

    private fun showErrorDialog(message: String) {
        AlertDialog.Builder(requireContext())
            .setTitle("Thêm mặt hàng mới THÀNH CÔNG")
            .setMessage(message)
            .setPositiveButton("OK") { dialog, _ -> dialog.dismiss() }
            .show()
    }
    private fun checkAndRequestCameraPermission() {

        when {
            // Android 6.0 trở lên: luôn cần quyền CAMERA
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.M -> {
                if (ContextCompat.checkSelfPermission(
                        requireContext(),
                        Manifest.permission.CAMERA
                    ) == PackageManager.PERMISSION_GRANTED
                ) {
                    openCameraWithPreview()
                } else {
                    cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
                }
            }

            // Dưới Android 6: quyền được cấp tự động khi cài đặt
            else -> {
                openCameraWithPreview()
            }
        }
    }
    private val cameraPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            openCameraWithPreview()
        } else {
            Toast.makeText(requireContext(), "Cần quyền camera để chụp ảnh", Toast.LENGTH_SHORT).show()
        }
    }
    private fun openCameraWithPreview() {
        cameraPreviewLauncher.launch(null)  // Mở camera
    }
    private val cameraPreviewLauncher = registerForActivityResult(ActivityResultContracts.TakePicturePreview()) { bitmap ->
        bitmap?.let {
            val uri = saveBitmapToTempFile(it)
            viewModel.sendImageToCloudinary(uri)
        } ?: run {
            Toast.makeText(requireContext(), "Chụp ảnh thất bại", Toast.LENGTH_SHORT).show()
        }
    }
    private fun saveBitmapToTempFile(bitmap: Bitmap): Uri {
        val file = File(requireContext().cacheDir, "preview_${System.currentTimeMillis()}.jpg")
        FileOutputStream(file).use { out ->
            bitmap.compress(Bitmap.CompressFormat.JPEG, 90, out)
        }
        return FileProvider.getUriForFile(
            requireContext(),
            "com.example.scanner.fileprovider",
            file
        )
    }




}