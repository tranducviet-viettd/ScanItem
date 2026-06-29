package com.example.scanner.ui.fragment.login

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.scanner.data.db.EventObserver
import com.example.scanner.databinding.FragmentLoginBinding
import com.example.scanner.ui.fragment.main.MainActivity
import com.example.scanner.util.forceHideKeyboard
import com.example.scanner.util.showSnackBar
import kotlin.getValue

class LoginFragment: Fragment() {

    private val viewModel by viewModels<LoginViewModel>()
    private lateinit var viewDataBinding: FragmentLoginBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        Log.d("CheckLifecycle", "LoginFragment: onCreateView called - Fragment creating View")

        viewDataBinding=FragmentLoginBinding.inflate(inflater,container,false).apply { viewmodel = viewModel }
        viewDataBinding.lifecycleOwner=this.viewLifecycleOwner
        setHasOptionsMenu(true)
        return viewDataBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpObservers()
    }
    private fun setUpObservers(){
        viewModel.dataLoading.observe(viewLifecycleOwner,
            EventObserver { (activity as MainActivity).showGlobalProgressBar(it) })
        viewModel.snackBarText.observe(viewLifecycleOwner,EventObserver{ text ->
            view?.showSnackBar(text)
            view?.forceHideKeyboard()

        })

        viewModel.isLoggedInEvent.observe(viewLifecycleOwner,EventObserver{
            findNavController().popBackStack()
        })


    }


}