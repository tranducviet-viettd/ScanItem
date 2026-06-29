package com.example.scanner.ui.fragment.create_account

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.scanner.data.db.EventObserver
import com.example.scanner.databinding.FragmentCreateAccountBinding
import com.example.scanner.ui.fragment.main.MainActivity
import com.example.scanner.ui.fragment.main.MainViewModel
import com.example.scanner.util.forceHideKeyboard
import com.example.scanner.util.showSnackBar
import kotlin.getValue

class CreateAccountFragment: Fragment(){
    private val viewModel by viewModels<CreateAccountViewModel>()
    private lateinit var viewDataBinding: FragmentCreateAccountBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        Log.d("CheckLifecycle", "CreateFragment: onCreateView called - Fragment creating View")

        viewDataBinding=FragmentCreateAccountBinding.inflate(inflater,container,false).apply { viewmodel=viewModel }
        viewDataBinding.lifecycleOwner=this.viewLifecycleOwner
        setHasOptionsMenu(true)
        return viewDataBinding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupObserver()
    }

    private val mainViewModel: MainViewModel by activityViewModels()

    private fun setupObserver(){
        viewModel.dataLoading.observe(viewLifecycleOwner, EventObserver {
            (activity as MainActivity).showGlobalProgressBar(it)
        })
        viewModel.snackBarText.observe(viewLifecycleOwner,EventObserver{
            view?.showSnackBar(it)
            view?.forceHideKeyboard()
        })
        viewModel.isCreatedEvent.observe(viewLifecycleOwner,EventObserver{
            Log.d("doi","46")
            mainViewModel.forceCheckUser(it.uid) // Ép MainViewModel kiểm tra sau khi lưu DB xong
            findNavController().popBackStack()
        })
    }
}