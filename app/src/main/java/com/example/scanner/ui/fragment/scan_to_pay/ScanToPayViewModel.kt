package com.example.scanner.ui.fragment.scan_to_pay

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.scanner.data.db.Result
import com.example.scanner.data.db.entity.Item
import com.example.scanner.data.db.repository.DatabaseRepository
import com.example.scanner.ui.fragment.DefaultViewModel
import com.example.scanner.ui.fragment.list_items.ItemsViewModel

class ScanToPayViewModelFactory(): ViewModelProvider.Factory{
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return ScanToPayViewModel() as T
    }
}

class ScanToPayViewModel() : DefaultViewModel(){
    private val databaseRepository= DatabaseRepository()

    private val _itemInfo = MutableLiveData<Item>()

    private var _totalAmount = MutableLiveData<String>("0")
    val totalAmount : LiveData<String> = _totalAmount

    private var savedTotal: Int = 0

    val itemInfo : LiveData<Item> = _itemInfo

    val boxEditText = MutableLiveData<String>("0")
    val packEditText = MutableLiveData<String>("0")
    val pieceEditText = MutableLiveData<String>("0")


    fun loadInfoItem(itemID:String){
        savedTotal = _totalAmount.value!!.toInt()
        boxEditText.value = "0"
        packEditText.value = "0"
        pieceEditText.value = "0"

        databaseRepository.loadItem(itemID){ result ->
            if (result is Result.Success){
                Log.d("ScanItem","2 : ${result.data}")
                result.data?.let{
                    _itemInfo.postValue(it)
                }
            }
        }
    }

    private fun calculateCurrentItemPrice(): Int {
        val box = boxEditText.value?.toIntOrNull() ?: 0
        val pack = packEditText.value?.toIntOrNull() ?: 0
        val piece = pieceEditText.value?.toIntOrNull() ?: 0

        val itemPrice = _itemInfo.value?.price ?: return 0  // Nếu chưa có data thì trả về 0, không crash
        val boxPrice = itemPrice.box.toIntOrNull() ?: 0     // toIntOrNull thay vì toInt
        val packPrice = itemPrice.pack.toIntOrNull() ?: 0
        val piecePrice = itemPrice.piece.toIntOrNull() ?: 0
        return (box * boxPrice) + (pack * packPrice) + (piece * piecePrice)}

    fun updateMoneys() {
        val currentPrice = calculateCurrentItemPrice()
        // Tổng tiền = Tiền các món trước + Tiền món hiện tại
        _totalAmount.value = (savedTotal + currentPrice).toString()
    }

}