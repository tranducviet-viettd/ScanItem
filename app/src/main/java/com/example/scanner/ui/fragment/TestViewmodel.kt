package com.example.scanner.ui.fragment

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class TestViewModel : ViewModel() {
    private val _barcode = MutableLiveData<String?>()
    val barcode: LiveData<String?> get() = _barcode

    fun updateBarcode(newBarcode: String) {
        _barcode.value = newBarcode
    }
}