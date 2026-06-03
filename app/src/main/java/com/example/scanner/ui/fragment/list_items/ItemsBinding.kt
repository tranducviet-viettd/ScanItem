package com.example.scanner.ui.fragment.list_items

import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.scanner.data.db.entity.Item

@BindingAdapter("bind_items_list")
fun bindItemsList(recyclerView: RecyclerView,listItems:List<Item>?){
    listItems?.let { (recyclerView.adapter as ItemsListAdapter).submitList(it) }
}