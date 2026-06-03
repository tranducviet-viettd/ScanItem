package com.example.scanner.ui.fragment.list_items

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import com.example.scanner.data.db.entity.Item
import com.example.scanner.databinding.ListCardItemsBinding

class ItemsListAdapter internal constructor(private val viewModel: ItemsViewModel): ListAdapter<(Item), ItemsListAdapter.ViewHolder>(ItemDiffCallBack()){

    class ViewHolder(private val binding: ListCardItemsBinding): RecyclerView.ViewHolder(binding.root){


        fun bind(viewModel: ItemsViewModel, item: Item){
            binding.viewmodel=viewModel
            binding.item=item
            binding.executePendingBindings()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ListCardItemsBinding.inflate(inflater,parent,false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            holder.bind(viewModel,getItem(position))
    }

class ItemDiffCallBack : DiffUtil.ItemCallback<Item>(){
    override fun areItemsTheSame(
        oldItem: Item,
        newItem: Item
    ): Boolean {
        return oldItem == newItem
    }

    override fun areContentsTheSame(
        oldItem: Item,
        newItem: Item
    ): Boolean {
        return oldItem.code==newItem.code
    }
}




}