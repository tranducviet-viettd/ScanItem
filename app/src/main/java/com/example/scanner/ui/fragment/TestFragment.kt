package com.example.scanner.ui.fragment


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResultListener
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.scanner.databinding.FragmentTestBinding
import com.example.scanner.R

class TestFragment : Fragment() {

    private var _binding: FragmentTestBinding? = null
    private val binding get() = _binding!!
    private val viewModel: TestViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTestBinding.inflate(inflater, container, false)
        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Nhận kết quả từ ScanFragment
        setFragmentResultListener("SCAN_RESULT") { _, bundle ->
            val barcode = bundle.getString("BARCODE")
            if (barcode != null) {
                viewModel.updateBarcode(barcode)
            }
        }

        // Xử lý click nút Add
        binding.AddButton.setOnClickListener {
            findNavController().navigate(R.id.action_navigation_test_to_scanFragment)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}