package com.example.scanner.ui.fragment.login

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.scanner.data.db.Event
import com.example.scanner.data.db.entity.User
import com.example.scanner.data.db.model.Login
import com.example.scanner.data.db.Result
import com.example.scanner.data.db.repository.AuthRepository
import com.example.scanner.data.db.repository.DatabaseRepository
import com.example.scanner.ui.fragment.DefaultViewModel
import com.example.scanner.util.isEmailValid
import com.example.scanner.util.isTextValid
import com.google.firebase.auth.FirebaseUser

class LoginViewModel : DefaultViewModel() {

    private val authRepository = AuthRepository()

    private val _isLoggedInEvent = MutableLiveData<Event<FirebaseUser>>()

    val isLoggedInEvent : LiveData<Event<FirebaseUser>> = _isLoggedInEvent
    val isLoggingIn = MutableLiveData<Boolean>()
    val emailText = MutableLiveData<String>()
    val passwordText = MutableLiveData<String>()

    val listUsers = MutableLiveData<MutableList<User>>()
    private lateinit var userCheck : User


    init {
        Log.d("CheckLifecycle", "LoginViewModel: LoginViewModel created")
    }

    override fun onCleared() {
        super.onCleared()
        Log.d("CheckLifecycle", "LoginViewModel: LoginViewModel onCleared called")
    }


    fun login(){
        isLoggingIn.value=true
        val login = Login(emailText.value!!, passwordText.value!!)

        authRepository.loginUser(login){
                result ->
            onResult(null,result)
            if(result is Result.Success) _isLoggedInEvent.value=Event(result.data!!)

            if(result is Result.Error || result is Result.Success){
                isLoggingIn.value=false

            }
        }

    }


    fun loginPressed(){
        if(!isEmailValid(emailText.value.toString())){
            mSnackBarText.value = Event("Invalid Email Format")
            return
        }
        if(!isTextValid(6,passwordText.value.toString())){
            mSnackBarText.value = Event("Password too short")
            return
        }

        login()
    }


}