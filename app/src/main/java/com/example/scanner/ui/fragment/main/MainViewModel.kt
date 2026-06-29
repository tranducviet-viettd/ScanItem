package com.example.scanner.ui.fragment.main

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.scanner.data.db.Result
import com.example.scanner.data.db.remote.FirebaseAuthStateObserver
import com.example.scanner.data.db.repository.AuthRepository
import com.example.scanner.data.db.repository.DatabaseRepository
import com.google.firebase.auth.FirebaseAuth

class MainViewModel : ViewModel(){

    companion object {
        var isCreatingAccountFlow = false
    }

    private val dbRepository = DatabaseRepository()

    private val authRepository = AuthRepository()
    private val fbAuthObserver= FirebaseAuthStateObserver()
    val isOwner = MutableLiveData<Boolean?>(null)

    val displayName = MutableLiveData<String>("")

    init {
        Log.d("MainViewModel", "ViewModel initialized, current user: ${FirebaseAuth.getInstance().currentUser}")
        setupAuthObserver()
    }
    override fun onCleared() {
        super.onCleared()
        fbAuthObserver.clear()  // ← Đừng quên cái này!
    }

    fun forceCheckUser(currentUid: String) {
               // ← Cập nhật LiveData
        dbRepository.isCustomer(currentUid) { isCustomer ->  // ← Dùng biến local
            isOwner.postValue(!isCustomer)
            if (isCustomer) {  // Nếu là khách thì mới cần load tên
                dbRepository.loadUserInfo(currentUid) { result ->
                    if (result is Result.Success) {
                        displayName.postValue(result.data?.displayName ?: "")
                    }
                }
            }
        }
    }

    private fun setupAuthObserver(){
        Log.d("MainViewModel", "begin")
            authRepository.observeAuthState(fbAuthObserver){ result ->
            if (isCreatingAccountFlow) {
                Log.d("MainViewModel", "Bỏ qua AuthObserver vì đang tạo tài khoản")
                return@observeAuthState
            }
                Log.d("MainViewModel", "Callback triggered with result: $result")
            if(result is Result.Success) {
                Log.d("MainViewModel", "auth:${result.data}")
                val currentUid = result.data!!.uid  // ← Lưu vào biến local
                forceCheckUser(currentUid)
            }
            else{
                Log.d("doi","21")
                Log.d("MainViewModel", "fail")
                isOwner.postValue(null)
            }

        }
        Log.d("MainViewModel", "Observer registered")
    }
    fun singout (){
        authRepository.logoutUser()
        displayName.postValue("")
    }
}