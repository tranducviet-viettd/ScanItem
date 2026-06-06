package com.example.scanner.ui.fragment.show_info_item

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.scanner.data.db.Result
import com.example.scanner.data.db.entity.Item
import com.example.scanner.data.db.repository.DatabaseRepository
import com.example.scanner.ui.fragment.DefaultViewModel



class ShowInfoItemViewModelFactory() : ViewModelProvider.Factory{
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return ShowInfoItemViewModel() as T
    }
}

class ShowInfoItemViewModel() : DefaultViewModel(){

    private val _infoItem = MutableLiveData<Item>()
    val infoItem: LiveData<Item> = _infoItem


    private val _pieceText = MutableLiveData<String>()
    val pieceText: LiveData<String> = _pieceText

    private val _packText = MutableLiveData<String>()
    val packText: LiveData<String> = _packText

    private val _boxText = MutableLiveData<String>()
    val boxText: LiveData<String> = _boxText


    private val databaseRepository = DatabaseRepository()

    fun loadInfoItem(itemID:String){
        databaseRepository.loadItem(itemID){ result ->
            if (result is Result.Success){
                Log.d("ScanItem","2 : ${result.data}")
                result.data?.let{showInfoItem(it)}
            }
        }
    }
    fun showInfoItem(item: Item) {
        _infoItem.value = item
        _pieceText.value = item.quantity.piece.toString()
        _packText.value = item.quantity.pack.toString()
        _boxText.value = item.quantity.box.toString()
    }



}