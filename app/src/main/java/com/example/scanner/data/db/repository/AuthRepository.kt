package com.example.scanner.data.db.repository


import android.util.Log
import com.example.scanner.data.db.remote.FirebaseAuthSource
import com.example.scanner.data.db.remote.FirebaseAuthStateObserver
import com.example.scanner.data.db.Result
import com.example.scanner.data.db.model.CreateUser
import com.example.scanner.data.db.model.Login
import com.google.firebase.auth.FirebaseUser

class AuthRepository {

    private val firebaseAuthService= FirebaseAuthSource()

    fun observeAuthState(firebaseAuthStateObserver: FirebaseAuthStateObserver, b: ((Result<FirebaseUser>) -> Unit)){
        Log.d("doi","11")
        Log.d("AuthRepository", "Starting to observe auth state")
        firebaseAuthService.attackAuthStateObserver(firebaseAuthStateObserver, b)
        Log.d("AuthRepository", "Auth state observer setup completed")
    }

    fun loginUser(login: Login,b : ((Result<FirebaseUser>) -> Unit)){
        b.invoke(Result.Loading)
        firebaseAuthService.loginWithEmailAndPassword(login).addOnSuccessListener {
            Log.d("AuthRepository", "Test login success, user: ${it.user}")
            b.invoke(Result.Success(it.user))
        }.addOnFailureListener {
            b.invoke(Result.Error(it.message))
        }

    }

    fun createUser(createUser: CreateUser,b : ((Result<FirebaseUser>) -> Unit)){
        Log.d("doi","38")
        b.invoke(Result.Loading)
        firebaseAuthService.createUser(createUser).addOnSuccessListener {
            Log.d("doi","40")
            b.invoke(Result.Success(it.user))
        }.addOnFailureListener {
            b.invoke(Result.Error(it.message))
        }

    }

    fun logoutUser(){
        firebaseAuthService.logout()
    }


}