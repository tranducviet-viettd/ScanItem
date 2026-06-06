package com.example.scanner.ui.fragment.list_items

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.scanner.data.db.Event
import com.example.scanner.data.db.Result
import com.example.scanner.data.db.entity.Item
import com.example.scanner.data.db.repository.DatabaseRepository
import com.example.scanner.ui.fragment.DefaultViewModel


class ItemsViewModelFactory(): ViewModelProvider.Factory{
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return ItemsViewModel() as T
    }
}

class ItemsViewModel (): DefaultViewModel(){

    private val databaseRepository= DatabaseRepository()
    val itemsList = MediatorLiveData<List<Item>>()
    private val updateItemsList= MutableLiveData<MutableList<Item>>()

    private val _selectItemEvent = MutableLiveData<Event<Item>>()
    val selectItemEvent : LiveData<Event<Item>> = _selectItemEvent

    init {
        itemsList.addSource(updateItemsList){ newItem ->
            itemsList.value= updateItemsList.value
        }

    }
    fun showFullItems(){
        databaseRepository.loadItems { result ->
            if (result is Result.Success){
                updateItemsList.value = result.data?.toMutableList()
            }
        }
    }
    fun searchItem(query : String){
        databaseRepository.loadItems{ result ->
            if(result is Result.Success){
                updateItemsList.value = result.data?.asSequence()?.filter { item ->
                    item.name!!.contains(query,ignoreCase = true)
                }?.toMutableList()
            }
        }
    }
    fun selectItem(item: Item){
        _selectItemEvent.value=Event(item)
    }




}