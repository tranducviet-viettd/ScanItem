package com.example.scanner.ui.fragment.add_item

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.scanner.ui.fragment.DefaultViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import com.example.scanner.data.db.Result
import com.example.scanner.data.db.entity.Item
import com.example.scanner.data.db.entity.Price
import com.example.scanner.data.db.entity.Quantity
import com.example.scanner.data.db.repository.DatabaseRepository
import com.example.scanner.data.db.repository.StorageRepository
import com.example.scanner.util.convertFileToByteArray
import com.example.scanner.util.generateShortItemID

class AddItemViewModelFactory(private val context: Context) : ViewModelProvider.Factory{
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return AddItemViewModel(context) as T
    }
}

class AddItemViewModel(private val context: Context

                        ) : DefaultViewModel() {

    private val storageRepository = StorageRepository()
    private val databaseRepository = DatabaseRepository()
    private val _itemImageUrl = MutableLiveData<String>("")   // giá trị mặc định là rỗng
    val itemImageUrl: LiveData<String> = _itemImageUrl

    val nameEditText = MutableLiveData<String>("")
    val priceEditText = MutableLiveData<Price>(Price("0","0","0"))
    val codeEditText = MutableLiveData<String>("")

    private val _pieceEditText = MutableLiveData<String>("0")
    val pieceEditText : MutableLiveData<String> = _pieceEditText

    private val _packEditText = MutableLiveData<String>("0")
    val packEditText : MutableLiveData<String> = _packEditText

    private val _boxEditText = MutableLiveData<String>("0")
    val boxEditText : MutableLiveData<String> = _boxEditText


    private val _addItemResult = MutableLiveData<Result<String>>()
    val addItemResult : LiveData<Result<String>> = _addItemResult

    init {

    }

    override fun onCleared() {
        super.onCleared()

    }
    fun addItem(){
        if(nameEditText.value.isNullOrBlank() || priceEditText.value!!.piece.isBlank() || priceEditText.value!!.pack.isBlank() || priceEditText.value!!.box.isBlank()||codeEditText.value.isNullOrBlank()){
            _addItemResult.value = Result.Error("Không được để trống ")
            return
        }
        val newItem = Item(id = generateShortItemID(),nameEditText.value!!,priceEditText.value!!,codeEditText.value!!,itemImageUrl.value!!,
            Quantity(pieceEditText.value!!.toInt(),packEditText.value!!.toInt(),boxEditText.value!!.toInt()))
        databaseRepository.addItem(codeEditText.value!!,newItem){result ->
            if (result is Result.Success){
                _addItemResult.value= result
            }
        }
    }
    fun sendImageToCloudinary(uri: Uri) {
        onResult(null, Result.Loading)
        CoroutineScope(Dispatchers.IO).launch {
            try {
                // Chuyển đổi Uri thành ByteArray
                val byteArray = convertFileToByteArray(context, uri)
                storageRepository.uploadItemImage(byteArray) { result ->
                    when (result) {
                        is Result.Success -> {
                            Log.d("AddItem", "0:${result.data.toString()}")
                            _itemImageUrl.value=result.data.toString()
                            Log.d("AddItem", "1:${_itemImageUrl.value}")
                            onResult(null,Result.Success(null))

                        }

                        is Result.Error -> {

                        }

                        is Result.Loading -> {
                            // Có thể cập nhật UI để hiển thị trạng thái loading
                        }
                    }
                }
            } catch (e: Exception) {

            } finally {

            }
        }
    }
    fun attachValueToCodeText(string: String){
        codeEditText.value = string
    }

    fun increasePiece() {
        val current = _pieceEditText.value?.toIntOrNull() ?: 1
        _pieceEditText.value = (current + 1).toString()
    }

    // Hàm giảm
    fun decreasePiece() {
        val current = _pieceEditText.value?.toIntOrNull() ?: 1
        if (current >= 1) {
            _pieceEditText.value = (current - 1).toString()
        }
    }
    fun increasePack() {
        val current = _packEditText.value?.toIntOrNull() ?: 1
        _packEditText.value = (current + 1).toString()
    }

    // Hàm giảm
    fun decreasePack() {
        val current = _packEditText.value?.toIntOrNull() ?: 1
        if (current >= 1) {
            _packEditText.value = (current - 1).toString()
        }
    }
    fun increaseBox() {
        val current = _boxEditText.value?.toIntOrNull() ?: 1
        _boxEditText.value = (current + 1).toString()
    }

    // Hàm giảm
    fun decreaseBox() {
        val current = _boxEditText.value?.toIntOrNull() ?: 1
        if (current >= 1) {
            _boxEditText.value = (current - 1).toString()
        }
    }
}