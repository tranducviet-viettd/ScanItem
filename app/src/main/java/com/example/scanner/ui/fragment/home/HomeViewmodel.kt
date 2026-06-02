package com.example.scanner.ui.fragment.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class HomeViewModel : ViewModel() {
    private val _barcode = MutableLiveData<String?>()
    val barcode: LiveData<String?> get() = _barcode

    fun updateBarcode(newBarcode: String) {
        _barcode.value = newBarcode
    }
}