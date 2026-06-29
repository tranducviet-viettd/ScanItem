package com.example.scanner.ui.fragment.create_account

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.scanner.data.db.Event
import com.example.scanner.data.db.Result
import com.example.scanner.data.db.entity.User
import com.example.scanner.data.db.model.CreateUser
import com.example.scanner.data.db.repository.AuthRepository
import com.example.scanner.data.db.repository.DatabaseRepository
import com.example.scanner.ui.fragment.DefaultViewModel
import com.example.scanner.util.isEmailValid
import com.example.scanner.util.isTextValid
import com.google.firebase.auth.FirebaseUser

class CreateAccountViewModel : DefaultViewModel(){

    private val authRepository = AuthRepository()
    private val dbRepository = DatabaseRepository()

    val emailText = MutableLiveData<String>()
    val passwordText = MutableLiveData<String>()
    val displayNameText = MutableLiveData<String>()

    private val _isCreatedEvent = MutableLiveData<Event<FirebaseUser>>()
    val isCreatedEvent : LiveData<Event<FirebaseUser>> =_isCreatedEvent
    val isCreatingAccount = MutableLiveData<Boolean>()


    init {
        Log.d("CheckLifecycle", "CreateViewModel: ChatViewModel created")
    }

    override fun onCleared() {
        super.onCleared()
        Log.d("CheckLifecycle", "CreateViewModel: ChatsViewModel onCleared called")
    }

    private fun createAccount(){
        Log.d("doi","37")
        com.example.scanner.ui.fragment.main.MainViewModel.isCreatingAccountFlow = true // Chặn AuthObserver

        val createAccount = CreateUser(
            displayNameText.value.toString(),
            emailText.value.toString(),
            passwordText.value.toString()
        )

        authRepository.createUser(createAccount){
                result ->
            Log.d("doi","41")
            onResult(null,result)
            if(result is Result.Success){
                Log.d("doi","42")
                dbRepository.updateNewUser(User().apply {
                    info.id=result.data!!.uid
                    info.displayName=createAccount.displayName
                    info.email=createAccount.email
                }){ result1 ->
                    if(result1 is Result.Success){
                        com.example.scanner.ui.fragment.main.MainViewModel.isCreatingAccountFlow = false // Trả lại bình thường
                        _isCreatedEvent.value = Event(result.data!!)
                    }
                }
                Log.d("doi","45")

            }
            if(result is Result.Success || result is Result.Error){
                isCreatingAccount.value=false
                }
        }


    }

    fun createAccountPressed(){
        Log.d("doi","36")
        if(!isEmailValid(emailText.value.toString())){
            mSnackBarText.value= Event("Invalid Email Format")
            return
        }
        if(!isTextValid(6,passwordText.value.toString())){
            mSnackBarText.value=Event("Password too short")
            return
        }
        createAccount()

    }


}