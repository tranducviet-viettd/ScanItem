package com.example.scanner.ui.fragment.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.scanner.data.db.Result
import com.example.scanner.data.db.entity.UserInfo
import com.example.scanner.data.db.repository.DatabaseRepository

class HomeViewModel : ViewModel() {
    private val _barcode = MutableLiveData<String?>()
    val barcode: LiveData<String?> get() = _barcode

    val dbRepository = DatabaseRepository()

    fun updateBarcode(newBarcode: String) {
        _barcode.value = newBarcode
    }


}