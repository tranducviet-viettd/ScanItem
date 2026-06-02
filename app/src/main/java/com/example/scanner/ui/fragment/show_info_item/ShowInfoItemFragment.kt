package com.example.scanner.ui.fragment.show_info_item

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResultListener
import androidx.fragment.app.viewModels
import com.example.scanner.databinding.FragmentAddItemBinding
import com.example.scanner.databinding.FragmentShowInfoItemBinding
import com.example.scanner.ui.fragment.add_item.AddItemViewModel
import com.example.scanner.ui.fragment.add_item.AddItemViewModelFactory


class ShowInfoItemFragment : Fragment(){

    private lateinit var viewDataBinding: FragmentShowInfoItemBinding

    private val viewModel: ShowInfoItemViewModel by viewModels {
        ShowInfoItemViewModelFactory(
        )}

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewDataBinding= FragmentShowInfoItemBinding.inflate(inflater,container,false).apply { viewmodel = viewModel }
        viewDataBinding.lifecycleOwner = this.viewLifecycleOwner

        return viewDataBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setFragmentResultListener("SCAN_RESULT") { _, bundle ->
            val barcode = bundle.getString("BARCODE")
            if (barcode != null) {
                Log.d("ScanItem","1: ${barcode}")
                viewModel.loadInfoItem(barcode)
            }
        }
        viewModel.infoItem.observe(viewLifecycleOwner) { item ->
            if (item != null) {
                // Bind dữ liệu lên UI
                Log.d("ScanItem","3: ${item}")
                viewDataBinding.itemNameText.text = item.name
                viewDataBinding.itemPriceText.text = "${item.price} đ"
                // ... bind các field khác
            } else {
                Toast.makeText(requireContext(), "Không tìm thấy sản phẩm", Toast.LENGTH_SHORT).show()
            }
        }
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
    }


}