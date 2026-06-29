package com.example.scanner.ui.fragment.home

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.setFragmentResultListener
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.scanner.R
import com.example.scanner.databinding.FragmentHomeBinding
import com.example.scanner.util.fragmentDestinationFlag
import com.example.scanner.ui.fragment.main.MainViewModel

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private val viewModel: HomeViewModel by viewModels()
    private val mainViewModel: MainViewModel by activityViewModels() // Đã chuyển ra đây

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        Log.d("doi","1")
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d("doi","2")
        // Observer displayName đặt ở đây, 1 lần duy nhất
        mainViewModel.displayName.observe(viewLifecycleOwner) { name ->
            Log.d("doi","3")
            binding.displayName.text = name
        }
        Log.d("doi","4")
        mainViewModel.isOwner.observe(viewLifecycleOwner) { isOwner ->
            Log.d("doi","5")
            when (isOwner) {
                true -> {
                    // CHỦ CỬA HÀNG → Hiện tất cả
                    Log.d("doi","6")
                    binding.AddButton.visibility = View.VISIBLE
                    binding.scanToPayButton.visibility = View.VISIBLE
                    binding.login.visibility = View.GONE
                    binding.createAccount.visibility = View.GONE
                    binding.displayName.text = "admin"
                }
                false -> {
                    Log.d("doi","7")
                    // KHÁCH ĐÃ ĐĂNG NHẬP → Ẩn chức năng chủ
                    binding.AddButton.visibility = View.GONE
                    binding.scanToPayButton.visibility = View.GONE
                    binding.login.visibility = View.GONE
                    binding.createAccount.visibility = View.GONE
                    // displayName tự cập nhật qua observer ở trên
                }
                null -> {
                    Log.d("doi","8")
                    // CHƯA ĐĂNG NHẬP
                    binding.AddButton.visibility = View.GONE
                    binding.scanToPayButton.visibility = View.GONE
                    binding.login.visibility = View.VISIBLE
                    binding.createAccount.visibility = View.VISIBLE
                }
            }
        }
        // Xử lý click nút Add
        binding.AddButton.setOnClickListener {
            fragmentDestinationFlag.flag = 1
            findNavController().navigate(R.id.action_navigation_home_to_scanFragment)
        }
        binding.ShowInfoItemButton.setOnClickListener {
            fragmentDestinationFlag.flag = 0
            findNavController().navigate(R.id.action_navigation_home_to_scanFragment)
        }
        binding.ListItemsButton.setOnClickListener {
            findNavController().navigate(R.id.action_navigation_home_to_listItemsFragment)
        }
        binding.storeMapButton.setOnClickListener {
            findNavController().navigate(R.id.action_navigation_home_to_storeMapFragment)
        }
        binding.scanToPayButton.setOnClickListener {
            findNavController().navigate(R.id.action_navigation_home_to_scanToPayFragment)
        }
        binding.login.setOnClickListener {
            findNavController().navigate(R.id.action_navigation_home_to_loginFragment)
        }
        binding.createAccount.setOnClickListener {
            findNavController().navigate(R.id.action_navigation_home_to_createAccountFragment )
        }
        binding.signout.setOnClickListener {
            mainViewModel.singout()
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}