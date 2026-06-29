package com.example.scanner.ui.fragment

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.scanner.data.db.Event
import com.example.scanner.data.db.Result

abstract class DefaultViewModel :ViewModel(){

    protected val mSnackBarText= MutableLiveData<Event<String>>()
    val snackBarText : LiveData<Event<String>> = mSnackBarText

    protected val mDataLoading=MutableLiveData<Event<Boolean>>()
    val dataLoading: LiveData<Event<Boolean>> = mDataLoading

    protected fun <T> onResult(mutableLiveData: MutableLiveData<T>? = null, result: Result<T>){
        when(result){
            is Result.Success -> {
                mDataLoading.value=Event(false)
                result.data?.let { mutableLiveData?.value=it }
            }

            is Result.Error -> {
                mDataLoading.value = Event(false)

            }
            is Result.Loading -> mDataLoading.value = Event(true)
        }
    }

}