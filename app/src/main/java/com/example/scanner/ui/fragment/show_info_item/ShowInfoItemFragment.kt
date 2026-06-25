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
import com.example.scanner.data.db.entity.Item

class ShowInfoItemFragment : Fragment(){
    companion object {
        const val ARGS_KEY_ITEM_CODE = "item_key"
    }
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
        val itemCode = arguments?.getString(ARGS_KEY_ITEM_CODE)
        if (itemCode != null) {
                viewModel.loadInfoItem(itemCode)           // Truyền trực tiếp Item
            }
        // Fallback: Nhận barcode từ ScanFragment
        else {
            setFragmentResultListener("SCAN_RESULT") { _, bundle ->
                val barcode = bundle.getString("BARCODE")
                barcode?.let { viewModel.loadInfoItem(it) }
            }
        }
        viewModel.infoItem.observe(viewLifecycleOwner) { item ->
            if (item != null) {
                // Bind dữ liệu lên UI
                Log.d("ScanItem","3: ${item}")
                viewDataBinding.itemNameText.text = item.name
                viewDataBinding.itemPriceText.text = "${item.price.piece} đ"
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