package com.example.scanner.ui.fragment.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResultListener
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.scanner.R
import com.example.scanner.databinding.FragmentHomeBinding
import com.example.scanner.util.fragmentDestinationFlag

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private val viewModel: HomeViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


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
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}