package com.example.scanner.ui.fragment.list_items

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.scanner.R
import com.example.scanner.data.db.EventObserver
import com.example.scanner.data.db.entity.Item
import com.example.scanner.databinding.FragmentListItemsBinding
import com.example.scanner.ui.fragment.show_info_item.ShowInfoItemFragment
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.getValue

class ItemsFragment: Fragment() {
    private val viewModel : ItemsViewModel by viewModels { ItemsViewModelFactory() }
    private lateinit var viewDataBinding : FragmentListItemsBinding
    private lateinit var viewAdapter: ItemsListAdapter
    private var searchJob: Job? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        Log.d("CheckLifecycle", "UserFragment: onCreateView called - Fragment creating View")

        viewDataBinding= FragmentListItemsBinding.inflate(inflater,container,false).apply { viewmodel = viewModel }
        viewDataBinding.lifecycleOwner=this.viewLifecycleOwner
        return viewDataBinding.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupListAdapter()
        setupObserver()
        viewModel.showFullItems()
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
    }

    private fun setupListAdapter(){
        val viewModel= viewDataBinding.viewmodel
        if(viewModel!=null) {  viewAdapter= ItemsListAdapter(viewModel)
            viewDataBinding.itemsRecyclerView.adapter=viewAdapter }
    }
    private fun setupObserver(){

        viewModel.selectItemEvent.observe(viewLifecycleOwner, EventObserver {
            nagivateToShowInfo(it.code)
        })

        viewDataBinding.searchItemEditText.addTextChangedListener(object : TextWatcher{
            override fun beforeTextChanged(
                p0: CharSequence?, p1: Int,p2: Int, p3: Int
            ) {

            }

            override fun onTextChanged(
                p0: CharSequence?, p1: Int, p2: Int, p3: Int
            ) {

            }

            override fun afterTextChanged(s: Editable?) {
                searchJob?.cancel()
                searchJob = lifecycleScope.launch {
                    delay(400) // Đợi 400ms sau khi ngừng gõ
                    val query = s?.toString()?.trim() ?: ""
                    if (query.isBlank()) {
                        viewModel.showFullItems()
                    } else {
                        viewModel.searchItem(query)
                    }
                }
            }
        })

    }

    private fun nagivateToShowInfo(itemCode : String){
        val bundle= bundleOf(ShowInfoItemFragment.ARGS_KEY_ITEM_CODE to itemCode)
        findNavController().navigate(R.id.action_listItemFragment_to_showInfoItemFragment,bundle)
    }

    override fun onStart() {
        super.onStart()
        }

    override fun onResume() {
        super.onResume()
       }

    override fun onPause() {
        super.onPause()
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





}